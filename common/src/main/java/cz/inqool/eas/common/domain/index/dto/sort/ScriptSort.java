package cz.inqool.eas.common.domain.index.dto.sort;

import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.ScriptSortBuilder.ScriptSortType;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortMode;
import org.elasticsearch.search.sort.SortOrder;

import javax.validation.constraints.NotNull;

import static cz.inqool.eas.common.domain.index.dto.sort.Sort.Type.SCRIPT;

/**
 * Script Sort
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/sort-search-results.html#script-based-sorting">Script Based Sorting</a>
 */
@Schema(
        description = "Allows to define sorting using custom script.",
        externalDocs = @ExternalDocumentation(
                description = "Script Based Sorting",
                url = "https://www.elastic.co/guide/en/elasticsearch/reference/current/sort-search-results.html#script-based-sorting"
        )
)
@Getter
@Setter
public class ScriptSort extends AbstractSort<ScriptSortBuilder> {

    /**
     * Sort type
     */
    @NotNull
    @Schema(allowableValues = {SCRIPT})
    private final String type = SCRIPT;

    /**
     * Script
     */
    @NotNull
    @Schema(description = "Script.")
    protected Script script;

    /**
     * Script sort type
     */
    @NotNull
    @Schema(description = "Script sort type.")
    protected ScriptSortType scriptSortType;

    /**
     * Sort mode.
     */
    @NotNull
    @Schema(description = "Sort mode.")
    protected SortMode mode;


    ScriptSort() {
    }

    @Builder
    public ScriptSort(@NotNull Script script, @NotNull ScriptSortType scriptSortType, @NotNull SortOrder order, @NotNull SortMode mode) {
        super(order);
        this.script = script;
        this.scriptSortType = scriptSortType;
        this.mode = mode;
    }


    @Override
    public org.elasticsearch.search.sort.ScriptSortBuilder toSortBuilder(IndexObjectFields indexedFields) {
        return SortBuilders
                .scriptSort(script, scriptSortType)
                .order(order)
                .sortMode(mode);
    }

    @Override
    public Sort<org.elasticsearch.search.sort.ScriptSortBuilder> withReversedOrder() {
        return new ScriptSort(script, scriptSortType, getReversedOrder(), mode);
    }
}
