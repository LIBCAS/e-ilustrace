package cz.inqool.eas.common.export.init.dto;

import cz.inqool.eas.common.export.provider.ExportDataProvider;
import cz.inqool.eas.common.export.provider.ExportDesignProvider;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ExportTemplateDto {
    private String id;
    private boolean active;
    private Integer order;
    private String name;
    private String label;
    private ExportDataProvider dataProvider;
    private ExportDesignProvider designProvider;
    private String configuration;
    private Set<String> tags;

    private String contentFile;

    private boolean replace;
    private Set<String> allowedTypes;
}
