package cz.inqool.eas.common.domain.index.field;

import cz.inqool.eas.common.domain.DomainIndexed;
import cz.inqool.eas.common.domain.index.field.ES.Suffix;
import cz.inqool.eas.common.domain.index.field.java.Field;
import cz.inqool.eas.common.domain.index.reindex.reference.IndexReferenceField;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static cz.inqool.eas.common.utils.CollectionUtils.join;

/**
 * Represents a field in ElasticSearch mapping, annotated with {@link org.springframework.data.elasticsearch.annotations.Field} or {@link MultiField}.
 */
@Slf4j
public class IndexFieldLeafNode extends IndexFieldNode {

    private final IndexedFieldProps mainField;

    private final Map<String, IndexedFieldProps> innerFields = new HashMap<>();

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private boolean fulltext;

    @Setter(AccessLevel.PROTECTED)
    private Float boost;


    public IndexFieldLeafNode(Class<? extends DomainIndexed<?, ?>> rootClass, Field javaField, org.springframework.data.elasticsearch.annotations.Field mainField, IndexFieldInnerNode parent) {
        super(rootClass, javaField, parent);
        this.mainField = new IndexedFieldProps(mainField.type(), mainField.index(), mainField.analyzer(), mainField.fielddata());
    }

    /**
     * Used when indexed field does not really exist in Indexed object, but is added dynamically
     *
     * @deprecated Use the next constructor instead
     */
    @Deprecated
    public IndexFieldLeafNode(Class<? extends DomainIndexed<?, ?>> rootClass, String javaFieldName, Class<?> javaFieldClass, IndexedFieldProps mainField, IndexFieldInnerNode parent, boolean fulltext, float boost, Set<IndexReferenceField> indexReferenceFields) {
        super(rootClass, javaFieldName, javaFieldClass, parent, indexReferenceFields);
        this.mainField = mainField;
        this.fulltext = fulltext;
        this.boost = boost;
    }

    /**
     * Used when indexed field does not really exist in Indexed object, but is added dynamically
     */
    public IndexFieldLeafNode(Class<? extends DomainIndexed<?, ?>> rootClass, String javaFieldName, Class<?> javaFieldClass, org.springframework.data.elasticsearch.annotations.Field mainField, IndexFieldInnerNode parent, Set<IndexReferenceField> indexReferenceFields) {
        super(rootClass, javaFieldName, javaFieldClass, parent, indexReferenceFields);
        this.mainField = new IndexedFieldProps(mainField.type(), mainField.index(), mainField.analyzer(), mainField.fielddata());
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

    public String getAnalyzer() {
        return mainField.getAnalyzer();
    }

    public boolean isFieldData() {
        return mainField.isFieldData();
    }

    public boolean hasInnerField(String suffix) {
        return innerFields.containsKey(suffix);
    }

    public IndexedFieldProps getInnerField(String suffix) {
        return innerFields.get(suffix);
    }

    public void registerInnerField(String suffix, InnerField innerField) {
        innerFields.put(suffix, new IndexedFieldProps(innerField.type(), innerField.index(), innerField.analyzer(), innerField.fielddata()));
    }

    public void registerInnerField(String suffix, IndexedFieldProps indexedFieldProps) {
        innerFields.put(suffix, indexedFieldProps);
    }

    public String getFolded() {
        String suffix = hasInnerField(Suffix.FOLD) ? Suffix.FOLD : null;
        return join(".", getElasticSearchPath(), suffix);
    }

    public String getSearchable() {
        String suffix = hasInnerField(Suffix.SEARCH) ? Suffix.SEARCH : null;
        return join(".", getElasticSearchPath(), suffix);
    }

    public String getSortable() {
        String suffix = hasInnerField(Suffix.SORT) ? Suffix.SORT : null;
        return join(".", getElasticSearchPath(), suffix);
    }
}
