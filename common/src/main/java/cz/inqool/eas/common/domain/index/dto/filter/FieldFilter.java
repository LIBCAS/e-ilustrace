package cz.inqool.eas.common.domain.index.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.inqool.eas.common.domain.index.field.IndexFieldLeafNode;
import cz.inqool.eas.common.domain.index.field.IndexObjectFields;
import cz.inqool.eas.common.exception.v2.IndexException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.FIELD_NOT_INDEXED;

/**
 * Filter representing a filter condition on given {@link FieldFilter#field}.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
abstract public class FieldFilter<FILTER extends FieldFilter<FILTER>> extends AbstractFilter {

    /**
     * Attribute name (in case of nested object filtering must always contain dot-separated path)
     */
    @NotBlank
    protected String field;

    /**
     * Caching value for indexed field related to {@link #field}
     */
    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private IndexFieldLeafNode indexFieldNode;


    protected FieldFilter(@NotNull String operation) {
        super(operation);
    }

    protected FieldFilter(@NotNull String operation, @NotBlank String field) {
        super(operation);
        this.field = field;
    }


    /**
     * Returns adequate index field node to given {@link #field}.
     */
    protected IndexFieldLeafNode getIndexFieldLeafNode(IndexObjectFields indexObjectFields) {
        if (this.indexFieldNode == null) {
            IndexFieldLeafNode leafNode = indexObjectFields.get(field, IndexFieldLeafNode.class);
            if (!leafNode.isIndexed()) {
                throw new IndexException(FIELD_NOT_INDEXED)
                        .details(details -> details.property("field", field))
                        .debugInfo(info -> info.property("class", this.getClass()));
            }
            this.indexFieldNode = leafNode;
        }
        return this.indexFieldNode;
    }
}
