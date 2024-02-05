package cz.inqool.eas.common.domain.index.filter;

import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject.IndexFields;
import cz.inqool.eas.common.domain.index.dto.filter.EqFilter;
import cz.inqool.eas.common.domain.index.dto.filter.Filter;
import cz.inqool.eas.common.domain.index.dto.filter.OrFilter;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrFilterTest extends IndexFilterTestBase {

    @Test
    void serialize() {
        Filter filter = new OrFilter(
                new EqFilter(IndexFields.shortString, entity_1.getShortString()),
                new EqFilter(IndexFields.integerObject, "50")
        );

        String jsonFilter = JsonUtils.toJsonString(filter, true);
        String expectedJsonFilter = "{\r\n" +
                "  \"operation\" : \"OR\",\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"EQ\",\r\n" +
                "    \"field\" : \"shortString\",\r\n" +
                "    \"value\" : \"Obecní úřad Błukovina\"\r\n" +
                "  }, {\r\n" +
                "    \"operation\" : \"EQ\",\r\n" +
                "    \"field\" : \"integerObject\",\r\n" +
                "    \"value\" : \"50\"\r\n" +
                "  } ]\r\n" +
                "}";

        assertEquals(expectedJsonFilter, jsonFilter);
    }

    @Test
    void serializeWithParams() {
        Params params = new Params();
        params.addFilter(
                new OrFilter(
                        new EqFilter(IndexFields.shortString, entity_1.getShortString()),
                        new EqFilter(IndexFields.integerObject, "50")
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
                "    \"operation\" : \"OR\",\r\n" +
                "    \"filters\" : [ {\r\n" +
                "      \"operation\" : \"EQ\",\r\n" +
                "      \"field\" : \"shortString\",\r\n" +
                "      \"value\" : \"Obecní úřad Błukovina\"\r\n" +
                "    }, {\r\n" +
                "      \"operation\" : \"EQ\",\r\n" +
                "      \"field\" : \"integerObject\",\r\n" +
                "      \"value\" : \"50\"\r\n" +
                "    } ]\r\n" +
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
                "  \"operation\" : \"OR\",\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"EQ\",\r\n" +
                "    \"field\" : \"shortString\",\r\n" +
                "    \"value\" : \"Obecní úřad Błukovina\"\r\n" +
                "  }, {\r\n" +
                "    \"operation\" : \"EQ\",\r\n" +
                "    \"field\" : \"integerObject\",\r\n" +
                "    \"value\" : \"50\"\r\n" +
                "  } ]\r\n" +
                "}";
        Filter filter = JsonUtils.fromJsonString(jsonFilter, Filter.class);

        Filter expectedFilter = new OrFilter(
                new EqFilter(IndexFields.shortString, entity_1.getShortString()),
                new EqFilter(IndexFields.integerObject, "50")
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
                "    \"operation\" : \"OR\",\r\n" +
                "    \"filters\" : [ {\r\n" +
                "      \"operation\" : \"EQ\",\r\n" +
                "      \"field\" : \"shortString\",\r\n" +
                "      \"value\" : \"Obecní úřad Błukovina\"\r\n" +
                "    }, {\r\n" +
                "      \"operation\" : \"EQ\",\r\n" +
                "      \"field\" : \"integerObject\",\r\n" +
                "      \"value\" : \"50\"\r\n" +
                "    } ]\r\n" +
                "  } ],\r\n" +
                "  \"aggregations\" : [ ],\r\n" +
                "  \"fields\" : null,\r\n" +
                "  \"include\" : [ ],\r\n" +
                "  \"exclude\" : [ ]\r\n" +
                "}";
        Params params = JsonUtils.fromJsonString(jsonParams, Params.class);

        Params expectedParams = new Params();
        expectedParams.addFilter(
                new OrFilter(
                        new EqFilter(IndexFields.shortString, entity_1.getShortString()),
                        new EqFilter(IndexFields.integerObject, "50")
                )
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void or_first() {
        Params params = new Params();
        params.getFilters().add(
                new OrFilter(
                        new EqFilter(IndexFields.shortString, entity_1.getShortString()),
                        new EqFilter(IndexFields.integerObject, "50")
                )
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void or_second() {
        Params params = new Params();
        params.getFilters().add(
                new OrFilter(
                        new EqFilter(IndexFields.shortString, "randomText"),
                        new EqFilter(IndexFields.integerObject, entity_2.getIntegerObject().toString())
                )
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void or_all() {
        Params params = new Params();
        params.getFilters().add(
                new OrFilter(
                        new EqFilter(IndexFields.shortString, entity_1.getShortString()),
                        new EqFilter(IndexFields.integerObject, entity_2.getIntegerObject().toString())
                )
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void or_none() {
        Params params = new Params();
        params.getFilters().add(
                new OrFilter(
                        new EqFilter(IndexFields.shortString, "random Text"),
                        new EqFilter(IndexFields.integerObject, "50")
                )
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }
}
