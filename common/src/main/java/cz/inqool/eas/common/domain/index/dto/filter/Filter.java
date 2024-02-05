package cz.inqool.eas.common.domain.index.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import org.elasticsearch.index.query.QueryBuilder;

import static cz.inqool.eas.common.domain.index.dto.filter.FilterOperation.*;

/**
 * Represents a filter condition for searching.
 */
@JsonDeserialize(using = FilterDeserializer.class)
@JsonIgnoreProperties // to disable ignore_unknown deserialization feature
@Schema(
        description = "Filter",
        oneOf = {
                AndFilter.class,
                AnyKeywordFieldFilter.class,
                ContainsFilter.class,
                CustomFilter.class,
                EndWithFilter.class,
                EqFilter.class,
                EqFoldedFilter.class,
                FulltextFilter.class,
                FulltextFieldFilter.class,
                GeoBoundingBoxFilter.class,
                GeoDistanceFilter.class,
                GeoPolygonFilter.class,
                GtFilter.class,
                GteFilter.class,
                RangeFilter.class,
                IdsFilter.class,
                InFilter.class,
                NullFilter.class,
                LtFilter.class,
                LteFilter.class,
                NestedFilter.class,
                NotFilter.class,
                NotNullFilter.class,
                OrFilter.class,
                QueryStringFilter.class,
                StartWithFilter.class
        },
        discriminatorProperty = AbstractFilter.Fields.operation,
        discriminatorMapping = {
                @DiscriminatorMapping(value = AND,              schema = AndFilter.class),
                @DiscriminatorMapping(value = AKF,              schema = AnyKeywordFieldFilter.class),
                @DiscriminatorMapping(value = CONTAINS,         schema = ContainsFilter.class),
                @DiscriminatorMapping(value = CUSTOM,           schema = CustomFilter.class),
                @DiscriminatorMapping(value = END_WITH,         schema = EndWithFilter.class),
                @DiscriminatorMapping(value = EQ,               schema = EqFilter.class),
                @DiscriminatorMapping(value = EQF,              schema = EqFoldedFilter.class),
                @DiscriminatorMapping(value = FTX,              schema = FulltextFilter.class),
                @DiscriminatorMapping(value = FTXF,             schema = FulltextFieldFilter.class),
                @DiscriminatorMapping(value = GEO_BOUNDING_BOX, schema = GeoBoundingBoxFilter.class),
                @DiscriminatorMapping(value = GEO_DISTANCE,     schema = GeoDistanceFilter.class),
                @DiscriminatorMapping(value = GEO_POLYGON,      schema = GeoPolygonFilter.class),
                @DiscriminatorMapping(value = GT,               schema = GtFilter.class),
                @DiscriminatorMapping(value = GTE,              schema = GteFilter.class),
                @DiscriminatorMapping(value = RANGE,            schema = RangeFilter.class),
                @DiscriminatorMapping(value = IDS,              schema = IdsFilter.class),
                @DiscriminatorMapping(value = IN,               schema = InFilter.class),
                @DiscriminatorMapping(value = IS_NULL,          schema = NullFilter.class),
                @DiscriminatorMapping(value = LT,               schema = LtFilter.class),
                @DiscriminatorMapping(value = LTE,              schema = LteFilter.class),
                @DiscriminatorMapping(value = NESTED,           schema = NestedFilter.class),
                @DiscriminatorMapping(value = NOT,              schema = NotFilter.class),
                @DiscriminatorMapping(value = NOT_NULL,         schema = NotNullFilter.class),
                @DiscriminatorMapping(value = OR,               schema = OrFilter.class),
                @DiscriminatorMapping(value = QUERY_STRING,     schema = QueryStringFilter.class),
                @DiscriminatorMapping(value = START_WITH,       schema = StartWithFilter.class)
        }
)
public interface Filter {

    /**
     * Returns an elastic search query builder representing the filter condition.
     *
     * @param indexedFields supported indexed fields
     * @return created query builder
     */
    QueryBuilder toQueryBuilder(IndexObjectFields indexedFields);
}
