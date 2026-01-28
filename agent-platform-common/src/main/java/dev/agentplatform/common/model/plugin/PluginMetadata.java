package dev.agentplatform.common.model.plugin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 插件元数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PluginMetadata implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String version;
    private String description;
    private String author;
    private List<PluginToolDef> tools;
}
