package cz.inqool.eas.common.domain.index.dto.filter;

import com.google.common.annotations.Beta;
import cz.inqool.eas.common.domain.index.QueryUtils;
import cz.inqool.eas.common.domain.index.field.ES;
import cz.inqool.eas.common.domain.index.field.IndexFieldLeafNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Filter representing a comparison filter condition on given text {@link FieldFilter#field}.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
abstract public class TextFilter<FILTER extends TextFilter<FILTER>> extends FieldValueFilter<FILTER> {

    /**
     * Modifies the operation execution behaviour
     */
    protected Modifier modifier;

    /**
     * Indicates whether use folding
     */
    protected Boolean useFolding = true;

    /**
     * Indicates whether to perform lowercase folding on {@link #value}.
     *
     * {@link #useFolding} has to be {@code true} for this to be taken into account.
     */
    protected Boolean lowercase = true;

    /**
     * Indicates whether to perform ASCII folding on {@link #value}.
     *
     * {@link #useFolding} has to be {@code true} for this to be taken into account.
     */
    protected Boolean asciiFolding = true;


    protected TextFilter(@NotNull String operation) {
        super(operation);
    }

    protected TextFilter(@NotNull String operation, @NotBlank String field, @NotBlank String value, Modifier modifier) {
        super(operation, field, value);
        this.modifier = modifier;
    }


    /**
     * Performs folding operation on this filter {@link #value}.
     *
     * Regexp and wildcard query do not analyze given query string, so it needs to be folded in the application.
     */
    protected String foldValue(IndexFieldLeafNode indexField) {
        boolean hasDedicatedFoldField = indexField.hasInnerField(ES.Suffix.FOLD);
        boolean mainFieldIsFolded = indexField.getType() == FieldType.Text && indexField.getAnalyzer() != null && (indexField.getAnalyzer().isEmpty() || indexField.getAnalyzer().contains("folding"));

        String foldedValue = value;
        if (useFolding && (hasDedicatedFoldField || mainFieldIsFolded)) {
            if (lowercase) {
                foldedValue = QueryUtils.lowercase(foldedValue);
            }
            if (asciiFolding) {
                foldedValue = QueryUtils.asciiFolding(foldedValue);
            }
        }

        return foldedValue;
    }

    public FILTER disableLowercase() {
        return lowercase(false);
    }

    public FILTER lowercase(boolean enabled) {
        lowercase = enabled;
        //noinspection unchecked
        return (FILTER) this;
    }

    public FILTER disableAsciiFolding() {
        return asciiFolding(false);
    }

    public FILTER asciiFolding(boolean enabled) {
        asciiFolding = enabled;
        //noinspection unchecked
        return (FILTER) this;
    }


    /**
     * Filter operation modifier for text values.
     */
    public enum Modifier {

        /**
         * Enables fuzzy searching.
         *
         * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.8/query-dsl-fuzzy-query.html#query-dsl-fuzzy-query">Fuzzy Query</a>
         */
        @Beta
        FUZZY,

        /**
         * Enables wildcard characters:
         * <ul>
         *   <li><strong>*</strong>: matches any character sequence (including the empty one)</li>
         *   <li><strong>?</strong>: matches any single character</li>
         * </ul>
         *
         * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.8/query-dsl-wildcard-query.html#query-dsl-wildcard-query">Wildcard Query</a>
         */
        WILDCARD,

        /**
         * Enables querying with regular expressions.
         *
         * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.8/query-dsl-regexp-query.html">Regexp Query</a>
         */
        REGEXP
    }
}
