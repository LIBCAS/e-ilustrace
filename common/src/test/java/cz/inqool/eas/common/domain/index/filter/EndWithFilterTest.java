package cz.inqool.eas.common.domain.index.filter;

import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject.IndexFields;
import cz.inqool.eas.common.domain.index.dto.filter.EndWithFilter;
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

class EndWithFilterTest extends IndexFilterTestBase {

    @Test
    void serialize() {
        Filter filter = new EndWithFilter(IndexFields.uuidId, "3e74360956");

        String jsonFilter = JsonUtils.toJsonString(filter, true);
        String expectedJsonFilter = "{\r\n" +
                "  \"operation\" : \"END_WITH\",\r\n" +
                "  \"field\" : \"uuidId\",\r\n" +
                "  \"value\" : \"3e74360956\",\r\n" +
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
                new EndWithFilter(IndexFields.uuidId, "3e74360956")
        );

        String jsonParams = JsonUtils.toJsonString(params, true);
        String expectedJsonParams = "{\r\n" +
                "  \"sort\" : [ ],\r\n" +
                "  \"offset\" : null,\r\n" +
                "  \"size\" : 10,\r\n" +
                "  \"searchAfter\" : null,\r\n" +
                "  \"flipDirection\" : false,\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"END_WITH\",\r\n" +
                "    \"field\" : \"uuidId\",\r\n" +
                "    \"value\" : \"3e74360956\",\r\n" +
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
                "  \"operation\" : \"END_WITH\",\r\n" +
                "  \"field\" : \"uuidId\",\r\n" +
                "  \"value\" : \"3e74360956\",\r\n" +
                "  \"modifier\" : null,\r\n" +
                "  \"useFolding\" : true,\r\n" +
                "  \"lowercase\" : true,\r\n" +
                "  \"asciiFolding\" : true\r\n" +
                "}";
        Filter filter = JsonUtils.fromJsonString(jsonFilter, Filter.class);

        Filter expectedFilter = new EndWithFilter(IndexFields.uuidId, "3e74360956");

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
                "    \"operation\" : \"END_WITH\",\r\n" +
                "    \"field\" : \"uuidId\",\r\n" +
                "    \"value\" : \"3e74360956\",\r\n" +
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
                new EndWithFilter(IndexFields.uuidId, "3e74360956")
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void keywordEndsWith_first() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.uuidId, "3e74360956")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordEndsWith_second() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.uuidId, "f6fcdb38")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void keywordEndsWith_all() {
        entity_2.setUuidId(entity_1.getUuidId());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.uuidId, "74360956")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void keywordEndsWith_none() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.uuidId, "4c20f6fcd")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void keywordEndsWithWholeValue_first() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.uuidId, entity_1.getUuidId().toString())
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordEndsWithEmpty_all() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.uuidId, "")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void keywordEndsWithCaseSensitive_none() {
        // Keyword field is not analyzed - no tokenizer or filter is applied."
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.uuidId, "20f6fCDB38")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    @Disabled("Not working properly") // fixme
    void keywordEndsWithFuzzy_first() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.uuidId, "e74360a56", FUZZY)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordEndsWithWildcardStar_first() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.uuidId, "36*56", WILDCARD)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordEndsWithWildCardQuestionMark_first() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.uuidId, "43???56", WILDCARD)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordEndsWithRegExp_second() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.uuidId, "84ff-[a-zA-Z_0-9]{12}", REGEXP)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void keywordEndsWithRegExp_all() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.uuidId, "-[a-zA-Z_0-9]{12}", REGEXP)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void keywordEndsWithRegExp_none() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.uuidId, "-[a-zA-Z_0-9]{16}")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void textEndsWith_first() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.longString, "aracters in this sentence.")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textEndsWith_second() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.longString, "hings I hate.")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void textEndsWith_all() {
        entity_2.setLongString(entity_1.getLongString());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.longString, "cial characters in this sentence.")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void textEndsWith_none() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.longString, "ipsum is nothing in")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void accentTextEndsWith_first() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.shortString, "cní úřad Błukovina")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void accentTextEndsWith_second() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.shortString, "ře Rexa.")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void textEndsWithWholeValue_first() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.longString, entity_1.getLongString())
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textEndsWithEmpty_all() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.longString, "")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void textEndsWithCaseSensitive_second() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.longString, "o Things I Hate.")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    @Disabled("Fuzzy search is not supported well in EndWith filter") // fixme
    void textEndsWithFuzzy_first() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.longString, "this sentece.", FUZZY)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textEndsWithWildcardStar_first() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.longString, "in this se*.", WILDCARD)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textEndsWithWildcardQuestionMark_first() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.longString, "in t??s sentence.", WILDCARD)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textEndsWithRegExp_second() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.longString, "t[A-z]{5} I .+", REGEXP)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void textEndsWithRegExp_all() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.longString, ".*", REGEXP)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void textEndsWithRegExpEscaped_none() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.longString, ".*")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectEndsWith_first() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toOneRelationshipNested,
                        new EndWithFilter(IndexFields.toOneRelationshipNested + "." + key, "Steve")
                )
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectEndsWith_second() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toOneRelationshipNested,
                        new EndWithFilter(IndexFields.toOneRelationshipNested + "." + key, "Mary")
                )
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectEndsWith_all() {
        entity_2.setToOneRelationship(entity_1.getToOneRelationship());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toOneRelationshipNested,
                        new EndWithFilter(IndexFields.toOneRelationshipNested + "." + key, "Steve")
                )
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectEndsWith_none() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.toOneRelationshipNested + "." + key, "random Text")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void nestedListEndsWith_first() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toManyRelationship,
                        new EndWithFilter(IndexFields.toManyRelationship + "." + key, "Luke")
                )
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void nestedListEndsWith_second() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toManyRelationship,
                        new EndWithFilter(IndexFields.toManyRelationship + "." + key, "Olivia")
                )
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void nestedListEndsWith_all() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toManyRelationship,
                        new EndWithFilter(IndexFields.toManyRelationship + "." + key, "e")
                )
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void nestedListEndsWith_none() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.toManyRelationship + "." + key, "Paul")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void filterFieldNotMapped() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter("nonMapped", "Hammer")
        );

        assertThrows(IndexException.class, () -> repository.listByParams(params));
    }

    @Test
    void filterFieldNotLeaf() {
        Params params = new Params();
        params.addFilter(
                new EndWithFilter(IndexFields.toOneRelationshipNested, "47")
        );

        assertThrows(IndexException.class, () -> repository.listByParams(params));
    }
}
