package cz.inqool.eas.common.domain.index.field;

import org.springframework.data.elasticsearch.annotations.InnerField;

/**
 * ElasticSearch constants
 */
public interface ES {

    /**
     * Custom ElasticSearch analyzers. Analyzers can be defined in {@code es_settings.json} file in resource folder.
     */
    interface Analyzer {

        /**
         * Analyzer for short text fields. Will treat them as keywords - without tokenizing and folding. Limits the
         * length of the string to max token value (30 000 chars).
         * <p>
         * Can be used on fields where operations EQ, CONTAINS, START WITH, ENDS WITH are crucial and need to function
         * properly.
         */
        String TEXT_SHORT_KEYWORD = "text_short_keyword";

        /**
         * Analyzer for long text fields. Will treat them as keywords - without tokenizing and folding. Limits the
         * length of the string to 200 chars.
         * <p>
         * Can be used on fields where exact comparison is not needed (such as long descriptions or notes) and only
         * full-text search is used.
         */
        String TEXT_LONG_KEYWORD = "text_long_keyword";

        /**
         * Applies ICU filters (lowercase, folding, ...) and not tokenize words (Keyword indexing).
         */
        String FOLDING = "folding";

        /**
         * Applies ICU filters (lowercase, folding, ...) and tokenize words (Text indexing) without stop-word filter.
         * Suitable for one line names.
         */
        String FOLDING_AND_TOKENIZING = "folding_and_tokenizing";

        /**
         * Applies ICU filters (lowercase, folding, ...) and tokenize words (Text indexing) with stop-word filter.
         * Suitable for longer multi-line text.
         */
        String FOLDING_AND_TOKENIZING_STOP = "folding_and_tokenizing_stop";

        /**
         * Applies only ICU collation to properly sort regional characters. No tokenizer is applied (so whole field is
         * stored in one token)
         */
        String SORTING = "sorting";
    }

    /**
     * Predefined ElasticSearch suffixes for {@link InnerField}s.
     */
    interface Suffix {

        /**
         * The field uses lowercase asciifolding with no tokenizer
         */
        String FOLD = "fold";

        /**
         * The field uses lowercase asciifolding with standard tokenizer
         */
        String SEARCH = "search";

        /**
         * The field uses ICU collation to sort regional characters properly with no tokenizer
         */
        String SORT = "sort";
    }
}
