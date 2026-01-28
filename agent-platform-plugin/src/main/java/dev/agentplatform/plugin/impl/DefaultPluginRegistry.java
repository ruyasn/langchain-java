package dev.agentplatform.plugin.impl;

import dev.agentplatform.common.model.plugin.PluginMetadata;
import dev.agentplatform.plugin.Plugin;
import dev.agentplatform.plugin.PluginRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认插件注册中心实现
 */
@Slf4j
@Service
public class DefaultPluginRegistry implements PluginRegistry {

    private final Map<String, Plugin> plugins = new ConcurrentHashMap<>();

    @Override
    public void register(Plugin plugin) {
        PluginMetadata meta = plugin.metadata();
        plugins.put(meta.getId(), plugin);
        log.info("Registered plugin: {} v{}", meta.getId(), meta.getVersion());
    }

    @Override
    public Optional<Plugin> getPlugin(String pluginId) {
        return Optional.ofNullable(plugins.get(pluginId));
    }

    @Override
    public List<PluginMetadata> listPlugins() {
        return plugins.values().stream().map(Plugin::metadata).toList();
    }

    @Override
    public Object execute(String pluginId, String toolName, Map<String, Object> params) {
        Plugin plugin = plugins.get(pluginId);
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin not found: " + pluginId);
        }
        return plugin.executeTool(toolName, params != null ? params : Map.of());
    }
}
