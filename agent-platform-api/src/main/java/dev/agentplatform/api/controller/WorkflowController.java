package dev.agentplatform.api.controller;

import dev.agentplatform.common.model.Result;
import dev.agentplatform.common.model.workflow.WorkflowDefinition;
import dev.agentplatform.workflow.WorkflowEngine;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 工作流 REST API
 */
@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowEngine workflowEngine;

    @PostMapping
    public Result<WorkflowDefinition> register(@RequestBody @Valid WorkflowDefinition definition) {
        workflowEngine.registerWorkflow(definition);
        return Result.ok(definition);
    }

    @GetMapping("/{workflowId}")
    public Result<WorkflowDefinition> get(@PathVariable String workflowId) {
        WorkflowDefinition def = workflowEngine.getWorkflow(workflowId);
        if (def == null) return Result.fail(404, "Workflow not found");
        return Result.ok(def);
    }

    @PostMapping("/{workflowId}/run")
    public Result<Map<String, Object>> run(@PathVariable String workflowId, @RequestBody Map<String, Object> input) {
        Map<String, Object> result = workflowEngine.run(workflowId, input);
        return Result.ok(result);
    }
}
