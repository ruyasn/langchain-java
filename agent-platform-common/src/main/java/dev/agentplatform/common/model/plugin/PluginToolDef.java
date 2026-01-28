package dev.agentplatform.common.model.plugin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 插件工具定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PluginToolDef implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Map<String, String> parameters;  // name -> description
}
