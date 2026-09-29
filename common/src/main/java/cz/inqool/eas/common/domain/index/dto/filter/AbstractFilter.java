package cz.inqool.eas.common.domain.index.dto.filter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import cz.inqool.eas.common.domain.index.dto.filter.AbstractFilter.Fields;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;

import static cz.inqool.eas.common.domain.index.dto.filter.FilterOperation.*;

/**
 * ElasticSearch filter.
 * <p>
 * Note that all instances of this class can be deserialized and therefore can be used both by front-end and back-end.
 */
@JsonDeserialize // to override parent annotation
@JsonTypeInfo(
        use = Id.NAME,
        include = As.EXISTING_PROPERTY,
        property = Fields.operation,
        visible = true)
@JsonSubTypes({
        @Type(name = AND,             value = AndFilter.class),
        @Type(name = AKF,             value = AnyKeywordFieldFilter.class),
        @Type(name = CONTAINS,        value = ContainsFilter.class),
        @Type(name = CUSTOM,          value = CustomFilter.class),
        @Type(name = END_WITH,        value = EndWithFilter.class),
        @Type(name = EQ,              value = EqFilter.class),
        @Type(name = EQF,             value = EqFoldedFilter.class),
        @Type(name = FTX,             value = FulltextFilter.class),
        @Type(name = FTXF,            value = FulltextFieldFilter.class),
        @Type(name = GEO_BOUNDING_BOX,value = GeoBoundingBoxFilter.class),
        @Type(name = GEO_DISTANCE,    value = GeoDistanceFilter.class),
        @Type(name = GEO_POLYGON,     value = GeoPolygonFilter.class),
        @Type(name = GT,              value = GtFilter.class),
        @Type(name = GTE,             value = GteFilter.class),
        @Type(name = RANGE,           value = RangeFilter.class),
        @Type(name = IDS,             value = IdsFilter.class),
        @Type(name = IN,              value = InFilter.class),
        @Type(name = IS_NULL,         value = NullFilter.class),
        @Type(name = LT,              value = LtFilter.class),
        @Type(name = LTE,             value = LteFilter.class),
        @Type(name = NESTED,          value = NestedFilter.class),
        @Type(name = NOT,             value = NotFilter.class),
        @Type(name = NOT_NULL,        value = NotNullFilter.class),
        @Type(name = OR,              value = OrFilter.class),
        @Type(name = QUERY_STRING,    value = QueryStringFilter.class),
        @Type(name = START_WITH,      value = StartWithFilter.class)
})
@Getter
@EqualsAndHashCode
@FieldNameConstants
abstract public class AbstractFilter implements Filter {

    /**
     * Filter operation
     */
    @NotNull
    @Schema(description = "Filter operation")
    private final String operation;


    protected AbstractFilter(@NotNull String operation) {
        this.operation = operation;
    }
}
