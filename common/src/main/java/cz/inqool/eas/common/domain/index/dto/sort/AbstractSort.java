package cz.inqool.eas.common.domain.index.dto.sort;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import javax.validation.constraints.NotNull;

import static cz.inqool.eas.common.domain.index.dto.sort.Sort.Type.*;

/**
 * ElasticSearch sorting specification.
 */
@JsonDeserialize // to override parent annotation
@JsonTypeInfo(
        use = Id.NAME,
        include = As.EXISTING_PROPERTY,
        property = "type",
        visible = true,
        defaultImpl = FieldSort.class) // todo remove defaultImpl after FE is adjusted to changes
@JsonSubTypes({
        @Type(name = FIELD,        value = FieldSort.class),
        @Type(name = GEO_DISTANCE, value = GeoDistanceSort.class),
        @Type(name = SCRIPT,       value = ScriptSort.class),
        @Type(name = SCORE,        value = ScoreSort.class)
})
@Getter
@Setter
@EqualsAndHashCode
@FieldNameConstants
public abstract class AbstractSort<SB extends SortBuilder<SB>> implements Sort<SB> {

    /**
     * Order of sorting.
     */
    @NotNull
    @Schema(description = "Order of sorting.")
    protected SortOrder order;


    protected AbstractSort() {
    }

    protected AbstractSort(@NotNull SortOrder order) {
        this.order = order;
    }


    /**
     * Returns the type of sort
     *
     * @see Sort.Type
     */
    public abstract String getType();

    @JsonIgnore
    protected SortOrder getReversedOrder() {
        return (order == SortOrder.ASC) ? SortOrder.DESC : SortOrder.ASC;
    }
}
