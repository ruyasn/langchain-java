package dev.agentplatform.workflow.impl;

import dev.agentplatform.agent.AgentService;
import dev.agentplatform.common.model.workflow.WorkflowDefinition;
import dev.agentplatform.common.model.workflow.WorkflowEdge;
import dev.agentplatform.common.model.workflow.WorkflowNode;
import dev.agentplatform.workflow.WorkflowEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认工作流引擎：按 DAG 顺序执行节点，支持 START -> AGENT/TOOL -> END
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultWorkflowEngine implements WorkflowEngine {

    private final AgentService agentService;
    private final Map<String, WorkflowDefinition> workflows = new ConcurrentHashMap<>();

    @Override
    public void registerWorkflow(WorkflowDefinition definition) {
        workflows.put(definition.getId(), definition);
        log.info("Registered workflow: {}", definition.getId());
    }

    @Override
    public WorkflowDefinition getWorkflow(String workflowId) {
        return workflows.get(workflowId);
    }

    @Override
    public Map<String, Object> run(String workflowId, Map<String, Object> input) {
        WorkflowDefinition wf = workflows.get(workflowId);
        if (wf == null) {
            throw new IllegalArgumentException("Workflow not found: " + workflowId);
        }
        Map<String, Object> context = new HashMap<>(input != null ? input : Map.of());
        Map<String, Object> nodeOutputs = new HashMap<>();

        WorkflowNode start = wf.getNodes().stream()
                .filter(n -> n.getType() == WorkflowNode.NodeType.START)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Workflow has no START node"));

        List<String> order = topologicalOrder(wf);
        String lastOutput = null;
        for (String nodeId : order) {
            WorkflowNode node = findNode(wf, nodeId);
            if (node == null) continue;
            if (node.getType() == WorkflowNode.NodeType.START) {
                nodeOutputs.put(nodeId, context);
                continue;
            }
            if (node.getType() == WorkflowNode.NodeType.END) {
                context.put("lastOutput", lastOutput);
                nodeOutputs.put(nodeId, context);
                break;
            }
            if (node.getType() == WorkflowNode.NodeType.AGENT && node.getRefId() != null) {
                Map<String, Object> agentInput = new HashMap<>();
                agentInput.put("task", context.get("task"));
                String out = agentService.execute(node.getRefId(), agentInput);
                lastOutput = out;
                context.put("lastOutput", out);
                nodeOutputs.put(nodeId, Map.of("output", out));
            } else {
                nodeOutputs.put(nodeId, context);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("context", context);
        result.put("nodeOutputs", nodeOutputs);
        return result;
    }

    private List<String> topologicalOrder(WorkflowDefinition wf) {
        Map<String, List<String>> outEdges = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();
        for (WorkflowNode n : wf.getNodes()) {
            outEdges.put(n.getId(), new ArrayList<>());
            inDegree.put(n.getId(), 0);
        }
        for (WorkflowEdge e : wf.getEdges()) {
            outEdges.get(e.getSourceNodeId()).add(e.getTargetNodeId());
            inDegree.merge(e.getTargetNodeId(), 1, Integer::sum);
        }
        Queue<String> q = new LinkedList<>();
        for (Map.Entry<String, Integer> e : inDegree.entrySet()) {
            if (e.getValue() == 0) q.add(e.getKey());
        }
        List<String> order = new ArrayList<>();
        while (!q.isEmpty()) {
            String u = q.poll();
            order.add(u);
            for (String v : outEdges.get(u)) {
                inDegree.merge(v, -1, Integer::sum);
                if (inDegree.get(v) == 0) q.add(v);
            }
        }
        return order;
    }

    private WorkflowNode findNode(WorkflowDefinition wf, String nodeId) {
        return wf.getNodes().stream().filter(n -> n.getId().equals(nodeId)).findFirst().orElse(null);
    }
}
