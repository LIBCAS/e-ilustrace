package cz.inqool.eas.common.domain.index.filter;

import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject.IndexFields;
import cz.inqool.eas.common.domain.index.dto.filter.*;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NotFilterTest extends IndexFilterTestBase {

    @Test
    void serialize() {
        Filter filter = new NotFilter(
                new EqFilter(IndexFields.integerObject, entity_2.getIntegerObject().toString())
        );

        String jsonFilter = JsonUtils.toJsonString(filter, true);
        String expectedJsonFilter = "{\r\n" +
                "  \"operation\" : \"NOT\",\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"EQ\",\r\n" +
                "    \"field\" : \"integerObject\",\r\n" +
                "    \"value\" : \"7415632\"\r\n" +
                "  } ]\r\n" +
                "}";

        assertEquals(expectedJsonFilter, jsonFilter);
    }

    @Test
    void serializeWithParams() {
        Params params = new Params();
        params.addFilter(
                new NotFilter(
                        new EqFilter(IndexFields.integerObject, entity_2.getIntegerObject().toString())
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
                "    \"operation\" : \"NOT\",\r\n" +
                "    \"filters\" : [ {\r\n" +
                "      \"operation\" : \"EQ\",\r\n" +
                "      \"field\" : \"integerObject\",\r\n" +
                "      \"value\" : \"7415632\"\r\n" +
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
                "  \"operation\" : \"NOT\",\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"EQ\",\r\n" +
                "    \"field\" : \"integerObject\",\r\n" +
                "    \"value\" : \"7415632\"\r\n" +
                "  } ]\r\n" +
                "}";
        Filter filter = JsonUtils.fromJsonString(jsonFilter, Filter.class);

        Filter expectedFilter = new NotFilter(
                new EqFilter(IndexFields.integerObject, entity_2.getIntegerObject().toString())
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
                "    \"operation\" : \"NOT\",\r\n" +
                "    \"filters\" : [ {\r\n" +
                "      \"operation\" : \"EQ\",\r\n" +
                "      \"field\" : \"integerObject\",\r\n" +
                "      \"value\" : \"7415632\"\r\n" +
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
                new NotFilter(
                        new EqFilter(IndexFields.integerObject, entity_2.getIntegerObject().toString())
                )
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void not_first() {
        Params params = new Params();
        params.addFilter(
                new NotFilter(
                        new EqFilter(IndexFields.integerObject, entity_2.getIntegerObject().toString())
                )
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void not_second() {
        Params params = new Params();
        params.addFilter(
                new NotFilter(
                        new EqFilter(IndexFields.shortString, entity_1.getShortString())
                )
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void not_all() {
        Params params = new Params();
        params.addFilter(
                new NotFilter(
                        new AndFilter(
                                new EqFilter(IndexFields.shortString, entity_1.getShortString()),
                                new EqFilter(IndexFields.integerObject, entity_2.getIntegerObject().toString())
                        )
                )
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void not_none() {
        Params params = new Params();
        params.addFilter(
                new NotFilter(
                        new OrFilter(
                                new EqFilter(IndexFields.shortString, entity_1.getShortString()),
                                new EqFilter(IndexFields.integerObject, entity_2.getIntegerObject().toString())
                        )
                )
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }
}
