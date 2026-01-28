package dev.agentplatform.common.model.agent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Agent 定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentDefinition implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String description;
    private String systemPrompt;
    private String modelName;
    private List<String> toolIds;
    private List<String> knowledgeBaseIds;
    private Map<String, Object> config;
}
