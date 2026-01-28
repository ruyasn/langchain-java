package dev.agentplatform.mcp;

import dev.agentplatform.common.model.mcp.McpToolInfo;

import java.util.List;
import java.util.Map;

/**
 * MCP 网关：对接 Model Context Protocol，暴露工具列表与执行
 */
public interface McpGateway {

    /**
     * 列出所有可用 MCP 工具（含本地插件转 MCP 格式）
     */
    List<McpToolInfo> listTools();

    /**
     * 执行指定 MCP 工具
     */
    Object callTool(String toolName, Map<String, Object> arguments);
}
