package cz.inqool.eas.common.domain.index.filter;

import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject.IndexFields;
import cz.inqool.eas.common.domain.index.dto.filter.Filter;
import cz.inqool.eas.common.domain.index.dto.filter.GeoBoundingBoxFilter;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.utils.JsonUtils;
import org.elasticsearch.common.geo.GeoPoint;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GeoBoundingBoxFilterTest extends IndexFilterTestBase {

    @Test
    void serialize() {
        Filter filter = new GeoBoundingBoxFilter(
                IndexFields.coordinates,
                new GeoPoint(21, 49),
                new GeoPoint(22, 50)
        );

        String jsonFilter = JsonUtils.toJsonString(filter, true);
        String expectedJsonFilter = "{\r\n" +
                "  \"operation\" : \"GEO_BOUNDING_BOX\",\r\n" +
                "  \"field\" : \"coordinates\",\r\n" +
                "  \"topLeft\" : {\r\n" +
                "    \"lat\" : 21.0,\r\n" +
                "    \"lon\" : 49.0,\r\n" +
                "    \"geohash\" : \"t5dz7nbtyg8p\",\r\n" +
                "    \"fragment\" : true\r\n" +
                "  },\r\n" +
                "  \"bottomRight\" : {\r\n" +
                "    \"lat\" : 22.0,\r\n" +
                "    \"lon\" : 50.0,\r\n" +
                "    \"geohash\" : \"t5gt1ubzk3np\",\r\n" +
                "    \"fragment\" : true\r\n" +
                "  }\r\n" +
                "}";

        assertEquals(expectedJsonFilter, jsonFilter);
    }

    @Test
    void serializeWithParams() {
        Params params = new Params();
        params.addFilter(
                new GeoBoundingBoxFilter(
                        IndexFields.coordinates,
                        new GeoPoint(21, 49),
                        new GeoPoint(22, 50)
                )
        );

        String jsonParams = JsonUtils.toJsonString(params, true);
        String expectedJsonParams = "{\r\n" +
                "  \"sort\" : [ ],\r\n" +
                "  \"offset\" : null,\r\n" +
                "  \"size\" : 10,\r\n" +
                "  \"searchAfter\" : null,\r\n" +
                "  \"flipDirection\" : false,\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"GEO_BOUNDING_BOX\",\r\n" +
                "    \"field\" : \"coordinates\",\r\n" +
                "    \"topLeft\" : {\r\n" +
                "      \"lat\" : 21.0,\r\n" +
                "      \"lon\" : 49.0,\r\n" +
                "      \"geohash\" : \"t5dz7nbtyg8p\",\r\n" +
                "      \"fragment\" : true\r\n" +
                "    },\r\n" +
                "    \"bottomRight\" : {\r\n" +
                "      \"lat\" : 22.0,\r\n" +
                "      \"lon\" : 50.0,\r\n" +
                "      \"geohash\" : \"t5gt1ubzk3np\",\r\n" +
                "      \"fragment\" : true\r\n" +
                "    }\r\n" +
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
                "  \"operation\" : \"GEO_BOUNDING_BOX\",\r\n" +
                "  \"field\" : \"coordinates\",\r\n" +
                "  \"topLeft\" : {\r\n" +
                "    \"lat\" : 21.0,\r\n" +
                "    \"lon\" : 49.0,\r\n" +
                "    \"geohash\" : \"t5dz7nbtyg8p\",\r\n" +
                "    \"fragment\" : true\r\n" +
                "  },\r\n" +
                "  \"bottomRight\" : {\r\n" +
                "    \"lat\" : 22.0,\r\n" +
                "    \"lon\" : 50.0,\r\n" +
                "    \"geohash\" : \"t5gt1ubzk3np\",\r\n" +
                "    \"fragment\" : true\r\n" +
                "  }\r\n" +
                "}";
        Filter filter = JsonUtils.fromJsonString(jsonFilter, Filter.class);

        Filter expectedFilter = new GeoBoundingBoxFilter(
                IndexFields.coordinates,
                new GeoPoint(21, 49),
                new GeoPoint(22, 50)
        );

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
                "    \"operation\" : \"GEO_BOUNDING_BOX\",\r\n" +
                "    \"field\" : \"coordinates\",\r\n" +
                "    \"topLeft\" : {\r\n" +
                "      \"lat\" : 21.0,\r\n" +
                "      \"lon\" : 49.0,\r\n" +
                "      \"geohash\" : \"t5dz7nbtyg8p\",\r\n" +
                "      \"fragment\" : true\r\n" +
                "    },\r\n" +
                "    \"bottomRight\" : {\r\n" +
                "      \"lat\" : 22.0,\r\n" +
                "      \"lon\" : 50.0,\r\n" +
                "      \"geohash\" : \"t5gt1ubzk3np\",\r\n" +
                "      \"fragment\" : true\r\n" +
                "    }\r\n" +
                "  } ],\r\n" +
                "  \"aggregations\" : [ ],\r\n" +
                "  \"fields\" : null,\r\n" +
                "  \"include\" : [ ],\r\n" +
                "  \"exclude\" : [ ]\r\n" +
                "}";
        Params params = JsonUtils.fromJsonString(jsonParams, Params.class);

        Params expectedParams = new Params();
        expectedParams.addFilter(
                new GeoBoundingBoxFilter(
                        IndexFields.coordinates,
                        new GeoPoint(21, 49),
                        new GeoPoint(22, 50)
                )
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void geoBoundingBox_first() {
        Params params = new Params();
        params.addFilter(
                new GeoBoundingBoxFilter(
                        IndexFields.coordinates,
                        new GeoPoint(22, 49),
                        new GeoPoint(21, 50)
                )
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void geoBoundingBox_second() {
        Params params = new Params();
        params.addFilter(
                new GeoBoundingBoxFilter(
                        IndexFields.coordinates,
                        new GeoPoint(17, 49),
                        new GeoPoint(16, 50)
                )
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void geoBoundingBox_all() {
        Params params = new Params();
        params.addFilter(
                new GeoBoundingBoxFilter(
                        IndexFields.coordinates,
                        new GeoPoint(22, 49),
                        new GeoPoint(16, 50)
                )
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void geoBoundingBox_none() {
        Params params = new Params();
        params.addFilter(
                new GeoBoundingBoxFilter(
                        IndexFields.coordinates,
                        new GeoPoint(19, 49),
                        new GeoPoint(18, 50)
                )
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }
}
