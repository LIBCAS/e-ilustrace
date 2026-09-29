package cz.inqool.eas.common.domain.index.filter;

import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject.IndexFields;
import cz.inqool.eas.common.domain.index.dto.filter.*;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.exception.v2.IndexException;
import cz.inqool.eas.common.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IsNullFilterTest extends IndexFilterTestBase {

    @Test
    void serialize() {
        Filter filter = new NullFilter(IndexFields.uuidId);

        String jsonFilter = JsonUtils.toJsonString(filter, true);
        String expectedJsonFilter = "{\r\n" +
                "  \"operation\" : \"IS_NULL\",\r\n" +
                "  \"field\" : \"uuidId\"\r\n" +
                "}";

        assertEquals(expectedJsonFilter, jsonFilter);
    }

    @Test
    void serializeWithParams() {
        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.uuidId)
        );

        String jsonParams = JsonUtils.toJsonString(params, true);
        String expectedJsonParams = "{\r\n" +
                "  \"sort\" : [ ],\r\n" +
                "  \"offset\" : null,\r\n" +
                "  \"size\" : 10,\r\n" +
                "  \"searchAfter\" : null,\r\n" +
                "  \"flipDirection\" : false,\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"IS_NULL\",\r\n" +
                "    \"field\" : \"uuidId\"\r\n" +
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
                "  \"operation\" : \"IS_NULL\",\r\n" +
                "  \"field\" : \"uuidId\"\r\n" +
                "}";
        Filter filter = JsonUtils.fromJsonString(jsonFilter, Filter.class);

        Filter expectedFilter = new NullFilter(IndexFields.uuidId);

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
                "    \"operation\" : \"IS_NULL\",\r\n" +
                "    \"field\" : \"uuidId\"\r\n" +
                "  } ],\r\n" +
                "  \"aggregations\" : [ ],\r\n" +
                "  \"fields\" : null,\r\n" +
                "  \"include\" : [ ],\r\n" +
                "  \"exclude\" : [ ]\r\n" +
                "}";
        Params params = JsonUtils.fromJsonString(jsonParams, Params.class);

        Params expectedParams = new Params();
        expectedParams.addFilter(
                new NullFilter(IndexFields.uuidId)
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void keywordIsNull_first() {
        entity_1.setUuidId(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.uuidId)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordIsNull_second() {
        entity_2.setUuidId(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.uuidId)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void keywordIsNull_all() {
        entity_1.setUuidId(null);
        entity_2.setUuidId(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.uuidId)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void keywordIsNull_none() {
        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.uuidId)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void textIsNull_first() {
        entity_1.setShortString(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.shortString)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textIsNull_second() {
        entity_2.setShortString(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.shortString)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void textIsNull_all() {
        entity_1.setShortString(null);
        entity_2.setShortString(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.shortString)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void textIsNull_none() {
        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.shortString)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void integerIsNull_first() {
        entity_1.setIntegerObject(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.integerObject)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void integerIsNull_second() {
        entity_2.setIntegerObject(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.integerObject)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void integerIsNull_all() {
        entity_1.setIntegerObject(null);
        entity_2.setIntegerObject(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.integerObject)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void integerIsNull_none() {
        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.integerObject)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void longIsNull_first() {
        entity_1.setLongObject(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.longObject)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void longIsNull_second() {
        entity_2.setLongObject(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.longObject)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void longIsNull_all() {
        entity_1.setLongObject(null);
        entity_2.setLongObject(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.longObject)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void longIsNull_none() {
        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.longObject)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void doubleIsNull_first() {
        entity_1.setDoubleObject(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.doubleObject)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void doubleIsNull_second() {
        entity_2.setDoubleObject(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.doubleObject)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void doubleIsNull_all() {
        entity_1.setDoubleObject(null);
        entity_2.setDoubleObject(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.doubleObject)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void doubleIsNull_none() {
        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.doubleObject)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void dateIsNull_first() {
        entity_1.setLocalDate(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.localDate)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void dateIsNull_second() {
        entity_2.setLocalDate(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.localDate)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void dateIsNull_all() {
        entity_1.setLocalDate(null);
        entity_2.setLocalDate(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.localDate)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void dateIsNull_none() {
        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.localDate)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void booleanIsNull_first() {
        entity_1.setBooleanObject(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.booleanObject)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void booleanIsNull_second() {
        entity_2.setBooleanObject(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.booleanObject)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void booleanIsNull_all() {
        entity_1.setBooleanObject(null);
        entity_2.setBooleanObject(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.booleanObject)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void booleanIsNull_none() {
        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.booleanObject)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void listIsNull_first() {
        entity_1.setElementCollection(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.elementCollection)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void listIsNull_second() {
        entity_2.setElementCollection(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.elementCollection)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void listIsNull_all() {
        entity_1.setElementCollection(null);
        entity_2.setElementCollection(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.elementCollection)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void listIsNull_none() {
        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.elementCollection)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void objectIsNull_first() {
        entity_1.getToOneRelationship().setKey(null);
        entity_1.getToOneRelationship().setValue(null);
        repository.update(entity_1);
        entity_1.setToOneRelationship(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.toOneRelationship)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void objectIsNull_second() {
        entity_2.setToOneRelationship(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.toOneRelationship)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void objectIsNull_all() {
        entity_1.setToOneRelationship(null);
        entity_2.setToOneRelationship(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.toOneRelationship)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void objectIsNull_none() {
        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.toOneRelationship)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectIsNull_first() { // not used NullFilter, because it doesn't work with nested fields (nested filter has to be between NOT & NOT_NULL filter)
        entity_1.getToOneRelationship().setKey(null);
        entity_1.getToOneRelationship().setValue(null);
        repository.update(entity_1);
        entity_1.setToOneRelationship(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NotFilter(
                        new NestedFilter(
                                IndexFields.toOneRelationshipNested,
                                new NotNullFilter(IndexFields.toOneRelationshipNested)
                        )
                )
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectIsNull_second() { // not used NullFilter, because it doesn't work with nested fields (nested filter has to be between NOT & NOT_NULL filter)
        entity_2.setToOneRelationship(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NotFilter(
                        new NestedFilter(
                                IndexFields.toOneRelationshipNested,
                                new NotNullFilter(IndexFields.toOneRelationshipNested)
                        )
                )
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectIsNull_all() {
        entity_1.setToOneRelationship(null);
        entity_2.setToOneRelationship(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NullFilter(IndexFields.toOneRelationshipNested)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectIsNull_none() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toOneRelationshipNested,
                        new NullFilter(IndexFields.toOneRelationshipNested)
                )
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void filterFieldNotMapped() {
        Params params = new Params();
        params.addFilter(
                new NullFilter("nonMapped")
        );

        assertThrows(IndexException.class, () -> repository.listByParams(params));
    }
}
