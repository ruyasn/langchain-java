package dev.agentplatform.agent.impl;

import dev.agentplatform.agent.AgentService;
import dev.agentplatform.common.model.agent.AgentDefinition;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.output.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认 Agent 服务实现：使用 LangChain4j ChatLanguageModel + ChatMemory
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultAgentService implements AgentService {

    private final ChatLanguageModel chatModel;
    private final Map<String, AgentDefinition> agents = new ConcurrentHashMap<>();
    private final Map<String, ChatMemory> sessionMemories = new ConcurrentHashMap<>();

    private static final int MEMORY_WINDOW_SIZE = 20;

    @Override
    public void registerAgent(AgentDefinition definition) {
        agents.put(definition.getId(), definition);
        log.info("Registered agent: {}", definition.getId());
    }

    @Override
    public AgentDefinition getAgent(String agentId) {
        return agents.get(agentId);
    }

    @Override
    public List<AgentDefinition> listAgents() {
        return List.copyOf(agents.values());
    }

    @Override
    public String chat(String agentId, String sessionId, String userMessage) {
        AgentDefinition def = agents.get(agentId);
        if (def == null) {
            throw new IllegalArgumentException("Agent not found: " + agentId);
        }
        ChatMemory memory = sessionMemories.computeIfAbsent(sessionKey(agentId, sessionId),
                k -> MessageWindowChatMemory.withMaxMessages(MEMORY_WINDOW_SIZE));
        String systemPrompt = def.getSystemPrompt() != null ? def.getSystemPrompt() : "You are a helpful assistant.";
        PromptTemplate template = PromptTemplate.from("""
                {{systemPrompt}}
                ---
                {{userMessage}}
                """);
        Map<String, Object> vars = new HashMap<>();
        vars.put("systemPrompt", systemPrompt);
        vars.put("userMessage", userMessage);
        memory.add(UserMessage.from(userMessage));
        Response<dev.langchain4j.data.message.AiMessage> response = chatModel.generate(memory.messages());
        String aiText = response.content().text();
        memory.add(response.content());
        return aiText;
    }

    @Override
    public String execute(String agentId, Map<String, Object> context) {
        AgentDefinition def = agents.get(agentId);
        if (def == null) {
            throw new IllegalArgumentException("Agent not found: " + agentId);
        }
        String systemPrompt = def.getSystemPrompt() != null ? def.getSystemPrompt() : "You are a helpful assistant.";
        String task = context != null && context.containsKey("task")
                ? String.valueOf(context.get("task"))
                : "Please respond briefly.";
        String prompt = systemPrompt + "\n\nTask: " + task;
        return chatModel.generate(prompt);
    }

    private static String sessionKey(String agentId, String sessionId) {
        return agentId + ":" + sessionId;
    }
}
