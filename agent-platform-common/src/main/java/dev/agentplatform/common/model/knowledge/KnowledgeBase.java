package dev.agentplatform.common.model.knowledge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * 知识库
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBase implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String description;
    private String embeddingModel;
    private Instant createdAt;
    private Instant updatedAt;
}
