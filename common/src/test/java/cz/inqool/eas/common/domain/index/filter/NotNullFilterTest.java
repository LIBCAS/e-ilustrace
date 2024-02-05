package cz.inqool.eas.common.domain.index.filter;

import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject.IndexFields;
import cz.inqool.eas.common.domain.index.dto.filter.Filter;
import cz.inqool.eas.common.domain.index.dto.filter.NestedFilter;
import cz.inqool.eas.common.domain.index.dto.filter.NotNullFilter;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.exception.v2.IndexException;
import cz.inqool.eas.common.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NotNullFilterTest extends IndexFilterTestBase {

    @Test
    void serialize() {
        Filter filter = new NotNullFilter(IndexFields.uuidId);

        String jsonFilter = JsonUtils.toJsonString(filter, true);
        String expectedJsonFilter = "{\r\n" +
                "  \"operation\" : \"NOT_NULL\",\r\n" +
                "  \"field\" : \"uuidId\"\r\n" +
                "}";

        assertEquals(expectedJsonFilter, jsonFilter);
    }

    @Test
    void serializeWithParams() {
        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.uuidId)
        );

        String jsonParams = JsonUtils.toJsonString(params, true);
        String expectedJsonParams = "{\r\n" +
                "  \"sort\" : [ ],\r\n" +
                "  \"offset\" : null,\r\n" +
                "  \"size\" : 10,\r\n" +
                "  \"searchAfter\" : null,\r\n" +
                "  \"flipDirection\" : false,\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"NOT_NULL\",\r\n" +
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
                "  \"operation\" : \"NOT_NULL\",\r\n" +
                "  \"field\" : \"uuidId\"\r\n" +
                "}";
        Filter filter = JsonUtils.fromJsonString(jsonFilter, Filter.class);

        Filter expectedFilter = new NotNullFilter(IndexFields.uuidId);

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
                "    \"operation\" : \"NOT_NULL\",\r\n" +
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
                new NotNullFilter(IndexFields.uuidId)
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void keywordNotNull_first() {
        entity_2.setUuidId(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.uuidId)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordNotNull_second() {
        entity_1.setUuidId(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.uuidId)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void keywordNotNull_all() {
        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.uuidId)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void keywordNotNull_none() {
        entity_1.setUuidId(null);
        entity_2.setUuidId(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.uuidId)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void textNotNull_first() {
        entity_2.setShortString(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.shortString)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textNotNull_second() {
        entity_1.setShortString(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.shortString)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void textNotNull_all() {
        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.shortString)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void textNotNull_none() {
        entity_1.setShortString(null);
        entity_2.setShortString(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.shortString)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void integerNotNull_first() {
        entity_2.setIntegerObject(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.integerObject)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void integerNotNull_second() {
        entity_1.setIntegerObject(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.integerObject)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void integerNotNull_all() {
        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.integerObject)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void integerNotNull_none() {
        entity_1.setIntegerObject(null);
        entity_2.setIntegerObject(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.integerObject)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void longNotNull_first() {
        entity_2.setLongObject(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.longObject)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void longNotNull_second() {
        entity_1.setLongObject(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.longObject)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void longNotNull_all() {
        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.longObject)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void longNotNull_none() {
        entity_1.setLongObject(null);
        entity_2.setLongObject(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.longObject)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void doubleNotNull_first() {
        entity_2.setDoubleObject(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.doubleObject)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void doubleNotNull_second() {
        entity_1.setDoubleObject(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.doubleObject)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void doubleNotNull_all() {
        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.doubleObject)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void doubleNotNull_none() {
        entity_1.setDoubleObject(null);
        entity_2.setDoubleObject(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.doubleObject)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void dateNotNull_first() {
        entity_2.setLocalDate(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.localDate)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void dateNotNull_second() {
        entity_1.setLocalDate(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.localDate)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void dateNotNull_all() {
        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.localDate)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void dateNotNull_none() {
        entity_1.setLocalDate(null);
        entity_2.setLocalDate(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.localDate)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void booleanNotNull_first() {
        entity_2.setBooleanObject(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.booleanObject)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void booleanNotNull_second() {
        entity_1.setBooleanObject(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.booleanObject)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void booleanNotNull_all() {
        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.booleanObject)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void booleanNotNull_none() {
        entity_1.setBooleanObject(null);
        entity_2.setBooleanObject(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.booleanObject)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void listNotNull_first() {
        entity_2.setElementCollection(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.elementCollection)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void listNotNull_second() {
        entity_1.setElementCollection(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.elementCollection)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void listNotNull_all() {
        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.elementCollection)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void listNotNull_none() {
        entity_1.setElementCollection(null);
        entity_2.setElementCollection(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.elementCollection)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void objectNotNull_first() {
        entity_2.setToOneRelationship(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.toOneRelationship)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void objectNotNull_second() {
        entity_1.setToOneRelationship(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.toOneRelationship)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void objectNotNull_all() {
        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.toOneRelationship)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void objectNotNull_none() {
        entity_1.setToOneRelationship(null);
        entity_2.setToOneRelationship(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.toOneRelationship)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectNotNull_first() {
        entity_2.setToOneRelationship(null);
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toOneRelationshipNested,
                        new NotNullFilter(IndexFields.toOneRelationshipNested)
                )
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectNotNull_second() {
        entity_1.setToOneRelationship(null);
        repository.update(entity_1);

        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toOneRelationshipNested,
                        new NotNullFilter(IndexFields.toOneRelationshipNested)
                )
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectNotNull_all() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toOneRelationshipNested,
                        new NotNullFilter(IndexFields.toOneRelationshipNested)
                )
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectNotNull_none() {
        entity_1.setToOneRelationship(null);
        entity_2.setToOneRelationship(null);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new NotNullFilter(IndexFields.toOneRelationshipNested)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void filterFieldNotMapped() {
        Params params = new Params();
        params.addFilter(
                new NotNullFilter("nonMapped")
        );

        assertThrows(IndexException.class, () -> repository.listByParams(params));
    }
}
