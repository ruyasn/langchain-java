package dev.agentplatform.common.model.mcp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * MCP 工具信息（与 Model Context Protocol 对齐）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpToolInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Map<String, Object> inputSchema;
}
