package cz.inqool.eas.common.reporting.report;

import cz.inqool.eas.common.authored.store.AuthoredObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.common.reporting.convert.ReportColumnsConverter;
import cz.inqool.eas.common.reporting.convert.ReportDataConverter;
import cz.inqool.eas.common.reporting.convert.ReportInputConverter;
import cz.inqool.eas.common.reporting.dto.ReportDefinition;
import cz.inqool.entityviews.Viewable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

/**
 * Generated report.
 */
@Viewable
@DomainViews
@Getter
@Setter
@Entity
@Table(name = "eas_reporting_report")
@BatchSize(size = 100)
public class Report extends AuthoredObject<Report> {
    /**
     * Id of the {@link ReportDefinition}.
     */
    protected String definitionId;

    /**
     * Serialized input.
     */
    @Convert(converter = ReportInputConverter.class)
    protected Map<String, Object> configuration;

    /**
     * Serialized data.
     */
    @Convert(converter = ReportDataConverter.class)
    protected List<Map<String, Object>> data;

    @Convert(converter = ReportColumnsConverter.class)
    protected List<ReportColumn> columns;
}
