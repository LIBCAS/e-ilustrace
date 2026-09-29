package cz.inqool.eas.common.domain.index.filter;

import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject.IndexFields;
import cz.inqool.eas.common.domain.index.dto.filter.*;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InFilterTest extends IndexFilterTestBase {

    @Test
    void serialize() {
        Filter filter = new InFilter(IndexFields.shortString, entity_1.getShortString(), entity_2.getShortString());

        String jsonFilter = JsonUtils.toJsonString(filter, true);
        String expectedJsonFilter = "{\r\n" +
                "  \"operation\" : \"IN\",\r\n" +
                "  \"field\" : \"shortString\",\r\n" +
                "  \"values\" : [ \"Obecní úřad Błukovina\", \"Případ komisáře Rexa.\" ]\r\n" +
                "}";

        assertEquals(expectedJsonFilter, jsonFilter);
    }

    @Test
    void serializeWithParams() {
        Params params = new Params();
        params.addFilter(
                new InFilter(IndexFields.shortString, entity_1.getShortString(), entity_2.getShortString())
        );

        String jsonParams = JsonUtils.toJsonString(params, true);
        String expectedJsonParams = "{\r\n" +
                "  \"sort\" : [ ],\r\n" +
                "  \"offset\" : null,\r\n" +
                "  \"size\" : 10,\r\n" +
                "  \"searchAfter\" : null,\r\n" +
                "  \"flipDirection\" : false,\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"IN\",\r\n" +
                "    \"field\" : \"shortString\",\r\n" +
                "    \"values\" : [ \"Obecní úřad Błukovina\", \"Případ komisáře Rexa.\" ]\r\n" +
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
                "  \"operation\" : \"IN\",\r\n" +
                "  \"field\" : \"shortString\",\r\n" +
                "  \"values\" : [ \"Obecní úřad Błukovina\", \"Případ komisáře Rexa.\" ]\r\n" +
                "}";
        Filter filter = JsonUtils.fromJsonString(jsonFilter, Filter.class);

        Filter expectedFilter = new InFilter(IndexFields.shortString, entity_1.getShortString(), entity_2.getShortString());

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
                "    \"operation\" : \"IN\",\r\n" +
                "    \"field\" : \"shortString\",\r\n" +
                "    \"values\" : [ \"Obecní úřad Błukovina\", \"Případ komisáře Rexa.\" ]\r\n" +
                "  } ],\r\n" +
                "  \"aggregations\" : [ ],\r\n" +
                "  \"fields\" : null,\r\n" +
                "  \"include\" : [ ],\r\n" +
                "  \"exclude\" : [ ]\r\n" +
                "}";
        Params params = JsonUtils.fromJsonString(jsonParams, Params.class);

        Params expectedParams = new Params();
        expectedParams.addFilter(
                new InFilter(IndexFields.shortString, entity_1.getShortString(), entity_2.getShortString())
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void and_first() {
        Params params = new Params();
        params.addFilter(
                new InFilter(IndexFields.shortString, entity_1.getShortString(), "random_string")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void and_second() {
        Params params = new Params();
        params.addFilter(
                new InFilter(IndexFields.shortString, "random_string", entity_2.getShortString())
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void and_all() {
        Params params = new Params();
        params.addFilter(
                new InFilter(IndexFields.shortString, entity_1.getShortString(), "random_string", entity_2.getShortString())
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void and_none() {
        Params params = new Params();
        params.addFilter(
                new InFilter(IndexFields.shortString, "random_string")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }
}
