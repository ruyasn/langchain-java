package dev.agentplatform.plugin.config;

import dev.agentplatform.plugin.Plugin;
import dev.agentplatform.plugin.PluginRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 插件模块配置：自动注册所有 Plugin Bean
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class PluginConfig {

    private final PluginRegistry registry;
    private final List<Plugin> pluginBeans;

    @Bean
    public ApplicationRunner pluginRegistrationRunner() {
        return args -> pluginBeans.forEach(registry::register);
    }
}
