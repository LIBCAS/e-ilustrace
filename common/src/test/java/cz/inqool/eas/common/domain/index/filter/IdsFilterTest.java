package cz.inqool.eas.common.domain.index.filter;

import cz.inqool.eas.common.domain.index.dto.filter.Filter;
import cz.inqool.eas.common.domain.index.dto.filter.IdsFilter;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IdsFilterTest extends IndexFilterTestBase {

    @Test
    void serialize() {
        Filter filter = new IdsFilter(entity_1.getId());

        String jsonFilter = JsonUtils.toJsonString(filter, true);
        String expectedJsonFilter = "{\r\n" +
                "  \"operation\" : \"IDS\",\r\n" +
                "  \"ids\" : [ \"" + entity_1.getId() + "\" ]\r\n" +
                "}";

        assertEquals(expectedJsonFilter, jsonFilter);
    }

    @Test
    void serializeWithParams() {
        Params params = new Params();
        params.addFilter(
                new IdsFilter(entity_1.getId())
        );

        String jsonParams = JsonUtils.toJsonString(params, true);
        String expectedJsonParams = "{\r\n" +
                "  \"sort\" : [ ],\r\n" +
                "  \"offset\" : null,\r\n" +
                "  \"size\" : 10,\r\n" +
                "  \"searchAfter\" : null,\r\n" +
                "  \"flipDirection\" : false,\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"IDS\",\r\n" +
                "    \"ids\" : [ \"" + entity_1.getId() + "\" ]\r\n" +
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
                "  \"operation\" : \"IDS\",\r\n" +
                "  \"ids\" : [ \"" + entity_1.getId() + "\" ]\r\n" +
                "}";
        Filter filter = JsonUtils.fromJsonString(jsonFilter, Filter.class);

        Filter expectedFilter = new IdsFilter(entity_1.getId());

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
                "    \"operation\" : \"IDS\",\r\n" +
                "    \"ids\" : [ \"" + entity_1.getId() + "\" ]\r\n" +
                "  } ],\r\n" +
                "  \"aggregations\" : [ ],\r\n" +
                "  \"fields\" : null,\r\n" +
                "  \"include\" : [ ],\r\n" +
                "  \"exclude\" : [ ]\r\n" +
                "}";
        Params params = JsonUtils.fromJsonString(jsonParams, Params.class);

        Params expectedParams = new Params();
        expectedParams.addFilter(
                new IdsFilter(entity_1.getId())
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void ids_first() {
        Params params = new Params();
        params.getFilters().add(
                new IdsFilter(entity_1.getId())
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void ids_second() {
        Params params = new Params();
        params.getFilters().add(
                new IdsFilter(entity_2.getId())
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void ids_all() {
        Params params = new Params();
        params.getFilters().add(
                new IdsFilter(entity_1.getId(), entity_2.getId())
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void ids_none() {
        Params params = new Params();
        params.getFilters().add(
                new IdsFilter("not-existing-id")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }
}
