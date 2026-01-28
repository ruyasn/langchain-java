package dev.agentplatform.workflow;

import dev.agentplatform.common.model.workflow.WorkflowDefinition;

import java.util.Map;

/**
 * 工作流引擎：执行工作流定义
 */
public interface WorkflowEngine {

    /**
     * 注册工作流定义
     */
    void registerWorkflow(WorkflowDefinition definition);

    /**
     * 获取工作流定义
     */
    WorkflowDefinition getWorkflow(String workflowId);

    /**
     * 执行工作流
     * @param workflowId 工作流 ID
     * @param input 输入参数
     * @return 执行结果（各节点输出汇总）
     */
    Map<String, Object> run(String workflowId, Map<String, Object> input);
}
