package dev.agentplatform.plugin.sample;

import dev.agentplatform.common.model.plugin.PluginMetadata;
import dev.agentplatform.common.model.plugin.PluginToolDef;
import dev.agentplatform.plugin.Plugin;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 示例插件：回显工具
 */
@Component
public class EchoPlugin implements Plugin {

    private static final PluginMetadata METADATA = PluginMetadata.builder()
            .id("echo")
            .name("Echo Plugin")
            .version("1.0.0")
            .description("Echoes input text")
            .author("Agent Platform")
            .tools(List.of(
                    PluginToolDef.builder()
                            .name("echo")
                            .description("Echo the given message")
                            .parameters(Map.of("message", "Text to echo"))
                            .build()
            ))
            .build();

    @Override
    public PluginMetadata metadata() {
        return METADATA;
    }

    @Override
    public Object executeTool(String toolName, Map<String, Object> params) {
        if ("echo".equals(toolName)) {
            Object msg = params.get("message");
            return "Echo: " + (msg != null ? msg.toString() : "");
        }
        throw new IllegalArgumentException("Unknown tool: " + toolName);
    }
}
