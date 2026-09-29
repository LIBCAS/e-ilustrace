package cz.inqool.eas.common.domain.index.filter;

import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject.IndexFields;
import cz.inqool.eas.common.domain.index.dto.filter.Filter;
import cz.inqool.eas.common.domain.index.dto.filter.LtFilter;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.exception.v2.IndexException;
import cz.inqool.eas.common.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LtFilterTest extends IndexFilterTestBase {

    @Test
    void serialize() {
        Filter filter = new LtFilter(IndexFields.integerObject, 41);

        String jsonFilter = JsonUtils.toJsonString(filter, true);
        String expectedJsonFilter = "{\r\n" +
                "  \"operation\" : \"LT\",\r\n" +
                "  \"field\" : \"integerObject\",\r\n" +
                "  \"value\" : \"41\",\r\n" +
                "  \"relation\" : null\r\n" +
                "}";

        assertEquals(expectedJsonFilter, jsonFilter);
    }

    @Test
    void serializeWithParams() {
        Params params = new Params();
        params.addFilter(
                new LtFilter(IndexFields.integerObject, 41)
        );

        String jsonParams = JsonUtils.toJsonString(params, true);
        String expectedJsonParams = "{\r\n" +
                "  \"sort\" : [ ],\r\n" +
                "  \"offset\" : null,\r\n" +
                "  \"size\" : 10,\r\n" +
                "  \"searchAfter\" : null,\r\n" +
                "  \"flipDirection\" : false,\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"LT\",\r\n" +
                "    \"field\" : \"integerObject\",\r\n" +
                "    \"value\" : \"41\",\r\n" +
                "    \"relation\" : null\r\n" +
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
                "  \"operation\" : \"LT\",\r\n" +
                "  \"field\" : \"integerObject\",\r\n" +
                "  \"value\" : \"41\",\r\n" +
                "  \"relation\" : null\r\n" +
                "}";
        Filter filter = JsonUtils.fromJsonString(jsonFilter, Filter.class);

        Filter expectedFilter = new LtFilter(IndexFields.integerObject, 41);

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
                "    \"operation\" : \"LT\",\r\n" +
                "    \"field\" : \"integerObject\",\r\n" +
                "    \"value\" : \"41\",\r\n" +
                "    \"relation\" : null\r\n" +
                "  } ],\r\n" +
                "  \"aggregations\" : [ ],\r\n" +
                "  \"fields\" : null,\r\n" +
                "  \"include\" : [ ],\r\n" +
                "  \"exclude\" : [ ]\r\n" +
                "}";
        Params params = JsonUtils.fromJsonString(jsonParams, Params.class);

        Params expectedParams = new Params();
        expectedParams.addFilter(
                new LtFilter(IndexFields.integerObject, 41)
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void integerLt_first() {
        entity_1.setIntegerObject(40);
        entity_2.setIntegerObject(43);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new LtFilter(IndexFields.integerObject, 41)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void integerLt_second() {
        entity_1.setIntegerObject(45);
        entity_2.setIntegerObject(43);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new LtFilter(IndexFields.integerObject, 44)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void integerLt_all() {
        entity_1.setIntegerObject(45);
        entity_2.setIntegerObject(43);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new LtFilter(IndexFields.integerObject, 50)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void integerLt_none() {
        entity_1.setIntegerObject(45);
        entity_2.setIntegerObject(43);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new LtFilter(IndexFields.integerObject, 41)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void longLt_first() {
        entity_1.setLongObject(40L);
        entity_2.setLongObject(43L);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new LtFilter(IndexFields.longObject, 41L)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void longLt_second() {
        entity_1.setLongObject(45L);
        entity_2.setLongObject(43L);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new LtFilter(IndexFields.longObject, 44L)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void longLt_all() {
        entity_1.setLongObject(45L);
        entity_2.setLongObject(43L);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new LtFilter(IndexFields.longObject, 50L)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void longLt_none() {
        entity_1.setLongObject(45L);
        entity_2.setLongObject(43L);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new LtFilter(IndexFields.longObject, 41)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void doubleLt_first() {
        entity_1.setDoubleObject(40.3);
        entity_2.setDoubleObject(43.4);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new LtFilter(IndexFields.doubleObject, 41.2)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void doubleLt_second() {
        entity_1.setDoubleObject(45.3);
        entity_2.setDoubleObject(43.4);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new LtFilter(IndexFields.doubleObject, 43.9)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void doubleLt_all() {
        entity_1.setDoubleObject(45.3);
        entity_2.setDoubleObject(43.4);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new LtFilter(IndexFields.doubleObject, 50.4)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void doubleLt_none() {
        entity_1.setDoubleObject(45.3);
        entity_2.setDoubleObject(43.4);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new LtFilter(IndexFields.doubleObject, 41.8)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void dateLt_first() {
        entity_1.setLocalDate(LocalDate.parse("2016-01-01"));
        entity_2.setLocalDate(LocalDate.parse("2017-01-01"));
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new LtFilter(IndexFields.localDate, "2016-02-01")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void dateLt_second() {
        entity_1.setLocalDate(LocalDate.parse("2018-01-01"));
        entity_2.setLocalDate(LocalDate.parse("2017-01-01"));
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new LtFilter(IndexFields.localDate, "2017-02-01")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void dateLt_all() {
        entity_1.setLocalDate(LocalDate.parse("2018-01-01"));
        entity_2.setLocalDate(LocalDate.parse("2017-01-01"));
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new LtFilter(IndexFields.localDate, "2019-01-01")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void dateLt_none() {
        entity_1.setLocalDate(LocalDate.parse("2018-01-01"));
        entity_2.setLocalDate(LocalDate.parse("2017-01-01"));
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new LtFilter(IndexFields.localDate, "2016-01-01")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void filterFieldNotMapped() {
        Params params = new Params();
        params.addFilter(
                new LtFilter("nonMapped", "Hammer")
        );

        assertThrows(IndexException.class, () -> repository.listByParams(params));
    }

    @Test
    void filterFieldNotLeaf() {
        Params params = new Params();
        params.addFilter(
                new LtFilter(IndexFields.toOneRelationshipNested, "47")
        );

        assertThrows(IndexException.class, () -> repository.listByParams(params));
    }
}
