package dev.agentplatform.agent;

import dev.agentplatform.common.model.agent.AgentDefinition;

import java.util.List;
import java.util.Map;

/**
 * Agent 服务：基于 LangChain4j 的对话与执行
 */
public interface AgentService {

    /**
     * 注册/更新 Agent 定义
     */
    void registerAgent(AgentDefinition definition);

    /**
     * 根据 ID 获取 Agent 定义
     */
    AgentDefinition getAgent(String agentId);

    /**
     * 列出所有 Agent
     */
    List<AgentDefinition> listAgents();

    /**
     * 与 Agent 对话（支持多轮，通过 sessionId 区分）
     */
    String chat(String agentId, String sessionId, String userMessage);

    /**
     * 执行 Agent（单次，可带上下文参数）
     */
    String execute(String agentId, Map<String, Object> context);
}
