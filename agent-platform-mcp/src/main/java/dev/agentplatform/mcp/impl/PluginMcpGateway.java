package dev.agentplatform.mcp.impl;

import dev.agentplatform.common.model.mcp.McpToolInfo;
import dev.agentplatform.common.model.plugin.PluginMetadata;
import dev.agentplatform.common.model.plugin.PluginToolDef;
import dev.agentplatform.mcp.McpGateway;
import dev.agentplatform.plugin.PluginRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于插件注册中心的 MCP 网关：将平台插件以 MCP 工具形式暴露
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PluginMcpGateway implements McpGateway {

    private final PluginRegistry pluginRegistry;

    @Override
    public List<McpToolInfo> listTools() {
        List<McpToolInfo> result = new ArrayList<>();
        for (PluginMetadata plugin : pluginRegistry.listPlugins()) {
            if (plugin.getTools() != null) {
                for (PluginToolDef tool : plugin.getTools()) {
                    Map<String, Object> inputSchema = new HashMap<>();
                    if (tool.getParameters() != null) {
                        Map<String, Object> props = new HashMap<>();
                        for (Map.Entry<String, String> e : tool.getParameters().entrySet()) {
                            props.put(e.getKey(), Map.of("type", "string", "description", e.getValue()));
                        }
                        inputSchema.put("properties", props);
                    }
                    result.add(McpToolInfo.builder()
                            .name(plugin.getId() + "." + tool.getName())
                            .description(tool.getDescription())
                            .inputSchema(inputSchema)
                            .build());
                }
            }
        }
        return result;
    }

    @Override
    public Object callTool(String toolName, Map<String, Object> arguments) {
        int dot = toolName.indexOf('.');
        if (dot <= 0) {
            throw new IllegalArgumentException("Tool name must be pluginId.toolName: " + toolName);
        }
        String pluginId = toolName.substring(0, dot);
        String name = toolName.substring(dot + 1);
        return pluginRegistry.execute(pluginId, name, arguments != null ? arguments : Map.of());
    }
}
