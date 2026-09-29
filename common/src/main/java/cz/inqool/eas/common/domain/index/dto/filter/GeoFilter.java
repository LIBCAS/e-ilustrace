package cz.inqool.eas.common.domain.index.dto.filter;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/geo-queries.html">Geo queries</a>
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class GeoFilter<FILTER extends GeoFilter<FILTER>> extends FieldFilter<FILTER> {

    protected GeoFilter(@NotNull String operation) {
        super(operation);
    }

    protected GeoFilter(@NotNull String operation, @NotBlank String field) {
        super(operation, field);
    }
}
