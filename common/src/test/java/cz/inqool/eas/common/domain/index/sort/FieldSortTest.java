package cz.inqool.eas.common.domain.index.sort;

import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject.IndexFields;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.domain.index.dto.sort.FieldSort;
import cz.inqool.eas.common.domain.index.dto.sort.FieldSort.MissingValues;
import cz.inqool.eas.common.domain.index.dto.sort.Sort;
import cz.inqool.eas.common.exception.v2.IndexException;
import cz.inqool.eas.common.utils.JsonUtils;
import org.elasticsearch.search.sort.SortMode;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;

import static cz.inqool.eas.common.dao.simple.keyvalue.SimpleKeyValueIndexedObject.IndexFields.value;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FieldSortTest extends IndexSortTestBase {

    @Test
    void serialize() {
        Sort<?> sort = new FieldSort(IndexFields.uuidId, SortOrder.ASC, MissingValues.LAST, SortMode.AVG);

        String jsonSort = JsonUtils.toJsonString(sort, true);
        String expectedJsonSort = "{\r\n" +
                "  \"order\" : \"ASC\",\r\n" +
                "  \"type\" : \"FIELD\",\r\n" +
                "  \"field\" : \"uuidId\",\r\n" +
                "  \"missing\" : \"LAST\",\r\n" +
                "  \"sortMode\" : \"AVG\"\r\n" +
                "}";

        assertEquals(expectedJsonSort, jsonSort);
    }

    @Test
    void serializeWithParams() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.uuidId, SortOrder.ASC, MissingValues.LAST, SortMode.AVG)
        );

        String jsonParams = JsonUtils.toJsonString(params, true);
        String expectedJsonParams = "{\r\n" +
                "  \"sort\" : [ {\r\n" +
                "    \"order\" : \"ASC\",\r\n" +
                "    \"type\" : \"FIELD\",\r\n" +
                "    \"field\" : \"uuidId\",\r\n" +
                "    \"missing\" : \"LAST\",\r\n" +
                "    \"sortMode\" : \"AVG\"\r\n" +
                "  } ],\r\n" +
                "  \"offset\" : null,\r\n" +
                "  \"size\" : 10,\r\n" +
                "  \"searchAfter\" : null,\r\n" +
                "  \"flipDirection\" : false,\r\n" +
                "  \"filters\" : [ ],\r\n" +
                "  \"aggregations\" : [ ],\r\n" +
                "  \"fields\" : null,\r\n" +
                "  \"include\" : [ ],\r\n" +
                "  \"exclude\" : [ ]\r\n" +
                "}";

        assertEquals(expectedJsonParams, jsonParams);
    }

    @Test
    void deserialize() {
        String jsonSort = "{\r\n" +
                "  \"order\" : \"DESC\",\r\n" +
                "  \"type\" : \"FIELD\",\r\n" +
                "  \"field\" : \"instant\",\r\n" +
                "  \"missing\" : \"FIRST\",\r\n" +
                "  \"sortMode\" : \"SUM\"\r\n" +
                "}";
        Sort<?> sort = JsonUtils.fromJsonString(jsonSort, Sort.class);

        Sort<?> expectedSort = new FieldSort(IndexFields.instant, SortOrder.DESC, MissingValues.FIRST, SortMode.SUM);

        assertThat(sort).isEqualToComparingFieldByField(expectedSort);
    }

    @Test
    void deserializeDefault() {
        String jsonSort = "{\r\n" +
                "  \"order\" : \"DESC\",\r\n" +
                "  \"field\" : \"instant\",\r\n" +
                "  \"missing\" : \"FIRST\",\r\n" +
                "  \"sortMode\" : \"SUM\"\r\n" +
                "}";
        Sort<?> sort = JsonUtils.fromJsonString(jsonSort, Sort.class);

        Sort<?> expectedSort = new FieldSort(IndexFields.instant, SortOrder.DESC, MissingValues.FIRST, SortMode.SUM);

        assertThat(sort).isEqualToComparingFieldByField(expectedSort);
    }

    @Test
    void deserializeWithParams() {
        String jsonParams = "{\r\n" +
                "  \"sort\" : [ {\r\n" +
                "    \"order\" : \"DESC\",\r\n" +
                "    \"type\" : \"FIELD\",\r\n" +
                "    \"field\" : \"instant\",\r\n" +
                "    \"missing\" : \"FIRST\",\r\n" +
                "    \"sortMode\" : \"SUM\"\r\n" +
                "  } ],\r\n" +
                "  \"offset\" : null,\r\n" +
                "  \"size\" : 10,\r\n" +
                "  \"searchAfter\" : null,\r\n" +
                "  \"flipDirection\" : false,\r\n" +
                "  \"filters\" : [ ],\r\n" +
                "  \"aggregations\" : [ ],\r\n" +
                "  \"fields\" : null,\r\n" +
                "  \"include\" : [ ],\r\n" +
                "  \"exclude\" : [ ]\r\n" +
                "}";
        Params params = JsonUtils.fromJsonString(jsonParams, Params.class);

        Params expectedParams = new Params();
        expectedParams.addSort(
                new FieldSort(IndexFields.instant, SortOrder.DESC, MissingValues.FIRST, SortMode.SUM)
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void deserializeDefaultWithParams() {
        String jsonParams = "{\r\n" +
                "  \"sort\" : [ {\r\n" +
                "    \"order\" : \"DESC\",\r\n" +
                "    \"field\" : \"instant\",\r\n" +
                "    \"missing\" : \"FIRST\",\r\n" +
                "    \"sortMode\" : \"SUM\"\r\n" +
                "  } ],\r\n" +
                "  \"offset\" : null,\r\n" +
                "  \"size\" : 10,\r\n" +
                "  \"searchAfter\" : null,\r\n" +
                "  \"flipDirection\" : false,\r\n" +
                "  \"filters\" : [ ],\r\n" +
                "  \"aggregations\" : [ ],\r\n" +
                "  \"fields\" : null,\r\n" +
                "  \"include\" : [ ]\r\n" +
                "}";
        Params params = JsonUtils.fromJsonString(jsonParams, Params.class);

        Params expectedParams = new Params();
        expectedParams.addSort(
                new FieldSort(IndexFields.instant, SortOrder.DESC, MissingValues.FIRST, SortMode.SUM)
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void keywordAsc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.uuidId, SortOrder.ASC)
        );
        
        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_1, entity_2);
    }

    @Test
    void keywordAscNullLast() {
        entity_3.setUuidId(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.uuidId, SortOrder.ASC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_1, entity_2, entity_3);
    }

    @Test
    void keywordAscNullFirst() {
        entity_3.setUuidId(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.uuidId, SortOrder.ASC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_1, entity_2);
    }

    @Test
    void keywordDesc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.uuidId, SortOrder.DESC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_1, entity_3);
    }

    @Test
    void keywordDescNullLast() {
        entity_3.setUuidId(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.uuidId, SortOrder.DESC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_1, entity_3);
    }

    @Test
    void keywordDescNullFirst() {
        entity_3.setUuidId(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.uuidId, SortOrder.DESC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_2, entity_1);
    }

    @Test
    void textAsc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.longString, SortOrder.ASC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_1, entity_2);
    }

    @Test
    void textAscNullLast() {
        entity_3.setLongString(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.longString, SortOrder.ASC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_1, entity_2, entity_3);
    }

    @Test
    void textAscNullFirst() {
        entity_3.setLongString(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.longString, SortOrder.ASC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_1, entity_2);
    }

    @Test
    void textDesc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.longString, SortOrder.DESC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_1, entity_3);
    }

    @Test
    void textDescNullLast() {
        entity_3.setLongString(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.longString, SortOrder.DESC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_1, entity_3);
    }

    @Test
    void textDescNullFirst() {
        entity_3.setLongString(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.longString, SortOrder.DESC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_2, entity_1);
    }

    @Test
    void integerAsc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.integerObject, SortOrder.ASC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_1, entity_3, entity_2);
    }

    @Test
    void integerAscNullLast() {
        entity_3.setIntegerObject(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.integerObject, SortOrder.ASC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_1, entity_2, entity_3);
    }

    @Test
    void integerAscNullFirst() {
        entity_3.setIntegerObject(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.integerObject, SortOrder.ASC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_1, entity_2);
    }

    @Test
    void integerDesc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.integerObject, SortOrder.DESC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_3, entity_1);
    }

    @Test
    void integerDescNullLast() {
        entity_3.setIntegerObject(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.integerObject, SortOrder.DESC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_1, entity_3);
    }

    @Test
    void integerDescNullFirst() {
        entity_3.setIntegerObject(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.integerObject, SortOrder.DESC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_2, entity_1);
    }

    @Test
    void longAsc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.longObject, SortOrder.ASC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_1, entity_2);
    }

    @Test
    void longAscNullLast() {
        entity_3.setLongObject(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.longObject, SortOrder.ASC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_1, entity_2, entity_3);
    }

    @Test
    void longAscNullFirst() {
        entity_3.setLongObject(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.longObject, SortOrder.ASC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_1, entity_2);
    }

    @Test
    void longDesc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.longObject, SortOrder.DESC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_1, entity_3);
    }

    @Test
    void longDescNullLast() {
        entity_3.setLongObject(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.longObject, SortOrder.DESC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_1, entity_3);
    }

    @Test
    void longDescNullFirst() {
        entity_3.setLongObject(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.longObject, SortOrder.DESC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_2, entity_1);
    }

    @Test
    void decimalAsc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.bigDecimal, SortOrder.ASC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_1, entity_3);
    }

    @Test
    void decimalAscNullLast() {
        entity_3.setBigDecimal(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.bigDecimal, SortOrder.ASC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_1, entity_3);
    }

    @Test
    void decimalAscNullFirst() {
        entity_3.setBigDecimal(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.bigDecimal, SortOrder.ASC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_2, entity_1);
    }

    @Test
    void decimalDesc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.bigDecimal, SortOrder.DESC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_1, entity_2);
    }

    @Test
    void decimalDescNullLast() {
        entity_3.setBigDecimal(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.bigDecimal, SortOrder.DESC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_1, entity_2, entity_3);
    }

    @Test
    void decimalDescNullFirst() {
        entity_3.setBigDecimal(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.bigDecimal, SortOrder.DESC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_1, entity_2);
    }

    @Test
    void doubleAsc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.doubleObject, SortOrder.ASC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_1, entity_2, entity_3);
    }

    @Test
    void doubleAscNullLast() {
        entity_3.setDoubleObject(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.doubleObject, SortOrder.ASC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_1, entity_2, entity_3);
    }

    @Test
    void doubleAscNullFirst() {
        entity_3.setDoubleObject(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.doubleObject, SortOrder.ASC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_1, entity_2);
    }

    @Test
    void doubleDesc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.doubleObject, SortOrder.DESC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_2, entity_1);
    }

    @Test
    void doubleDescNullLast() {
        entity_3.setDoubleObject(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.doubleObject, SortOrder.DESC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_1, entity_3);
    }

    @Test
    void doubleDescNullFirst() {
        entity_3.setDoubleObject(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.doubleObject, SortOrder.DESC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_2, entity_1);
    }

    @Test
    void dateAsc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.localDate, SortOrder.ASC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_1, entity_3);
    }

    @Test
    void dateAscNullLast() {
        entity_3.setLocalDate(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.localDate, SortOrder.ASC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_1, entity_3);
    }

    @Test
    void dateAscNullFirst() {
        entity_3.setLocalDate(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.localDate, SortOrder.ASC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_2, entity_1);
    }

    @Test
    void dateDesc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.localDate, SortOrder.DESC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_1, entity_2);
    }

    @Test
    void dateDescNullLast() {
        entity_3.setLocalDate(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.localDate, SortOrder.DESC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_1, entity_2, entity_3);
    }

    @Test
    void dateDescNullFirst() {
        entity_3.setLocalDate(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.localDate, SortOrder.DESC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_1, entity_2);
    }

    @Test
    void dateTimeAsc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.localDateTime, SortOrder.ASC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_2, entity_1);
    }

    @Test
    void dateTimeAscNullLast() {
        entity_3.setLocalDateTime(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.localDateTime, SortOrder.ASC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_1, entity_3);
    }

    @Test
    void dateTimeAscNullFirst() {
        entity_3.setLocalDateTime(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.localDateTime, SortOrder.ASC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_2, entity_1);
    }

    @Test
    void dateTimeDesc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.localDateTime, SortOrder.DESC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_1, entity_2, entity_3);
    }

    @Test
    void dateTimeDescNullLast() {
        entity_3.setLocalDateTime(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.localDateTime, SortOrder.DESC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_1, entity_2, entity_3);
    }

    @Test
    void dateTimeDescNullFirst() {
        entity_3.setLocalDateTime(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.localDateTime, SortOrder.DESC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_1, entity_2);
    }

    @Test
    void booleanAsc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.booleanObject, SortOrder.ASC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_1, entity_3);
    }

    @Test
    void booleanAscNullLast() {
        entity_3.setBooleanObject(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.booleanObject, SortOrder.ASC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_1, entity_3);
    }

    @Test
    void booleanAscNullFirst() {
        entity_3.setBooleanObject(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.booleanObject, SortOrder.ASC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_2, entity_1);
    }

    @Test
    void booleanDesc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.booleanObject, SortOrder.DESC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_1, entity_2, entity_3);
    }

    @Test
    void booleanDescNullLast() {
        entity_3.setBooleanObject(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.booleanObject, SortOrder.DESC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_1, entity_2, entity_3);
    }


    @Test
    void booleanDescNullFirst() {
        entity_3.setBooleanObject(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.booleanObject, SortOrder.DESC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_1, entity_2);
    }

    @Test
//    @Disabled("FieldSort of collection of primitive type is not supported well.") todo
    void listAsc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.elementCollection, SortOrder.ASC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_1, entity_3, entity_2);
    }

    @Test
//    @Disabled("FieldSort of collection of primitive type is not supported well.") todo
    void listDesc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.elementCollection, SortOrder.DESC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_3, entity_1);
    }

    @Test
    void enumAsc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.tstEnumString, SortOrder.ASC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_1, entity_3);
    }

    @Test
    void enumAscNullLast() {
        entity_3.setTstEnumString(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.tstEnumString, SortOrder.ASC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_1, entity_3);
    }

    @Test
    void enumAscNullFirst() {
        entity_3.setTstEnumString(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.tstEnumString, SortOrder.ASC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_2, entity_1);
    }

    @Test
    void enumDesc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.tstEnumString, SortOrder.DESC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_1, entity_2);
    }

    @Test
    void enumDescNullLast() {
        entity_3.setTstEnumString(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.tstEnumString, SortOrder.DESC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_1, entity_2, entity_3);
    }

    @Test
    void enumDescNullFirst() {
        entity_3.setTstEnumString(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.tstEnumString, SortOrder.DESC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_1, entity_2);
    }

    @Test
    void objectAsc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.toOneRelationshipNested + "." + value, SortOrder.ASC)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_1, entity_2);
    }

    @Test
    void objectAscNullLast() {
        entity_3.getToOneRelationship().setValue(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.toOneRelationshipNested + "." + value, SortOrder.ASC, MissingValues.LAST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_1, entity_2, entity_3);
    }

    @Test
    void objectAscNullFirst() {
        entity_3.getToOneRelationship().setValue(null);
        repository.update(entity_3);

        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.toOneRelationshipNested + "." + value, SortOrder.ASC, MissingValues.FIRST)
        );

        assertMatchesOrder(() -> repository.listByParams(params), entity_3, entity_1, entity_2);
    }

    @Test
    void objectDesc() {
        Params params = new Params();
        params.addSort(
                new FieldSort(IndexFields.toOneRelationshipNested + "." + value, SortOrder.DESC));

        assertMatchesOrder(() -> repository.listByParams(params), entity_2, entity_1, entity_3);
    }

    @Test
    void sortFieldNotMapped() {
        Params params = new Params();
        params.addSort(
                new FieldSort("nonMapped", SortOrder.DESC)
        );

        assertThrows(IndexException.class, () -> repository.listByParams(params));
    }

    @Test
    void filterFieldNotSortable() {
        Params params = new Params();
        params.addSort(new FieldSort(IndexFields.toOneRelationshipNested, SortOrder.ASC));

        assertThrows(IndexException.class, () -> repository.listByParams(params));
    }
}
