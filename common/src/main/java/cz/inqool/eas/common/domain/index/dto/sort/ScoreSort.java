package cz.inqool.eas.common.domain.index.dto.sort;

import com.google.common.annotations.Beta;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import javax.validation.constraints.NotNull;

import static cz.inqool.eas.common.domain.index.dto.sort.Sort.Type.SCORE;

/**
 * Score Sort
 *
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/sort-search-results.html#sort-search-results">Sort search results</a>
 */
@Beta
@Schema(
        description = "Allows to define sorting by score.",
        externalDocs = @ExternalDocumentation(
                description = "Sort search results",
                url = "https://www.elastic.co/guide/en/elasticsearch/reference/current/sort-search-results.html#sort-search-results"
        )
)
@Getter
@Setter
public class ScoreSort extends AbstractSort<ScoreSortBuilder> {

    /**
     * Sort type
     */
    @NotNull
    @Schema(allowableValues = {SCORE})
    private final String type = SCORE;

    ScoreSort() {
    }

    @Builder
    public ScoreSort(@NotNull SortOrder order) {
        super(order);
    }


    @Override
    public org.elasticsearch.search.sort.ScoreSortBuilder toSortBuilder(IndexObjectFields indexedFields) {
        return SortBuilders
                .scoreSort()
                .order(order);
    }

    @Override
    public Sort<org.elasticsearch.search.sort.ScoreSortBuilder> withReversedOrder() {
        return new ScoreSort(getReversedOrder());
    }
}
