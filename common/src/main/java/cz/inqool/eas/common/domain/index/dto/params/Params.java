package cz.inqool.eas.common.domain.index.dto.params;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.inqool.eas.common.domain.index.DomainIndex;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.aggregation.Aggregation;
import cz.inqool.eas.common.domain.index.dto.filter.CustomFilter;
import cz.inqool.eas.common.domain.index.dto.filter.Filter;
import cz.inqool.eas.common.domain.index.dto.filter.LogicalFilter;
import cz.inqool.eas.common.domain.index.dto.filter.custom.CustomFilterModel;
import cz.inqool.eas.common.domain.index.dto.filter.custom.CustomFilterSpecificParameters;
import cz.inqool.eas.common.domain.index.dto.sort.Sort;
import cz.inqool.eas.common.utils.JsonUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.utils.JsonUtils.fromJsonStringParametrized;

/**
 * Data transfer object for specification of filtering, sorting and paging.
 */
@EqualsAndHashCode
@Getter
@Setter
@Schema
public class Params {

    /**
     * Allows to define sorting on multiple values.
     */
    @Valid
    protected List<@NotNull Sort<?>> sort = new ArrayList<>();

    /**
     * Allows to set an offset for returned instances.
     * <p>
     * If {@link #searchAfter} is present, this value is ignored.
     * <p>
     * Note that {@link #offset} + {@link #size} must not be greater than 10 000 (see <a
     * href="https://www.elastic.co/guide/en/elasticsearch/reference/6.8/search-request-from-size.html">ElasticSearch
     * From/Size</a>), so use {@link #searchAfter} instead of this in case you need to traverse more items.
     */
    @Min(-1)
    protected Integer offset;

    /**
     * Number of requested instances.
     * <p>
     * If {@code null} or {@code -1} then paging is disabled and all items are returned (depending on {@link
     * DomainIndex#ELASTIC_SIZE_LIMIT}).
     * <p>
     * Note that {@link #offset} + {@link #size} must not be greater than 10 000 (see <a
     * href="https://www.elastic.co/guide/en/elasticsearch/reference/6.8/search-request-from-size.html">ElasticSearch
     * From/Size</a>), so use {@link #searchAfter} instead of this in case you need to traverse more items.
     */
    @SuppressWarnings("JavadocReference")
    @NotNull
    @Min(-1)
    protected Integer size = 10;

    /**
     * Infinity scroll support. Contains last result's sorting field values (in correct order). The values should
     * uniquely identify one particular result to avoid duplicate results returned.
     * <p>
     * These values are returned in {@link Result#getSearchAfter()} property. Overrides {@link #offset} property.
     */
    @Schema(description = "Infinity scroll support. Contains last result's sorting field values (in correct order). " +
            "These values are returned in 'searchAfter' property of response result. Overrides 'offset' property.")
    @JsonSerialize(using = SearchAfterSerializer.class)
    @JsonDeserialize(using = SearchAfterDeserializer.class)
    protected Object[] searchAfter;

    /**
     * Flip direction of infinite scroll.
     */
    @NotNull
    @Schema(description = "Flip direction of infinite scroll.")
    protected Boolean flipDirection = false;

    /**
     * Filter conditions.
     */
    @Valid
    protected List<@NotNull Filter> filters = new ArrayList<>();

    /**
     * Aggregations across data to be gathered in addition to results
     */
    @Valid
    protected List<@NotNull Aggregation> aggregations = new ArrayList<>();

    /**
     * Specifies additional stored fields to be returned from indexed data in ElasticSearch
     */
    @Schema(description = "Specifies additional stored fields to be returned from indexed data in ElasticSearch")
    protected List<@NotBlank String> fields;

    /**
     * Modifies the search to return items of specific state
     */
    @Schema(description = "Modifies the search to return items of specific state.")
    protected Set<@NotNull String> include = new LinkedHashSet<>();

    /**
     * Modifies the search to not return items of specific state
     */
    @Schema(description = "Modifies the search to not return items of specific state.")
    protected Set<@NotNull String> exclude = new LinkedHashSet<>();


    public void setFields(String... fields) {
        setFields(List.of(fields));
    }

    @JsonSetter
    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    /**
     * Converts all {@link CustomFilter}s to corresponding logical filter and inserts them into {@link #filters}
     *
     * @param specificSubClass custom filter specific parameter type
     * @param <T>              type of specific subclass
     */
    public <T extends CustomFilterSpecificParameters> void processCustomFilters(Class<T> specificSubClass) {
        List<LogicalFilter> customLogicalFilters = this.filters.stream()
                .filter(filter -> filter instanceof CustomFilter)
                .map(filter -> ((CustomFilter) filter).getCustomJsonModel())
                .map(jsonString -> fromJsonStringParametrized(jsonString, CustomFilterModel.class, specificSubClass).processSpecificParams())
                .collect(Collectors.toList());
        this.filters.addAll(customLogicalFilters);
    }

    /**
     * Add given filters to filter list
     */
    public void addFilter(@NotNull Filter... filters) {
        this.filters.addAll(List.of(filters));
    }

    /**
     * Add given sortings to sorting list
     */
    public void addSort(@NotNull Sort<?>... sortings) {
        this.sort.addAll(List.of(sortings));
    }

    @SneakyThrows
    public Params copy() {
        return JsonUtils.convert(this, Params.class);
    }
}
