package cz.inqool.eas.common.domain.index.filter;

import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject.IndexFields;
import cz.inqool.eas.common.domain.index.dto.filter.Filter;
import cz.inqool.eas.common.domain.index.dto.filter.GteFilter;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.exception.v2.IndexException;
import cz.inqool.eas.common.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GteFilterTest extends IndexFilterTestBase {

    @Test
    void serialize() {
        Filter filter = new GteFilter(IndexFields.integerObject, 45);

        String jsonFilter = JsonUtils.toJsonString(filter, true);
        String expectedJsonFilter = "{\r\n" +
                "  \"operation\" : \"GTE\",\r\n" +
                "  \"field\" : \"integerObject\",\r\n" +
                "  \"value\" : \"45\",\r\n" +
                "  \"relation\" : null\r\n" +
                "}";

        assertEquals(expectedJsonFilter, jsonFilter);
    }

    @Test
    void serializeWithParams() {
        Params params = new Params();
        params.addFilter(
                new GteFilter(IndexFields.integerObject, 45)
        );

        String jsonParams = JsonUtils.toJsonString(params, true);
        String expectedJsonParams = "{\r\n" +
                "  \"sort\" : [ ],\r\n" +
                "  \"offset\" : null,\r\n" +
                "  \"size\" : 10,\r\n" +
                "  \"searchAfter\" : null,\r\n" +
                "  \"flipDirection\" : false,\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"GTE\",\r\n" +
                "    \"field\" : \"integerObject\",\r\n" +
                "    \"value\" : \"45\",\r\n" +
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
                "  \"operation\" : \"GTE\",\r\n" +
                "  \"field\" : \"integerObject\",\r\n" +
                "  \"value\" : \"45\",\r\n" +
                "  \"relation\" : null\r\n" +
                "}";
        Filter filter = JsonUtils.fromJsonString(jsonFilter, Filter.class);

        Filter expectedFilter = new GteFilter(IndexFields.integerObject, 45);

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
                "    \"operation\" : \"GTE\",\r\n" +
                "    \"field\" : \"integerObject\",\r\n" +
                "    \"value\" : \"45\",\r\n" +
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
                new GteFilter(IndexFields.integerObject, 45)
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void integerGte_first() {
        entity_1.setIntegerObject(45);
        entity_2.setIntegerObject(43);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new GteFilter(IndexFields.integerObject, 45)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void integerGte_second() {
        entity_1.setIntegerObject(40);
        entity_2.setIntegerObject(43);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new GteFilter(IndexFields.integerObject, 43)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void integerGte_all() {
        entity_1.setIntegerObject(45);
        entity_2.setIntegerObject(43);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new GteFilter(IndexFields.integerObject, 43)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }


    @Test
    void integerGte_none() {
        entity_1.setIntegerObject(45);
        entity_2.setIntegerObject(43);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new GteFilter(IndexFields.integerObject, 50)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void longGte_first() {
        entity_1.setLongObject(45L);
        entity_2.setLongObject(43L);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new GteFilter(IndexFields.longObject, 45L)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void longGte_second() {
        entity_1.setLongObject(40L);
        entity_2.setLongObject(43L);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new GteFilter(IndexFields.longObject, 43L)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void longGte_all() {
        entity_1.setLongObject(45L);
        entity_2.setLongObject(43L);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new GteFilter(IndexFields.longObject, 43L)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void longGte_none() {
        entity_1.setLongObject(45L);
        entity_2.setLongObject(43L);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new GteFilter(IndexFields.longObject, 50L)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void doubleGte_first() {
        entity_1.setDoubleObject(45.3);
        entity_2.setDoubleObject(43.4);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new GteFilter(IndexFields.doubleObject, 45.3)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void doubleGte_second() {
        entity_1.setDoubleObject(40.3);
        entity_2.setDoubleObject(43.4);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new GteFilter(IndexFields.doubleObject, 43.4)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void doubleGte_all() {
        entity_1.setDoubleObject(45.3);
        entity_2.setDoubleObject(43.4);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new GteFilter(IndexFields.doubleObject, 43.4)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void doubleGte_none() {
        entity_1.setDoubleObject(45.3);
        entity_2.setDoubleObject(43.4);
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new GteFilter(IndexFields.doubleObject, 50.7)
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void dateGte_first() {
        entity_1.setLocalDate(LocalDate.parse("2018-01-01"));
        entity_2.setLocalDate(LocalDate.parse("2017-01-01"));
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new GteFilter(IndexFields.localDate, "2018-01-01")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void dateGte_second() {
        entity_1.setLocalDate(LocalDate.parse("2016-01-01"));
        entity_2.setLocalDate(LocalDate.parse("2017-01-01"));
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new GteFilter(IndexFields.localDate, "2017-01-01")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void dateGte_all() {
        entity_1.setLocalDate(LocalDate.parse("2018-01-01"));
        entity_2.setLocalDate(LocalDate.parse("2017-01-01"));
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new GteFilter(IndexFields.localDate, "2017-01-01")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void dateGte_none() {
        entity_1.setLocalDate(LocalDate.parse("2018-01-01"));
        entity_2.setLocalDate(LocalDate.parse("2017-01-01"));
        repository.update(Set.of(entity_1, entity_2));

        Params params = new Params();
        params.addFilter(
                new GteFilter(IndexFields.localDate, "2019-01-01")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void filterFieldNotMapped() {
        Params params = new Params();
        params.addFilter(
                new GteFilter("nonMapped", "Hammer")
        );

        assertThrows(IndexException.class, () -> repository.listByParams(params));
    }

    @Test
    void filterFieldNotLeaf() {
        Params params = new Params();
        params.addFilter(
                new GteFilter(IndexFields.toOneRelationshipNested, "47")
        );

        assertThrows(IndexException.class, () -> repository.listByParams(params));
    }
}
