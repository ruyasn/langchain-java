package dev.agentplatform.api.controller;

import dev.agentplatform.agent.AgentService;
import dev.agentplatform.common.model.Result;
import dev.agentplatform.common.model.agent.AgentDefinition;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Agent REST API
 */
@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @PostMapping
    public Result<AgentDefinition> register(@RequestBody @Valid AgentDefinition definition) {
        agentService.registerAgent(definition);
        return Result.ok(definition);
    }

    @GetMapping("/{agentId}")
    public Result<AgentDefinition> get(@PathVariable String agentId) {
        AgentDefinition def = agentService.getAgent(agentId);
        if (def == null) return Result.fail(404, "Agent not found");
        return Result.ok(def);
    }

    @GetMapping
    public Result<List<AgentDefinition>> list() {
        return Result.ok(agentService.listAgents());
    }

    @PostMapping("/{agentId}/chat")
    public Result<String> chat(
            @PathVariable String agentId,
            @RequestParam(required = false, defaultValue = "default") String sessionId,
            @RequestBody Map<String, String> body) {
        String message = body != null ? body.get("message") : null;
        if (message == null) return Result.fail(400, "message required");
        String reply = agentService.chat(agentId, sessionId, message);
        return Result.ok(reply);
    }

    @PostMapping("/{agentId}/execute")
    public Result<String> execute(@PathVariable String agentId, @RequestBody Map<String, Object> context) {
        String out = agentService.execute(agentId, context);
        return Result.ok(out);
    }
}
