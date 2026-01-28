package dev.agentplatform.plugin;

import dev.agentplatform.common.model.plugin.PluginMetadata;

import java.util.List;
import java.util.Optional;

/**
 * 插件注册中心：注册、查询、执行插件
 */
public interface PluginRegistry {

    /**
     * 注册插件
     */
    void register(Plugin plugin);

    /**
     * 根据 ID 获取插件
     */
    Optional<Plugin> getPlugin(String pluginId);

    /**
     * 列出所有插件元数据
     */
    List<PluginMetadata> listPlugins();

    /**
     * 执行指定插件的工具
     */
    Object execute(String pluginId, String toolName, java.util.Map<String, Object> params);
}
