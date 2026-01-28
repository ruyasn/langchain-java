package dev.agentplatform.plugin;

import dev.agentplatform.common.model.plugin.PluginMetadata;

import java.util.Map;

/**
 * 插件 SPI：平台加载并调用插件
 */
public interface Plugin {

    /**
     * 插件元数据（id、name、version、tools 等）
     */
    PluginMetadata metadata();

    /**
     * 执行插件工具
     * @param toolName 工具名称
     * @param params 参数
     * @return 执行结果（可序列化）
     */
    Object executeTool(String toolName, Map<String, Object> params);
}
