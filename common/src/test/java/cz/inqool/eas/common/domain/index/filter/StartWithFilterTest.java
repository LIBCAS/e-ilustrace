package cz.inqool.eas.common.domain.index.filter;

import cz.inqool.eas.common.dao.simple.multiple.MultipleFieldsIndexedObject.IndexFields;
import cz.inqool.eas.common.domain.index.dto.filter.Filter;
import cz.inqool.eas.common.domain.index.dto.filter.NestedFilter;
import cz.inqool.eas.common.domain.index.dto.filter.StartWithFilter;
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

class StartWithFilterTest extends IndexFilterTestBase {

    @Test
    void serialize() {
        Filter filter = new StartWithFilter(IndexFields.uuidId, "3daf125");

        String jsonFilter = JsonUtils.toJsonString(filter, true);
        String expectedJsonFilter = "{\r\n" +
                "  \"operation\" : \"START_WITH\",\r\n" +
                "  \"field\" : \"uuidId\",\r\n" +
                "  \"value\" : \"3daf125\",\r\n" +
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
                new StartWithFilter(IndexFields.uuidId, "3daf125")
        );

        String jsonParams = JsonUtils.toJsonString(params, true);
        String expectedJsonParams = "{\r\n" +
                "  \"sort\" : [ ],\r\n" +
                "  \"offset\" : null,\r\n" +
                "  \"size\" : 10,\r\n" +
                "  \"searchAfter\" : null,\r\n" +
                "  \"flipDirection\" : false,\r\n" +
                "  \"filters\" : [ {\r\n" +
                "    \"operation\" : \"START_WITH\",\r\n" +
                "    \"field\" : \"uuidId\",\r\n" +
                "    \"value\" : \"3daf125\",\r\n" +
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
                "  \"operation\" : \"START_WITH\",\r\n" +
                "  \"field\" : \"uuidId\",\r\n" +
                "  \"value\" : \"3daf125\",\r\n" +
                "  \"modifier\" : null,\r\n" +
                "  \"useFolding\" : true,\r\n" +
                "  \"lowercase\" : true,\r\n" +
                "  \"asciiFolding\" : true\r\n" +
                "}";
        Filter filter = JsonUtils.fromJsonString(jsonFilter, Filter.class);

        Filter expectedFilter = new StartWithFilter(IndexFields.uuidId, "3daf125");

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
                "    \"operation\" : \"START_WITH\",\r\n" +
                "    \"field\" : \"uuidId\",\r\n" +
                "    \"value\" : \"3daf125\",\r\n" +
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
                new StartWithFilter(IndexFields.uuidId, "3daf125")
        );

        assertThat(params).isEqualToComparingFieldByField(expectedParams);
    }

    @Test
    void keywordStartWith_first() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.uuidId, "3daf125")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordStartWith_second() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.uuidId, "fe5c07c")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void keywordStartWith_all() {
        entity_2.setUuidId(entity_1.getUuidId());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.uuidId, "3daf125")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void keywordStartWith_none() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.uuidId, "random Text")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void keywordStartWithWholeValue_first() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.uuidId, entity_1.getUuidId().toString())
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordStartWithEmpty_all() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.uuidId, "")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void keywordStartWithCaseSensitive_none() {
        // Keyword field is not analyzed - no tokenizer or filter is applied."
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.uuidId, "3DAF")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    @Disabled("Not working properly") // fixme
    void keywordStartWithFuzzy_first() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.uuidId, "3caf125", FUZZY)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordStartWithWildcardStar_first() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.uuidId, "3daf1*", WILDCARD)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordStartWithWildCardQuestionMark_first() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.uuidId, "3da??258", WILDCARD)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void keywordStartWithRegExp_second() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.uuidId, "[a-zA-Z_0-9]{8}-e03c", REGEXP)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void keywordStartWithRegExp_all() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.uuidId, "[a-zA-Z_0-9]{8}-", REGEXP)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void keywordStartWithRegExp_none() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.uuidId, "[a-zA-Z_0-9]{9}-")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void textStartWith_first() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.longString, "Probably more than so")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textStartWith_second() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.longString, "You are unbeliev")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void textStartWith_all() {
        entity_2.setLongString(entity_1.getLongString());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.longString, "Probably more than some two w")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void textStartWith_none() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.longString, "want to write this")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void accentTextStartWith_first() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.shortString, "Obecní úřa")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void accentTextStartWith_second() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.shortString, "Případ k")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void extraLongTextStartWith_first() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.blobText, "FAKULTA SOCIÁLNÍC")
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textStartWithWholeValue_first() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.longString, entity_1.getLongString())
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textStartWithEmpty_all() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.longString, "")
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void textStartWithCaseSensitive_second() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.longString, "You are UNBELIEVABLE")
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    @Disabled("Fuzzy search is not supported well in StartWith filter") // fixme
    void textStartWithFuzzy_first() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.longString, "Probebly more than some", FUZZY)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textStartWithWildcardStar_first() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.longString, "Pro*re than some", WILDCARD)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textStartWithWildcardQuestionMark_first() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.longString, "Probab?? more than some", WILDCARD)
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void textStartWithRegExp_second() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.longString, "You [are]{3}", REGEXP)
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void textStartWithRegExp_all() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.longString, "(Yo)|(Pr).+", REGEXP)
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void textStartWithRegExpEscaped_none() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.longString, "You [are]{3}")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectStartWith_first() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toOneRelationshipNested,
                        new StartWithFilter(IndexFields.toOneRelationshipNested + "." + key, "Grandfather")
                )
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectStartWith_second() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toOneRelationshipNested,
                        new StartWithFilter(IndexFields.toOneRelationshipNested + "." + key, "Grandmother")
                )
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectStartWith_all() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toOneRelationshipNested,
                        new StartWithFilter(IndexFields.toOneRelationshipNested + "." + key, "Grand")
                )
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void nestedObjectStartWith_none() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.toOneRelationshipNested + "." + key, "mother")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void nestedListStartWith_first() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toManyRelationship,
                        new StartWithFilter(IndexFields.toManyRelationship + "." + key, "Son")
                )
        );

        assertMatchesFirst(() -> repository.listByParams(params));
    }

    @Test
    void nestedListsStartWith_second() {
        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toManyRelationship,
                        new StartWithFilter(IndexFields.toManyRelationship + "." + key, "Daughter")
                )
        );

        assertMatchesSecond(() -> repository.listByParams(params));
    }

    @Test
    void nestedListStartWith_all() {
        entity_2.setToManyRelationship(entity_1.getToManyRelationship());
        repository.update(entity_2);

        Params params = new Params();
        params.addFilter(
                new NestedFilter(
                        IndexFields.toManyRelationship,
                        new StartWithFilter(IndexFields.toManyRelationship + "." + key, "Son")
                )
        );

        assertMatchesBoth(() -> repository.listByParams(params));
    }

    @Test
    void nestedListStartWith_none() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.toManyRelationship + "." + key, "Pet")
        );

        assertMatchesNone(() -> repository.listByParams(params));
    }

    @Test
    void filterFieldNotMapped() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter("nonMapped", "Hammer")
        );

        assertThrows(IndexException.class, () -> repository.listByParams(params));
    }

    @Test
    void filterFieldNotLeaf() {
        Params params = new Params();
        params.addFilter(
                new StartWithFilter(IndexFields.toOneRelationshipNested, "47")
        );

        assertThrows(IndexException.class, () -> repository.listByParams(params));
    }
}
