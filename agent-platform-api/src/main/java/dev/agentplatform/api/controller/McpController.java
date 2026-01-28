package dev.agentplatform.api.controller;

import dev.agentplatform.common.model.Result;
import dev.agentplatform.common.model.mcp.McpToolInfo;
import dev.agentplatform.mcp.McpGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * MCP（Model Context Protocol）REST API：工具列表与调用
 */
@RestController
@RequestMapping("/api/mcp")
@RequiredArgsConstructor
public class McpController {

    private final McpGateway mcpGateway;

    @GetMapping("/tools")
    public Result<List<McpToolInfo>> listTools() {
        return Result.ok(mcpGateway.listTools());
    }

    @PostMapping("/tools/{toolName}/call")
    public Result<Object> callTool(
            @PathVariable String toolName,
            @RequestBody(required = false) Map<String, Object> arguments) {
        Object result = mcpGateway.callTool(toolName, arguments != null ? arguments : Map.of());
        return Result.ok(result);
    }
}
