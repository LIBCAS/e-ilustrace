package cz.inqool.eas.common.domain.index.filter;

import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject.IndexFields;
import cz.inqool.eas.common.domain.index.dto.filter.ContainsFilter;
import cz.inqool.eas.common.domain.index.dto.filter.Filter;
import cz.inqool.eas.common.domain.index.dto.filter.NestedFilter;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.exception.v2.IndexException;
import cz.inqool.eas.common.utils.JsonUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static cz.inqool.eas.common.dao.simple.keyvalue.SimpleKeyValueIndexedObject.IndexFields.key;
import static cz.inqool.eas.common.domain.index.dto.filter.TextFilter.Modifier.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContainsFilterTest extends IndexFilterTestBase {

    @Test
    void serialize() {
        Filter filter = new ContainsFilter(IndexFields.uuidId, "79b8-40b4");

        String jsonFilter = JsonUtils.toJsonString(filter, true);
        String expectedJsonFilter = "{\r\n" +
                "  \"operation\" : \"CONTAINS\",\r\n" +
                "  \"field\" : \"uuidId\",\r\n" +
                "  \"value\" : \"79b8-40b4\",\r\n" +
                "  \"modifier\" : null,\r\n" +
                "  \"useFolding\" : true,\r\n" +
                "  \"lowercase\" : true,\r\n" +
                "  \"asciiFolding\" : true\r\n" +
                "}";

        assertEquals(expectedJsonFilter, jsonFilter);
    }

    @Test
    void serializeWithParams() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.uuidId, "79b8-40b4")
        );

        String jsonParams = JsonUtils.toJsonString(params, true);
        String expectedJsonParams = "{\r\n" +
                "  \"sort\" : [ ],\r\n" +
                "  \"offset\" : null,\r\n" +
                "  \"size\" : 10,\r\n" +
                "  \"searchAfter\" : null,\r\n" +
                "  \"flipDirection\" : false,\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"CONTAINS\",\r\n" +
                "    \"field\" : \"uuidId\",\r\n" +
                "    \"value\" : \"79b8-40b4\",\r\n" +
                "    \"modifier\" : null,\r\n" +
                "    \"useFolding\" : true,\r\n" +
                "    \"lowercase\" : true,\r\n" +
                "    \"asciiFolding\" : true\r\n" +
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
                "  \"operation\" : \"CONTAINS\",\r\n" +
                "  \"field\" : \"uuidId\",\r\n" +
                "  \"value\" : \"79b8-40b4\",\r\n" +
                "  \"modifier\" : null,\r\n" +
                "  \"useFolding\" : true,\r\n" +
                "  \"lowercase\" : true,\r\n" +
                "  \"asciiFolding\" : true\r\n" +
                "}";
        Filter filter = JsonUtils.fromJsonString(jsonFilter, Filter.class);

        Filter expectedFilter = new ContainsFilter(IndexFields.uuidId, "79b8-40b4");

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
                "    \"operation\" : \"CONTAINS\",\r\n" +
                "    \"field\" : \"uuidId\",\r\n" +
                "    \"value\" : \"79b8-40b4\",\r\n" +
                "    \"modifier\" : null,\r\n" +
                "    \"useFolding\" : true,\r\n" +
                "    \"lowercase\" : true,\r\n" +
                "    \"asciiFolding\" : true\r\n" +
                "  } ],\r\n" +
                "  \"aggregations\" : [ ],\r\n" +
                "  \"fields\" : null,\r\n" +
                "  \"include\" : [ ],\r\n" +
                "  \"exclude\" : [ ]\r\n" +
                "}";
        Params params = JsonUtils.fromJsonString(jsonParams, Params.class);

        Params expectedParams = new Params();
        expectedParams.addFilter(
                new ContainsFilter(IndexFields.uuidId, "79b8-40b4")
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void keywordContains_first() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.uuidId, "79b8-40b4")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordContains_second() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.uuidId, "e03c-4a41-")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void keywordContains_all() {
        entity_2.setUuidId(entity_1.getUuidId());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.uuidId, "79b8-40b4")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void keywordContains_none() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.uuidId, "random Text")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void keywordContainsWholeValue_first() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.uuidId, entity_1.getUuidId().toString())
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordContainsEmpty_all() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.uuidId, "")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void keywordContainsCaseSensitive_none() {
        // Keyword field is not analyzed - no tokenizer or filter is applied."
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.uuidId, "DAF1258")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    @Disabled("Not working properly") // fixme
    void keywordContainsFuzzy_first() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.uuidId, "40b4-8e01", FUZZY)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordContainsWildcardStar_first() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.uuidId, "79b*363", WILDCARD)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordContainsWildCardQuestionMark_first() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.uuidId, "79b8-4??4-8f01-363", WILDCARD)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordContainsRegExp_second() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.uuidId, "[a-zA-Z_0-9]{4}-4a41-[a-zA-Z_0-9]{4}", REGEXP)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void keywordContainsRegExp_all() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.uuidId, "[a-zA-Z_0-9]{4}-[a-zA-Z_0-9]{4}-[a-zA-Z_0-9]{4}", REGEXP)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void keywordContainsRegExpEscaped_none() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.uuidId, "[A-z]{14}")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void textContains_first() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.longString, "functionality of ElasticSear")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textContains_second() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.longString, "meaningless piec")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void textContains_all() {
        entity_2.setLongString(entity_1.getLongString());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.longString, "ore than some two words because")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void textContains_none() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.longString, "random Text")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void textContainsLowercase_first() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.longString, "functionality of elasticsear")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textNoLowercaseContains_none() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.longString, "functionality of ElasticSear").disableLowercase()
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void textNoLowercaseContainsLowercase_first() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.longString, "functionality of elasticsear").disableLowercase()
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void accentTextContains_first() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.shortString, "cní úřad Błu")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void accentTextContainsNoInterpunction_first() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.shortString, "cni urad Blu")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void accentTextContains_second() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.shortString, "omisáře Rex")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void accentTextNoAsciiFoldingContains_none() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.shortString, "cní úřad Błu").disableAsciiFolding()
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void accentTextNoAsciiFoldingContainsNoInterpunction_first() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.shortString, "cni urad Blu").disableAsciiFolding()
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void extraLongTextContains_first() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.blobText, "konfirmační")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textContainsWholeValue_first() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.longString, entity_1.getLongString())
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textContainsEmpty_all() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.longString, "")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void textContainsCaseSensitive_second() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.longString, "DON'T WANT to WRIT")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void textContainsFuzzy_first() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.longString, "bacause", FUZZY)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textContainsWildcardStar_first() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.longString, "w*ds", WILDCARD)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textContainsWildcardQuestionMark_first() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.longString, "w??ds", WILDCARD)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textContainsRegExp_second() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.longString, "m[A-z]{5,8}ss", REGEXP)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void textContainsRegExp_all() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.longString, ".*", REGEXP)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void textContainsRegExpEscaped_none() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.longString, "m[A-z]{5,8}ss")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectContains_first() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toOneRelationshipNested,
                        new ContainsFilter(IndexFields.toOneRelationshipNested + "." + key, "father")
                )
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectContains_second() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toOneRelationshipNested,
                        new ContainsFilter(IndexFields.toOneRelationshipNested + "." + key, "mother")
                )
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectContains_all() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toOneRelationshipNested,
                        new ContainsFilter(IndexFields.toOneRelationshipNested + "." + key, "rand")
                )
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectContains_none() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.toOneRelationshipNested + "." + key, "random Text")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void nestedListContains_first() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toManyRelationship,
                        new ContainsFilter(IndexFields.toManyRelationship + "." + key, "Mar")
                )
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void nestedListContains_second() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toManyRelationship,
                        new ContainsFilter(IndexFields.toManyRelationship + "." + key, "harlot")
                )
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void nestedListContains_all() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toManyRelationship,
                        new ContainsFilter(IndexFields.toManyRelationship + "." + key, "r")
                )
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void nestedListContains_none() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.toManyRelationship + "." + key, "random Text")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void filterFieldNotMapped() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter("nonMapped", "Hammer")
        );

        assertThrows(IndexException.class, () -> repository.listByParams(params));
    }

    @Test
    void filterFieldNotLeaf() {
        Params params = new Params();
        params.addFilter(
                new ContainsFilter(IndexFields.toOneRelationshipNested, "47")
        );

        assertThrows(IndexException.class, () -> repository.listByParams(params));
    }
}
