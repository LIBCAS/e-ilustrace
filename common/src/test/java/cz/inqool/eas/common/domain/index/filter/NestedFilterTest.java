package cz.inqool.eas.common.domain.index.filter;

import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject.IndexFields;
import cz.inqool.eas.common.domain.index.dto.filter.*;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import static cz.inqool.eas.common.dao.simple.keyvalue.SimpleKeyValueIndexedObject.IndexFields.key;
import static cz.inqool.eas.common.dao.simple.keyvalue.SimpleKeyValueIndexedObject.IndexFields.value;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NestedFilterTest extends IndexFilterTestBase {

    @Test
    void serialize() {
        Filter filter = new NestedFilter(
                IndexFields.toManyRelationship,
                new EqFilter(IndexFields.toManyRelationship + "." + key, entity_1.getToManyRelationship().get(0).getKey())
        );

        String jsonFilter = JsonUtils.toJsonString(filter, true);
        String expectedJsonFilter = "{\r\n" +
                "  \"operation\" : \"NESTED\",\r\n" +
                "  \"path\" : \"toManyRelationship\",\r\n" +
                "  \"filter\" : {\r\n" +
                "    \"operation\" : \"EQ\",\r\n" +
                "    \"field\" : \"toManyRelationship.key\",\r\n" +
                "    \"value\" : \"Son Luke\"\r\n" +
                "  },\r\n" +
                "  \"scoreMode\" : \"None\"\r\n" +
                "}";

        assertEquals(expectedJsonFilter, jsonFilter);
    }

    @Test
    void serializeWithParams() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toManyRelationship,
                        new EqFilter(IndexFields.toManyRelationship + "." + key, entity_1.getToManyRelationship().get(0).getKey())
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
                "    \"operation\" : \"NESTED\",\r\n" +
                "    \"path\" : \"toManyRelationship\",\r\n" +
                "    \"filter\" : {\r\n" +
                "      \"operation\" : \"EQ\",\r\n" +
                "      \"field\" : \"toManyRelationship.key\",\r\n" +
                "      \"value\" : \"Son Luke\"\r\n" +
                "    },\r\n" +
                "    \"scoreMode\" : \"None\"\r\n" +
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
                "  \"operation\" : \"NESTED\",\r\n" +
                "  \"path\" : \"toManyRelationship\",\r\n" +
                "  \"filter\" : {\r\n" +
                "    \"operation\" : \"EQ\",\r\n" +
                "    \"field\" : \"toManyRelationship.key\",\r\n" +
                "    \"value\" : \"Son Luke\"\r\n" +
                "  },\r\n" +
                "  \"scoreMode\" : \"None\"\r\n" +
                "}";
        Filter filter = JsonUtils.fromJsonString(jsonFilter, Filter.class);

        Filter expectedFilter = new NestedFilter(
                IndexFields.toManyRelationship,
                new EqFilter(IndexFields.toManyRelationship + "." + key, entity_1.getToManyRelationship().get(0).getKey())
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
                "    \"operation\" : \"NESTED\",\r\n" +
                "    \"path\" : \"toManyRelationship\",\r\n" +
                "    \"filter\" : {\r\n" +
                "      \"operation\" : \"EQ\",\r\n" +
                "      \"field\" : \"toManyRelationship.key\",\r\n" +
                "      \"value\" : \"Son Luke\"\r\n" +
                "    },\r\n" +
                "    \"scoreMode\" : \"None\"\r\n" +
                "  } ],\r\n" +
                "  \"aggregations\" : [ ],\r\n" +
                "  \"fields\" : null,\r\n" +
                "  \"include\" : [ ],\r\n" +
                "  \"exclude\" : [ ]\r\n" +
                "}";
        Params params = JsonUtils.fromJsonString(jsonParams, Params.class);

        Params expectedParams = new Params();
        expectedParams.addFilter(
                new NestedFilter(
                        IndexFields.toManyRelationship,
                        new EqFilter(IndexFields.toManyRelationship + "." + key, entity_1.getToManyRelationship().get(0).getKey())
                )
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void and_first() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toManyRelationship,
                        new AndFilter(
                                new EqFilter(IndexFields.toManyRelationship + "." + key, entity_1.getToManyRelationship().get(0).getKey()),
                                new EqFilter(IndexFields.toManyRelationship + "." + value, entity_1.getToManyRelationship().get(0).getValue())
                        )
                )
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void and_second() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toManyRelationship,
                        new AndFilter(
                                new EqFilter(IndexFields.toManyRelationship + "." + key, entity_2.getToManyRelationship().get(1).getKey()),
                                new EqFilter(IndexFields.toManyRelationship + "." + value, entity_2.getToManyRelationship().get(1).getValue())
                        )
                )
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void and_all() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toManyRelationship,
                        new OrFilter(
                                new AndFilter(
                                        new EqFilter(IndexFields.toManyRelationship + "." + key, entity_1.getToManyRelationship().get(0).getKey()),
                                        new EqFilter(IndexFields.toManyRelationship + "." + value, entity_1.getToManyRelationship().get(0).getValue())
                                ),
                                new AndFilter(
                                        new EqFilter(IndexFields.toManyRelationship + "." + key, entity_2.getToManyRelationship().get(1).getKey()),
                                        new EqFilter(IndexFields.toManyRelationship + "." + value, entity_2.getToManyRelationship().get(1).getValue())
                                )
                        )
                )
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void and_none() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toManyRelationship,
                        new AndFilter(
                                new EqFilter(IndexFields.toManyRelationship + "." + key, entity_1.getToManyRelationship().get(0).getKey()),
                                new EqFilter(IndexFields.toManyRelationship + "." + value, entity_1.getToManyRelationship().get(1).getValue())
                        )
                )
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }
}
