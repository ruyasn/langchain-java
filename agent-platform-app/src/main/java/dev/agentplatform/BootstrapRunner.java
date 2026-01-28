package dev.agentplatform;

import dev.agentplatform.agent.AgentService;
import dev.agentplatform.common.model.agent.AgentDefinition;
import dev.agentplatform.common.model.workflow.WorkflowDefinition;
import dev.agentplatform.common.model.workflow.WorkflowEdge;
import dev.agentplatform.common.model.workflow.WorkflowNode;
import dev.agentplatform.workflow.WorkflowEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 启动时注册默认 Agent 与工作流示例
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class BootstrapRunner {

    private final AgentService agentService;
    private final WorkflowEngine workflowEngine;

    @Bean
    public ApplicationRunner bootstrap() {
        return args -> {
            AgentDefinition defaultAgent = AgentDefinition.builder()
                    .id("default-agent")
                    .name("Default Assistant")
                    .description("Default LangChain4j agent")
                    .systemPrompt("You are a helpful assistant. Reply concisely.")
                    .modelName("gpt-3.5-turbo")
                    .build();
            agentService.registerAgent(defaultAgent);

            WorkflowNode startNode = WorkflowNode.builder().id("n1").name("Start").type(WorkflowNode.NodeType.START).build();
            WorkflowNode agentNode = WorkflowNode.builder().id("n2").name("Agent").type(WorkflowNode.NodeType.AGENT).refId("default-agent").build();
            WorkflowNode endNode = WorkflowNode.builder().id("n3").name("End").type(WorkflowNode.NodeType.END).build();
            WorkflowDefinition sampleWorkflow = WorkflowDefinition.builder()
                    .id("sample-workflow")
                    .name("Sample Workflow")
                    .description("Start -> Agent -> End")
                    .nodes(List.of(startNode, agentNode, endNode))
                    .edges(List.of(
                            WorkflowEdge.builder().id("e1").sourceNodeId("n1").targetNodeId("n2").build(),
                            WorkflowEdge.builder().id("e2").sourceNodeId("n2").targetNodeId("n3").build()
                    ))
                    .build();
            workflowEngine.registerWorkflow(sampleWorkflow);

            log.info("Bootstrap: default-agent and sample-workflow registered.");
        };
    }
}
