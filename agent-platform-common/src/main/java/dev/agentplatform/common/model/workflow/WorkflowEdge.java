package dev.agentplatform.common.model.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 工作流边（节点连接）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowEdge implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String sourceNodeId;
    private String targetNodeId;
    private String condition;  // 条件分支时使用
}
