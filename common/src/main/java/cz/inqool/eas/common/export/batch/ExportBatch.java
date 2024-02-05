package cz.inqool.eas.common.export.batch;

import cz.inqool.eas.common.authored.store.AuthoredObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.common.export.request.ExportRequest;
import cz.inqool.eas.common.storage.file.File;
import cz.inqool.entityviews.Viewable;
import cz.inqool.entityviews.ViewableMapping;
import cz.inqool.entityviews.ViewableProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

import static cz.inqool.eas.common.dictionary.DictionaryViews.LABELED;
import static cz.inqool.eas.common.domain.DomainViews.*;

/**
 * Export request batch.
 */
@Viewable
@DomainViews
@Getter
@Setter
@BatchSize(size = 100)
@Entity
@Table(name = "eas_export_batch")
public class ExportBatch extends AuthoredObject<ExportBatch> {
    /**
     * Name of the batch.
     */
    protected String name;

    /**
     * State of the request
     */
    @ViewableProperty(views = {DETAIL, LIST})
    @Enumerated(EnumType.STRING)
    protected ExportBatchState state;

    /**
     * Export requests in batch.
     */
    @ViewableProperty(views = {DETAIL, CREATE, UPDATE})
    @ViewableMapping(views = {DETAIL}, mappedTo = DETAIL)
    @ViewableMapping(views = {CREATE, UPDATE}, useRef = true)
    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "eas_export_batch_requests",
            joinColumns = @JoinColumn(name = "batch_id"),
            inverseJoinColumns = @JoinColumn(name = "request_id"))
    @BatchSize(size = 100)
    protected Set<ExportRequest> requests = new LinkedHashSet<>();

    @ViewableProperty(views = DETAIL)
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    protected File result;
}
