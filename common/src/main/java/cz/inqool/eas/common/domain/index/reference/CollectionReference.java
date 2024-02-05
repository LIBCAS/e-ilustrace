package cz.inqool.eas.common.domain.index.reference;

import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.domain.index.field.Boost;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

/**
 * Reference to a collection of another objects.
 */
@Getter
@Setter
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CollectionReference {
    /**
     * List of IDs only.
     */
    @Field(type = FieldType.Keyword)
    protected List<String> id;

    /**
     * List of names only.
     */
    @Boost(0)
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    protected List<String> flat;

    /**
     * Combined name from all object names concatenated using ","
     */
    @Boost(0)
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_LONG_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    protected String concatenated;

    /**
     * Count of items in referenced collection
     */
    @Field(type = FieldType.Integer)
    protected int size;


    public static CollectionReference of(Collection<? extends Labeled> array) {
        return CollectionReference.of(array,
                (o) -> o.map(Labeled::getId),
                (o) -> o.map(Labeled::getLabel));
    }

    /**
     * Converts given {@code array} collection of entity to List of names using provided name mapper + create
     * concatenated string.
     *
     * @param labelMapper Mapper from Object type to name
     * @return new instance of collection reference
     */
    public static <ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>>  CollectionReference of(Collection<PROJECTED> array,
                                                                                                      Function<Optional<PROJECTED>, Optional<String>> labelMapper) {
        return of(array, o -> o.map(Domain::getId), labelMapper);
    }

    /**
     * Converts given {@code array} collection of entity to List of names using provided name mapper + create
     * concatenated string.
     *
     * @param labelMapper Mapper from Object type to name
     * @return new instance of collection reference
     */
    public static <T> CollectionReference of(Collection<T> array, Function<Optional<T>, Optional<String>> idMapper,
                                             Function<Optional<T>, Optional<String>> labelMapper) {
        if (array == null) {
            return null;
        }
        List<String> ids = null;

        if (idMapper != null) {
            ids = array.stream()
                    .map(Optional::of)
                    .map(idMapper)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }

        List<String> names = null;
        String namesConcatenated = null;

        if (labelMapper != null) {
            names = array.stream()
                    .map(Optional::of)
                    .map(labelMapper)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            namesConcatenated = !names.isEmpty() ? String.join(", ", names) : null;
        }

        return new CollectionReference(ids, names, namesConcatenated, array.size());
    }
}
