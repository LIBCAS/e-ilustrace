package cz.inqool.eas.common.domain.index.filter;

import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject.IndexFields;
import cz.inqool.eas.common.domain.index.dto.filter.*;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AndFilterTest extends IndexFilterTestBase {

    @Test
    void serialize() {
        Filter filter = new AndFilter(
                new EqFilter(IndexFields.shortString, entity_1.getShortString()),
                new EqFilter(IndexFields.integerObject, entity_1.getIntegerObject().toString())
        );

        String jsonFilter = JsonUtils.toJsonString(filter, true);
        String expectedJsonFilter = "{\r\n" +
                "  \"operation\" : \"AND\",\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"EQ\",\r\n" +
                "    \"field\" : \"shortString\",\r\n" +
                "    \"value\" : \"Obecní úřad Błukovina\"\r\n" +
                "  }, {\r\n" +
                "    \"operation\" : \"EQ\",\r\n" +
                "    \"field\" : \"integerObject\",\r\n" +
                "    \"value\" : \"598\"\r\n" +
                "  } ]\r\n" +
                "}";

        assertEquals(expectedJsonFilter, jsonFilter);
    }

    @Test
    void serializeWithParams() {
        Params params = new Params();
        params.addFilter(
                new AndFilter(
                        new EqFilter(IndexFields.shortString, entity_1.getShortString()),
                        new EqFilter(IndexFields.integerObject, entity_1.getIntegerObject().toString())
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
                "    \"operation\" : \"AND\",\r\n" +
                "    \"filters\" : [ {\r\n" +
                "      \"operation\" : \"EQ\",\r\n" +
                "      \"field\" : \"shortString\",\r\n" +
                "      \"value\" : \"Obecní úřad Błukovina\"\r\n" +
                "    }, {\r\n" +
                "      \"operation\" : \"EQ\",\r\n" +
                "      \"field\" : \"integerObject\",\r\n" +
                "      \"value\" : \"598\"\r\n" +
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
                "  \"operation\" : \"AND\",\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"EQ\",\r\n" +
                "    \"field\" : \"shortString\",\r\n" +
                "    \"value\" : \"Obecní úřad Błukovina\"\r\n" +
                "  }, {\r\n" +
                "    \"operation\" : \"EQ\",\r\n" +
                "    \"field\" : \"integerObject\",\r\n" +
                "    \"value\" : \"598\"\r\n" +
                "  } ]\r\n" +
                "}";
        Filter filter = JsonUtils.fromJsonString(jsonFilter, Filter.class);

        Filter expectedFilter = new AndFilter(
                new EqFilter(IndexFields.shortString, entity_1.getShortString()),
                new EqFilter(IndexFields.integerObject, entity_1.getIntegerObject().toString())
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
                "    \"operation\" : \"AND\",\r\n" +
                "    \"filters\" : [ {\r\n" +
                "      \"operation\" : \"EQ\",\r\n" +
                "      \"field\" : \"shortString\",\r\n" +
                "      \"value\" : \"Obecní úřad Błukovina\"\r\n" +
                "    }, {\r\n" +
                "      \"operation\" : \"EQ\",\r\n" +
                "      \"field\" : \"integerObject\",\r\n" +
                "      \"value\" : \"598\"\r\n" +
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
                new AndFilter(
                        new EqFilter(IndexFields.shortString, entity_1.getShortString()),
                        new EqFilter(IndexFields.integerObject, entity_1.getIntegerObject().toString())
                )
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void and_first() {
        entity_2.setShortString(entity_1.getShortString());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new AndFilter(
                        new EqFilter(IndexFields.shortString, entity_1.getShortString()),
                        new EqFilter(IndexFields.integerObject, entity_1.getIntegerObject().toString())
                )
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void and_second() {
        entity_2.setShortString(entity_1.getShortString());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new AndFilter(
                        new EqFilter(IndexFields.shortString, entity_1.getShortString()),
                        new EqFilter(IndexFields.integerObject, entity_2.getIntegerObject().toString())
                )
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void and_all() {
        Params params = new Params();
        params.addFilter(
                new AndFilter(
                        new GteFilter(IndexFields.integerObject, 500),
                        new LteFilter(IndexFields.integerObject, 8_000_000)
                )
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void and_none() {
        Params params = new Params();
        params.addFilter(
                new AndFilter(
                        new EqFilter(IndexFields.shortString, "random Text"),
                        new LteFilter(IndexFields.integerObject, 0)
                )
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }
}
