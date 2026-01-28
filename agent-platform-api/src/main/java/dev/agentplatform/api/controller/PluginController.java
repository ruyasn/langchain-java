package dev.agentplatform.api.controller;

import dev.agentplatform.common.model.Result;
import dev.agentplatform.common.model.plugin.PluginMetadata;
import dev.agentplatform.plugin.PluginRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 插件 REST API
 */
@RestController
@RequestMapping("/api/plugins")
@RequiredArgsConstructor
public class PluginController {

    private final PluginRegistry pluginRegistry;

    @GetMapping
    public Result<List<PluginMetadata>> list() {
        return Result.ok(pluginRegistry.listPlugins());
    }

    @GetMapping("/{pluginId}")
    public Result<PluginMetadata> get(@PathVariable String pluginId) {
        return pluginRegistry.getPlugin(pluginId)
                .map(p -> Result.<PluginMetadata>ok(p.metadata()))
                .orElse(Result.fail(404, "Plugin not found"));
    }

    @PostMapping("/{pluginId}/execute")
    public Result<Object> execute(
            @PathVariable String pluginId,
            @RequestParam String toolName,
            @RequestBody(required = false) Map<String, Object> params) {
        Object result = pluginRegistry.execute(pluginId, toolName, params != null ? params : Map.of());
        return Result.ok(result);
    }
}
