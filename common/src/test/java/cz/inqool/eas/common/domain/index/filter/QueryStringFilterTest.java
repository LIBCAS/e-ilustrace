package cz.inqool.eas.common.domain.index.filter;

import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsEntity.Fields;
import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject.IndexFields;
import cz.inqool.eas.common.domain.index.dto.filter.EqFilter;
import cz.inqool.eas.common.domain.index.dto.filter.Filter;
import cz.inqool.eas.common.domain.index.dto.filter.QueryStringFilter;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.exception.v2.IndexException;
import cz.inqool.eas.common.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QueryStringFilterTest extends IndexFilterTestBase {

    @Test
    void serialize() {
        Filter filter = new QueryStringFilter("(new york city) OR (big apple)");

        String jsonFilter = JsonUtils.toJsonString(filter, true);
        String expectedJsonFilter = "{\r\n" +
                "  \"operation\" : \"QUERY_STRING\",\r\n" +
                "  \"value\" : \"(new york city) OR (big apple)\",\r\n" +
                "  \"defaultOperator\" : \"OR\",\r\n" +
                "  \"fields\" : null\r\n" +
                "}";

        assertEquals(expectedJsonFilter, jsonFilter);


        filter = new QueryStringFilter("(new york city) OR (big apple)").addField(Fields.longString, 4F);

        jsonFilter = JsonUtils.toJsonString(filter, true);
        expectedJsonFilter = "{\r\n" +
                "  \"operation\" : \"QUERY_STRING\",\r\n" +
                "  \"value\" : \"(new york city) OR (big apple)\",\r\n" +
                "  \"defaultOperator\" : \"OR\",\r\n" +
                "  \"fields\" : {\r\n" +
                "    \"longString\" : 4.0\r\n" +
                "  }\r\n" +
                "}";

        assertEquals(expectedJsonFilter, jsonFilter);
    }

    @Test
    void serializeWithParams() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("(new york city) OR (big apple)").addField(Fields.longString, 4F)
        );

        String jsonParams = JsonUtils.toJsonString(params, true);
        String expectedJsonParams = "{\r\n" +
                "  \"sort\" : [ ],\r\n" +
                "  \"offset\" : null,\r\n" +
                "  \"size\" : 10,\r\n" +
                "  \"searchAfter\" : null,\r\n" +
                "  \"flipDirection\" : false,\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"QUERY_STRING\",\r\n" +
                "    \"value\" : \"(new york city) OR (big apple)\",\r\n" +
                "    \"defaultOperator\" : \"OR\",\r\n" +
                "    \"fields\" : {\r\n" +
                "      \"longString\" : 4.0\r\n" +
                "    }\r\n" +
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
                "  \"operation\" : \"QUERY_STRING\",\r\n" +
                "  \"value\" : \"(new york city) OR (big apple)\",\r\n" +
                "  \"fields\" : null\r\n" +
                "}";
        Filter filter = JsonUtils.fromJsonString(jsonFilter, Filter.class);

        Filter expectedFilter = new QueryStringFilter("(new york city) OR (big apple)");

        assertThat(filter).isEqualToComparingFieldByField(expectedFilter);


        jsonFilter = "{\r\n" +
                "  \"operation\" : \"QUERY_STRING\",\r\n" +
                "  \"value\" : \"(new york city) OR (big apple)\",\r\n" +
                "  \"fields\" : {\r\n" +
                "    \"longString\" : 4.0\r\n" +
                "  }\r\n" +
                "}";
        filter = JsonUtils.fromJsonString(jsonFilter, Filter.class);

        expectedFilter = new QueryStringFilter("(new york city) OR (big apple)").addField(Fields.longString, 4F);

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
                "    \"operation\" : \"QUERY_STRING\",\r\n" +
                "    \"value\" : \"(new york city) OR (big apple)\",\r\n" +
                "    \"fields\" : {\r\n" +
                "      \"longString\" : 4.0\r\n" +
                "    }\r\n" +
                "  } ],\r\n" +
                "  \"aggregations\" : [ ],\r\n" +
                "  \"fields\" : null,\r\n" +
                "  \"include\" : [ ],\r\n" +
                "  \"exclude\" : [ ]\r\n" +
                "}";
        Params params = JsonUtils.fromJsonString(jsonParams, Params.class);

        Params expectedParams = new Params();
        expectedParams.addFilter(
                new QueryStringFilter("(new york city) OR (big apple)").addField(Fields.longString, 4F)
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void matchWord_first() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("searching")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void matchWord_second() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("meaningless")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void matchWord_all() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("this")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void matchWord_none() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("universe")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void matchTwoWords_first() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("Lorem special")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void matchTwoWords_second() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("unbelievable piece")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void exactPhrase_first() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("\"Błukovina\"")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void exactPhraseCaseInsensitive_first() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("\"blukovina\"")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void exactPhrase_second() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("\"meaningless\"")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void exactPhrase_all() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("\"this\"")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void exactPhrase_none() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("\"cryptonite\"")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void simpleAndQuery_first() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("(Lorem) AND (special)")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void simpleAndQuery_second() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("(unbelievable) AND (making)")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void simpleAndQuery_all() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("(this) AND (me)")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void simpleAndQuery_none() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("(truck) AND (plane)")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void simpleOrQuery_first() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("(sentence generator) OR (something else)")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void simpleOrQuery_second() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("(\"something else\") OR (unbelievable)")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void simpleOrQuery_all() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("(sentence generator) OR (are unbelievable)")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void simpleOrQuery_none() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("(anything) OR (\"something else\")")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void wildcard_first() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("ge?erat*")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void wildcard_second() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("mea*le?s")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void wildcard_all() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("*ing")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void wildcard_none() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("probab??ity")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void not_first() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("-unbelievable")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void not_second() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("-generator")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void not_all() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("-probability")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void not_none() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("-this")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void proximityMatch_first() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("\"best comparison\"~9")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void proximityNoMatch_first() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("\"best comparison\"~7")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void proximityMatch_second() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("\"making unbelievable\"~14")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void proximityNoMatch_second() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("\"making unbelievable\"~12")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void proximityMatch_all() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("\"this me\"~14")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void proximity_none() {
        Params params = new Params();
        params.addFilter(
                new QueryStringFilter("\"this me\"~2")
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