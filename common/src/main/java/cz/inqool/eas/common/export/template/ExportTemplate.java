package cz.inqool.eas.common.export.template;

import cz.inqool.eas.common.dictionary.store.DictionaryObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.common.export.provider.ExportDataProvider;
import cz.inqool.eas.common.export.provider.ExportDesignProvider;
import cz.inqool.eas.common.storage.file.File;
import cz.inqool.entityviews.Viewable;
import cz.inqool.entityviews.ViewableMapping;
import cz.inqool.entityviews.ViewableProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static cz.inqool.eas.common.domain.DomainViews.*;

/**
 * Template for export.
 */
@Viewable
@DomainViews
@Getter
@Setter
@Entity
@Table(name = "eas_export_template")
public class ExportTemplate extends DictionaryObject<ExportTemplate> {
    /**
     * Label used in export dialog.
     */
    @Nationalized
    protected String label;

    /**
     * File template.
     */
    @ViewableProperty(views = {CREATE, UPDATE, DETAIL})
    @ViewableMapping(views = {CREATE, UPDATE}, useRef = true)
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    protected File content;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected ExportDataProvider dataProvider;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected ExportDesignProvider designProvider;

    /**
     * Allowed export types to generate.
     */
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    @JoinTable(name = "eas_export_template_types", joinColumns = @JoinColumn(name = "export_template_id"))
    @Column(name = "type", nullable = false)
    protected Set<ExportType> allowedTypes;

    /**
     * Fixed params.
     */
    protected String configuration;

    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "eas_export_template_tag", joinColumns = @JoinColumn(name = "export_template_id"))
    @Column(name = "tag")
    protected Set<String> tags;

    /**
     * File name template.
     *
     * If null, the result will have the name of the {@link #name}.
     */
    protected String fileNameTemplate;
}
