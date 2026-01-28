package dev.agentplatform.common.model.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 工作流节点
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowNode implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum NodeType { START, AGENT, TOOL, CONDITION, END }

    private String id;
    private String name;
    private NodeType type;
    private String refId;  // agentId / toolId etc.
    private Map<String, Object> config;
}
