package dev.agentplatform.common.model.knowledge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文档分块（用于 RAG）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentChunk implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String knowledgeBaseId;
    private String content;
    private String source;
    private double[] embedding;
}
