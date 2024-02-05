package cz.inqool.eas.common.domain.index.dto.filter;

import cz.inqool.eas.common.domain.index.field.IndexFieldGeoPointLeafNode;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-polygon-query.html">Geo-polygon query</a>
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class GeoPolygonFilter extends GeoFilter<GeoPolygonFilter> {

    @NotEmpty
    private List<GeoPoint> points;


    GeoPolygonFilter() {
        super(FilterOperation.GEO_POLYGON);
    }

    public GeoPolygonFilter(@NotNull String field, @NotEmpty List<GeoPoint> points) {
        super(FilterOperation.GEO_POLYGON, field);
        this.points = points;
    }


    @Override
    public QueryBuilder toQueryBuilder(IndexObjectFields indexedFields) {
        IndexFieldGeoPointLeafNode leafNode = indexedFields.get(field, IndexFieldGeoPointLeafNode.class);

        return QueryBuilders.geoPolygonQuery(leafNode.getElasticSearchPath(), points);
    }
}
