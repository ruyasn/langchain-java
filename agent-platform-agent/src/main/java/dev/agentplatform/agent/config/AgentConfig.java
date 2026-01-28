package dev.agentplatform.agent.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Agent 模块配置：提供默认的 ChatLanguageModel（可被应用层覆盖）
 */
@Configuration
public class AgentConfig {

    @Bean
    @ConditionalOnMissingBean(ChatLanguageModel.class)
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .temperature(0.7)
                .timeout(Duration.ofSeconds(60))
                .build();
    }
}
