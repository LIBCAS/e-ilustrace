package cz.inqool.eas.common.domain.index.filter;

import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject.IndexFields;
import cz.inqool.eas.common.domain.index.dto.filter.AndFilter;
import cz.inqool.eas.common.domain.index.dto.filter.EqFilter;
import cz.inqool.eas.common.domain.index.dto.filter.Filter;
import cz.inqool.eas.common.domain.index.dto.filter.NestedFilter;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.exception.v2.IndexException;
import cz.inqool.eas.common.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static cz.inqool.eas.common.dao.simple.keyvalue.SimpleKeyValueIndexedObject.IndexFields.key;
import static cz.inqool.eas.common.dao.simple.keyvalue.SimpleKeyValueIndexedObject.IndexFields.value;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EqFilterTest extends IndexFilterTestBase {

    @Test
    void serialize() {
        Filter filter = new EqFilter(IndexFields.uuidId, entity_1.getUuidId().toString());

        String jsonFilter = JsonUtils.toJsonString(filter, true);
        String expectedJsonFilter = "{\r\n" +
                "  \"operation\" : \"EQ\",\r\n" +
                "  \"field\" : \"uuidId\",\r\n" +
                "  \"value\" : \"3daf1258-79b8-40b4-8f01-363e74360956\"\r\n" +
                "}";

        assertEquals(expectedJsonFilter, jsonFilter);
    }

    @Test
    void serializeWithParams() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.uuidId, entity_1.getUuidId().toString())
        );

        String jsonParams = JsonUtils.toJsonString(params, true);
        String expectedJsonParams = "{\r\n" +
                "  \"sort\" : [ ],\r\n" +
                "  \"offset\" : null,\r\n" +
                "  \"size\" : 10,\r\n" +
                "  \"searchAfter\" : null,\r\n" +
                "  \"flipDirection\" : false,\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"EQ\",\r\n" +
                "    \"field\" : \"uuidId\",\r\n" +
                "    \"value\" : \"3daf1258-79b8-40b4-8f01-363e74360956\"\r\n" +
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
                "  \"operation\" : \"EQ\",\r\n" +
                "  \"field\" : \"uuidId\",\r\n" +
                "  \"value\" : \"3daf1258-79b8-40b4-8f01-363e74360956\"\r\n" +
                "}";
        Filter filter = JsonUtils.fromJsonString(jsonFilter, Filter.class);

        Filter expectedFilter = new EqFilter(IndexFields.uuidId, entity_1.getUuidId().toString());

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
                "    \"operation\" : \"EQ\",\r\n" +
                "    \"field\" : \"uuidId\",\r\n" +
                "    \"value\" : \"3daf1258-79b8-40b4-8f01-363e74360956\"\r\n" +
                "  } ],\r\n" +
                "  \"aggregations\" : [ ],\r\n" +
                "  \"fields\" : null,\r\n" +
                "  \"include\" : [ ],\r\n" +
                "  \"exclude\" : [ ]\r\n" +
                "}";
        Params params = JsonUtils.fromJsonString(jsonParams, Params.class);

        Params expectedParams = new Params();
        expectedParams.addFilter(
                new EqFilter(IndexFields.uuidId, entity_1.getUuidId().toString())
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void keywordEq_first() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.uuidId, entity_1.getUuidId().toString())
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordEq_second() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.uuidId, entity_2.getUuidId().toString())
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void keywordEq_all() {
        entity_2.setUuidId(entity_1.getUuidId());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.uuidId, entity_1.getUuidId().toString())
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void keywordEq_none() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.uuidId, "91153e82-4b4c-43af-8e05-42db517183d8")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void accentTextEq_first() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.shortString, entity_1.getShortString())
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void accentTextEq_second() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.shortString, entity_2.getShortString())
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void accentTextEq_all() {
        entity_2.setShortString(entity_1.getShortString());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.shortString, entity_1.getShortString())
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void accentTextEq_none() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.shortString, "THIS IS AN MADE-UP TEXT")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void accentTextEqSimilarAsFirst_none() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.shortString, "Obecní úrad Bukovina")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void accentTextEqSimilarAsSecond_none() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.shortString, "případ komisáře Rexa.")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void longTextEq_first() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.longString, entity_1.getLongString())
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void longTextEq_second() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.longString, entity_2.getLongString())
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void longTextEq_all() {
        entity_2.setLongString(entity_1.getLongString());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.longString, entity_1.getLongString())
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void longTextEq_none() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.longString, "THIS IS AN MADE-UP TEXT")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void booleanEq_first() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.booleanObject, entity_1.getBooleanObject())
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void booleanEq_second() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.booleanObject, entity_2.getBooleanObject())
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void booleanEq_all() {
        entity_2.setBooleanObject(entity_1.getBooleanObject());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.booleanObject, entity_1.getBooleanObject())
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void booleanEq_none() {
        entity_1.setBooleanObject(false);
        entity_2.setBooleanObject(false);
        repository.update(List.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.booleanObject, Boolean.TRUE)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void integerEq_first() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.integerObject, entity_1.getIntegerObject())
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void integerEq_second() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.integerObject, entity_2.getIntegerObject())
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void integerEq_all() {
        entity_2.setIntegerObject(entity_1.getIntegerObject());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.integerObject, entity_1.getIntegerObject())
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void integerEq_none() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.integerObject, 456)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void longEq_first() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.longObject, entity_1.getLongObject())
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void longEq_second() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.longObject, entity_2.getLongObject())
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void longEq_all() {
        entity_2.setLongObject(entity_1.getLongObject());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.longObject, entity_1.getLongObject())
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void longEq_none() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.longObject, 84579531)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void doubleEq_first() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.doubleObject, entity_1.getDoubleObject())
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void doubleEq_second() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.doubleObject, entity_2.getDoubleObject())
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void doubleEq_all() {
        entity_2.setDoubleObject(entity_1.getDoubleObject());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.doubleObject, entity_1.getDoubleObject())
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void doubleEq_none() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.doubleObject, 17.695)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void decimalEq_first() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.bigDecimal, entity_1.getBigDecimal())
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void decimalEq_second() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.bigDecimal, entity_2.getBigDecimal())
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void decimalEq_all() {
        entity_2.setBigDecimal(entity_1.getBigDecimal());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.bigDecimal, entity_1.getBigDecimal())
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void decimalEq_none() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.bigDecimal, new BigDecimal("854.21"))
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void dateTimeEq_first() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.localDateTime, entity_1.getLocalDateTime().toString())
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void dateTimeEq_second() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.localDateTime, entity_2.getLocalDateTime().toString())
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void dateTimeEq_all() {
        entity_2.setLocalDateTime(entity_1.getLocalDateTime());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.localDateTime, entity_1.getLocalDateTime().toString())
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void dateTimeEq_none() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.localDateTime, "2014-03-08T10:15:30")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void keywordEnumEq_first() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.tstEnumString, entity_1.getTstEnumString())
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordEnumEq_second() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.tstEnumString, entity_2.getTstEnumString())
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void keywordEnumEq_all() {
        entity_2.setTstEnumString(entity_1.getTstEnumString());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.tstEnumString, entity_1.getTstEnumString())
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void keywordEnumEq_none() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.tstEnumString, "THIS IS AN MADE-UP TEXT")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void textListEq_first() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.elementCollection, entity_1.getElementCollection().get(0))
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textListEq_second() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.elementCollection, entity_2.getElementCollection().get(0))
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void textListEq_all() {
        entity_2.setElementCollection(entity_1.getElementCollection());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.elementCollection, entity_1.getElementCollection().get(1))
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void textListEq_none() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.elementCollection, "If you’re going through hell, keep going.")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectEq_first() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toOneRelationshipNested,
                        new EqFilter(IndexFields.toOneRelationshipNested + "." + key, entity_1.getToOneRelationship().getKey())
                )
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectEq_second() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toOneRelationshipNested,
                        new EqFilter(IndexFields.toOneRelationshipNested + "." + key, entity_2.getToOneRelationship().getKey())
                )
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectEq_all() {
        entity_2.setToOneRelationship(entity_1.getToOneRelationship());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toOneRelationshipNested,
                        new EqFilter(IndexFields.toOneRelationshipNested + "." + key, entity_1.getToOneRelationship().getKey())
                )
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectEq_none() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.toOneRelationshipNested + "." + key, "random Text")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void nestedListEq_first() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toManyRelationship,
                        new EqFilter(IndexFields.toManyRelationship + "." + key, entity_1.getToManyRelationship().get(1).getKey())
                )
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void nestedListEq_second() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toManyRelationship,
                        new EqFilter(IndexFields.toManyRelationship + "." + key, entity_2.getToManyRelationship().get(1).getKey())
                )
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void nestedListEq_all() {
        entity_2.setToManyRelationship(entity_1.getToManyRelationship());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toManyRelationship,
                        new EqFilter(IndexFields.toManyRelationship + "." + key, entity_1.getToManyRelationship().get(1).getKey())
                )
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void nestedListEq_none() {
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

    @Test
    void filterFieldNotMapped() {
        Params params = new Params();
        params.addFilter(
                new EqFilter("nonMapped", "Hammer")
        );

        assertThrows(IndexException.class, () -> repository.listByParams(params));
    }

    @Test
    void filterFieldNotLeaf() {
        Params params = new Params();
        params.addFilter(
                new EqFilter(IndexFields.toOneRelationshipNested, "47")
        );

        assertThrows(IndexException.class, () -> repository.listByParams(params));
    }
}