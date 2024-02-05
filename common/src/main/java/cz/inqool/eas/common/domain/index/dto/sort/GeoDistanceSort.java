package cz.inqool.eas.common.domain.index.dto.sort;

import cz.inqool.eas.common.domain.index.field.IndexFieldGeoPointLeafNode;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortMode;
import org.elasticsearch.search.sort.SortOrder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static cz.inqool.eas.common.domain.index.dto.sort.Sort.Type.GEO_DISTANCE;

/**
 * Geo Distance Sort
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/sort-search-results.html#geo-sorting">Geo Distance Sorting</a>
 */
@Schema(
        description = "Allows to define sorting by geo distance.",
        externalDocs = @ExternalDocumentation(
                description = "Geo Distance Sorting",
                url = "https://www.elastic.co/guide/en/elasticsearch/reference/current/sort-search-results.html#geo-sorting"
        )
)
@Getter
@Setter
public class GeoDistanceSort extends AbstractSort<GeoDistanceSortBuilder> {

    /**
     * Sort type
     */
    @NotNull
    @Schema(allowableValues = {GEO_DISTANCE})
    private final String type = GEO_DISTANCE;

    /**
     * Attribute name to sort on.
     */
    @NotBlank
    @Schema(description = "Attribute name to sort on.")
    protected String field;

    /**
     * Geo points.
     */
    @NotEmpty
    @Schema(description = "Geo points.")
    protected List<GeoPoint> points;

    /**
     * Configure how to compute the distance. Can either be {@code arc} (default), or {@code plane} (faster, but
     * inaccurate on long distances and close to the poles
     */
    @Schema(description = "Configure how to compute the distance.", defaultValue = "ARC")
    protected GeoDistance geoDistance = GeoDistance.ARC;

    /**
     * Configure what to do in case a field has several geo points. By default, the shortest distance is taken into
     * account when sorting in ascending order and the longest distance when sorting in descending order.
     */
    @Schema(description = "Configure what to do in case a field has several geo points.")
    protected SortMode sortMode;

    /**
     * The unit to use when computing sort values. The default is {@code m} (meters).
     */
    @NotNull
    @Schema(description = "Configure the unit to use when computing sort values.", defaultValue = "METERS")
    protected DistanceUnit unit = DistanceUnit.METERS;


    GeoDistanceSort() {
    }

    @Builder
    public GeoDistanceSort(@NotBlank String field, @NotEmpty List<GeoPoint> points, @NotNull SortOrder order,
                           GeoDistance geoDistance, @NotNull SortMode sortMode, @NotNull DistanceUnit unit) {
        super(order);
        this.field = field;
        this.points = points;
        this.geoDistance = geoDistance;
        this.sortMode = sortMode;
        this.unit = unit;
    }


    @Override
    public org.elasticsearch.search.sort.GeoDistanceSortBuilder toSortBuilder(IndexObjectFields indexedFields) {
        IndexFieldGeoPointLeafNode leafNode = indexedFields.get(field, IndexFieldGeoPointLeafNode.class);

        return SortBuilders
                .geoDistanceSort(leafNode.getElasticSearchPath(), points.toArray(GeoPoint[]::new))
                .order(order)
                .geoDistance(geoDistance)
                .sortMode(sortMode)
                .unit(unit);
    }

    @Override
    public Sort<org.elasticsearch.search.sort.GeoDistanceSortBuilder> withReversedOrder() {
        return new GeoDistanceSort(field, points, getReversedOrder(), geoDistance, sortMode, unit);
    }
}
