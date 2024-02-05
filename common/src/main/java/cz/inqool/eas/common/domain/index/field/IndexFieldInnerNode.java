package cz.inqool.eas.common.domain.index.field;

import cz.inqool.eas.common.domain.DomainIndexed;
import cz.inqool.eas.common.domain.index.field.java.Field;
import cz.inqool.eas.common.domain.index.reindex.reference.IndexReferenceField;
import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents an inner field in ElasticSearch mapping, annotated with {@link org.springframework.data.elasticsearch.annotations.Field}.
 */
public class IndexFieldInnerNode extends IndexFieldNode {

    private final IndexedFieldProps mainField;

    private Set<IndexFieldNode> children = Set.of();

    @Setter(AccessLevel.PACKAGE)
    private Float boost;


    public IndexFieldInnerNode(Class<? extends DomainIndexed<?, ?>> rootClass, Field javaField, org.springframework.data.elasticsearch.annotations.Field mainField, IndexFieldInnerNode parent) {
        super(rootClass, javaField, parent);
        this.mainField = new IndexedFieldProps(mainField.type(), mainField.index(), mainField.analyzer(), mainField.fielddata());
    }

    /**
     * Used when indexed field does not really exist in Indexed object, but is added dynamically
     */
    public IndexFieldInnerNode(Class<? extends DomainIndexed<?, ?>> rootClass, String javaFieldName, Class<?> javaFieldClass, IndexedFieldProps mainField, IndexFieldInnerNode parent, Set<IndexReferenceField> indexReferenceFields) {
        super(rootClass, javaFieldName, javaFieldClass, parent, indexReferenceFields);
        this.mainField = mainField;
    }

    public FieldType getType() {
        return mainField.getFieldType();
    }

    public float getBoost() {
        if (boost != null) {
            return boost;
        } else {
            return Optional.ofNullable(parent)
                    .map(IndexFieldInnerNode::getBoost)
                    .orElse(1.0F);
        }
    }

    public boolean isIndexed() {
        return mainField.isIndexed();
    }

    void addChild(IndexFieldNode child) {
        Set<IndexFieldNode> allChildren = new LinkedHashSet<>(this.children);
        allChildren.add(child);
        this.children = Set.copyOf(allChildren);
    }
}
