package cz.inqool.eas.common.domain.index.filter;

import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject.IndexFields;
import cz.inqool.eas.common.domain.index.dto.filter.Filter;
import cz.inqool.eas.common.domain.index.dto.filter.GeoDistanceFilter;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.exception.v2.IndexException;
import cz.inqool.eas.common.utils.JsonUtils;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeoDistanceFilterTest extends IndexFilterTestBase {

    @Test
    void serialize() {
        Filter filter = new GeoDistanceFilter(IndexFields.coordinates, new GeoPoint(21.2130969, 49.1852093), 10, DistanceUnit.KILOMETERS);

        String jsonFilter = JsonUtils.toJsonString(filter, true);
        String expectedJsonFilter = "{\r\n" +
                "  \"operation\" : \"GEO_DISTANCE\",\r\n" +
                "  \"field\" : \"coordinates\",\r\n" +
                "  \"point\" : {\r\n" +
                "    \"lat\" : 21.2130969,\r\n" +
                "    \"lon\" : 49.1852093,\r\n" +
                "    \"geohash\" : \"t5fbxjxx4ry5\",\r\n" +
                "    \"fragment\" : true\r\n" +
                "  },\r\n" +
                "  \"distance\" : 10.0,\r\n" +
                "  \"unit\" : \"KILOMETERS\"\r\n" +
                "}";

        assertEquals(expectedJsonFilter, jsonFilter);
    }

    @Test
    void serializeWithParams() {
        Params params = new Params();
        params.addFilter(
                new GeoDistanceFilter(IndexFields.coordinates, new GeoPoint(21.2130969, 49.1852093), 10, DistanceUnit.KILOMETERS)
        );

        String jsonParams = JsonUtils.toJsonString(params, true);
        String expectedJsonParams = "{\r\n" +
                "  \"sort\" : [ ],\r\n" +
                "  \"offset\" : null,\r\n" +
                "  \"size\" : 10,\r\n" +
                "  \"searchAfter\" : null,\r\n" +
                "  \"flipDirection\" : false,\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"GEO_DISTANCE\",\r\n" +
                "    \"field\" : \"coordinates\",\r\n" +
                "    \"point\" : {\r\n" +
                "      \"lat\" : 21.2130969,\r\n" +
                "      \"lon\" : 49.1852093,\r\n" +
                "      \"geohash\" : \"t5fbxjxx4ry5\",\r\n" +
                "      \"fragment\" : true\r\n" +
                "    },\r\n" +
                "    \"distance\" : 10.0,\r\n" +
                "    \"unit\" : \"KILOMETERS\"\r\n" +
                "  } ],\r\n" +
                "  \"aggregations\" : [ ],\r\n" +
                "  \"fields\" : null,\r\n" +
                "  \"include\" : [ ],\r\n" +
                "  \"exclude\" : [ ]\r\n" +
                "}";

        assertEquals(expectedJsonParams, jsonParams);
    }

    @Test
    void deserialize() {
        String jsonFilter = "{\r\n" +
                "  \"operation\" : \"GEO_DISTANCE\",\r\n" +
                "  \"field\" : \"coordinates\",\r\n" +
                "  \"point\" : {\r\n" +
                "    \"lat\" : 21.2130969,\r\n" +
                "    \"lon\" : 49.1852093,\r\n" +
                "    \"geohash\" : \"t5fbxjxx4ry5\",\r\n" +
                "    \"fragment\" : true\r\n" +
                "  },\r\n" +
                "  \"distance\" : 10.0,\r\n" +
                "  \"unit\" : \"KILOMETERS\"\r\n" +
                "}";
        Filter filter = JsonUtils.fromJsonString(jsonFilter, Filter.class);

        Filter expectedFilter = new GeoDistanceFilter(IndexFields.coordinates, new GeoPoint(21.2130969, 49.1852093), 10, DistanceUnit.KILOMETERS);

        assertThat(filter).isEqualToComparingFieldByField(expectedFilter);
    }

    @Test
    void deserializeWithParams() {
        String jsonParams = "{\r\n" +
                "  \"sort\" : [ ],\r\n" +
                "  \"offset\" : null,\r\n" +
                "  \"size\" : 10,\r\n" +
                "  \"searchAfter\" : null,\r\n" +
                "  \"flipDirection\" : false,\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"GEO_DISTANCE\",\r\n" +
                "    \"field\" : \"coordinates\",\r\n" +
                "    \"point\" : {\r\n" +
                "      \"lat\" : 21.2130969,\r\n" +
                "      \"lon\" : 49.1852093,\r\n" +
                "      \"geohash\" : \"t5fbxjxx4ry5\",\r\n" +
                "      \"fragment\" : true\r\n" +
                "    },\r\n" +
                "    \"distance\" : 10.0,\r\n" +
                "    \"unit\" : \"KILOMETERS\"\r\n" +
                "  } ],\r\n" +
                "  \"aggregations\" : [ ],\r\n" +
                "  \"fields\" : null,\r\n" +
                "  \"include\" : [ ],\r\n" +
                "  \"exclude\" : [ ]\r\n" +
                "}";
        Params params = JsonUtils.fromJsonString(jsonParams, Params.class);

        Params expectedParams = new Params();
        expectedParams.addFilter(
                new GeoDistanceFilter(IndexFields.coordinates, new GeoPoint(21.2130969, 49.1852093), 10, DistanceUnit.KILOMETERS)
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void coordinateWithinDistance_first() {
        Params params = new Params();
        params.addFilter(
                new GeoDistanceFilter(IndexFields.coordinates, new GeoPoint(21.2130969, 49.1852093), 10, DistanceUnit.KILOMETERS)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void coordinateWithinDistance_second() {
        Params params = new Params();
        params.addFilter(
                new GeoDistanceFilter(IndexFields.coordinates, new GeoPoint(16.6057847, 49.1258932), 10, DistanceUnit.KILOMETERS)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void coordinateWithinDistance_both() {
        Params params = new Params();
        params.addFilter(
                new GeoDistanceFilter(IndexFields.coordinates, new GeoPoint(19.2675660, 49.0490454), 300, DistanceUnit.KILOMETERS)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void coordinateWithinDistance_none() {
        Params params = new Params();
        params.addFilter(
                new GeoDistanceFilter(IndexFields.coordinates, new GeoPoint(19.2675660, 49.0490454), 10, DistanceUnit.KILOMETERS)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void filterFieldNotExisting() {
        Params params = new Params();
        params.addFilter(
                new GeoDistanceFilter("nonMappedField", new GeoPoint(19.2675660, 49.0490454), 300, DistanceUnit.KILOMETERS)
        );

        assertThrows(IndexException.class, () -> repository.listByParams(params));
    }

    @Test
    void filterFieldNotGeoLeaf() {
        Params params = new Params();
        params.addFilter(
                new GeoDistanceFilter(IndexFields.toOneRelationshipNested, new GeoPoint(19.2675660, 49.0490454), 10, DistanceUnit.KILOMETERS)
        );

        assertThrows(IndexException.class, () -> repository.listByParams(params));
    }
}
