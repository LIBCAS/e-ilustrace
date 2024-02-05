package cz.inqool.eas.common.export.request;

import cz.inqool.eas.common.authored.store.AuthoredObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.common.export.template.ExportTemplate;
import cz.inqool.eas.common.export.template.ExportType;
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
import java.time.Instant;

import static cz.inqool.eas.common.domain.DomainViews.*;

/**
 * Export request.
 */
@Viewable
@DomainViews
@Getter
@Setter
@BatchSize(size = 100)
@Entity
@Table(name = "eas_export_request")
public class ExportRequest extends AuthoredObject<ExportRequest> {
    /**
     * Export template
     */
    @ViewableProperty(views = {CREATE, UPDATE, DETAIL})
    @ViewableMapping(views = {CREATE, UPDATE}, useRef = true)
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    protected ExportTemplate template;

    /**
     * User supplied parameters
     */
    protected String configuration;

    /**
     * Export type to generate
     */
    @Enumerated(EnumType.STRING)
    protected ExportType type;

    /**
     * Priority (the lower the value, the higher the priority)
     */
    protected Integer priority;

    @ViewableProperty(views = DETAIL)
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    protected File result;

    /**
     * State of the request
     */
    @ViewableProperty(views = {DETAIL, LIST})
    @Enumerated(EnumType.STRING)
    protected ExportRequestState state;

    /**
     * Message (e.g. error that occurred when processing this export request)
     */
    @ViewableProperty(views = DETAIL)
    @Nationalized
    protected String message;

    /**
     * Time of start of generation of the corresponding export file
     */
    @ViewableProperty(views = {DETAIL, LIST})
    protected Instant processingStart;

    /**
     * Time of completion of generation of the corresponding export file
     */
    @ViewableProperty(views = {DETAIL, LIST})
    protected Instant processingEnd;

    @ViewableProperty
    protected byte[] securityContext;

    /**
     * System generated request.
     *
     * Will be processed immediately outside the queue.
     */
    @ViewableProperty(views = {CREATE, DETAIL, LIST})
    protected boolean systemRequest;
}
