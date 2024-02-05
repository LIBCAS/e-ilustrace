package cz.inqool.eas.common.domain.index.field;

import cz.inqool.eas.common.domain.DomainIndexed;
import cz.inqool.eas.common.domain.index.field.java.Field;
import cz.inqool.eas.common.domain.index.reference.CollectionReference;
import cz.inqool.eas.common.domain.index.reference.LabeledReference;
import cz.inqool.eas.common.domain.index.reindex.reference.IndexReferenceField;
import cz.inqool.eas.common.domain.index.reindex.reference.annotation.IndexReference;
import cz.inqool.eas.common.domain.index.reindex.reference.annotation.IndexReferences;
import cz.inqool.eas.common.exception.GeneralException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.utils.AssertionUtils.*;
import static cz.inqool.eas.common.utils.CollectionUtils.join;

/**
 * Represents one index object field ES mapping, provides methods for retrieval of field names with proper suffixes
 * during search query build.
 *
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
@Slf4j
@Getter
@EqualsAndHashCode
public abstract class IndexFieldNode implements Comparable<IndexFieldNode> {

    protected final Class<? extends DomainIndexed<?, ?>> rootClass;

    protected final String javaFieldName;
    protected Class<?> javaFieldClass;

    protected final IndexFieldInnerNode parent;

    protected final Set<IndexReferenceField> indexReferences = new HashSet<>();

    protected IndexFieldNode(Class<? extends DomainIndexed<?, ?>> rootClass, Field javaField, @Nullable IndexFieldInnerNode parent) {
        this.rootClass = rootClass;
        this.javaFieldName = javaField.getName();
        this.javaFieldClass = javaField.resolveType();
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
        for (IndexReference indexReference : getIndexReferences(javaField)) {
            this.indexReferences.add(createIndexReferenceField(indexReference));
        }
    }

    protected IndexFieldNode(Class<? extends DomainIndexed<?, ?>> rootClass, String javaFieldName, Class<?> javaFieldClass, @Nullable IndexFieldInnerNode parent, Set<IndexReferenceField> indexReferenceFields) {
        this.rootClass = rootClass;
        this.javaFieldName = javaFieldName;
        this.javaFieldClass = javaFieldClass;
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
        this.indexReferences.addAll(indexReferenceFields);
    }

    public abstract FieldType getType();

    public String getJavaPath() {
        StringBuilder sb = new StringBuilder();

        IndexFieldNode indexFieldNode = this;
        while (indexFieldNode != null) {
            if (sb.length() != 0) {
                sb.insert(0, ".");
            }
            sb.insert(0, indexFieldNode.getJavaFieldName());
            indexFieldNode = indexFieldNode.parent;
        }

        sb.insert(0, rootClass.getSimpleName() + ".");
        return sb.toString();
    }

    public String getElasticSearchPath() {
        StringBuilder sb = new StringBuilder();

        IndexFieldNode indexFieldNode = this;
        while (indexFieldNode != null) {
            if (sb.length() != 0) {
                sb.insert(0, ".");
            }
            sb.insert(0, indexFieldNode.getJavaFieldName());
            indexFieldNode = indexFieldNode.parent;
        }

        return sb.toString();
    }

    public boolean isOfNested() {
        return getNestedParent() != null;
    }

    public IndexFieldInnerNode getNestedParent() {
        IndexFieldNode indexFieldNode = this;
        while (indexFieldNode != null) {
            if (indexFieldNode.getType() == FieldType.Nested) {
                return cast(indexFieldNode, IndexFieldInnerNode.class, () -> new IllegalStateException("Nested field musn't be a leaf node"));
            }
            indexFieldNode = indexFieldNode.parent;
        }

        return null;
    }

    public String getNestedPath() {
        IndexFieldInnerNode nestedParent = getNestedParent();
        notNull(nestedParent, () -> new GeneralException("The field is not of type 'Nested'."));

        return nestedParent.getElasticSearchPath();
    }

    private static Set<IndexReference> getIndexReferences(Field field) {
        Set<IndexReference> references = new HashSet<>();

        IndexReferences indexReferences = field.getAnnotation(IndexReferences.class);
        if (indexReferences != null) {
            references.addAll(Set.of(indexReferences.value()));
        }

        IndexReference reference = field.getAnnotation(IndexReference.class);
        if (reference != null) {
            references.add(reference);
        }

        return references;
    }

    private IndexReferenceField createIndexReferenceField(IndexReference reference) {
        Class<?> type = javaFieldClass;

        String elasticSearchPath;
        if (CollectionReference.class.isAssignableFrom(type) || LabeledReference.class.isAssignableFrom(type)) {
            if (reference.idPath().isEmpty()) {
                elasticSearchPath = join(".", getElasticSearchPath(), "id");
            } else {
                String parentElasticSearchPath = (parent != null) ? parent.getElasticSearchPath() : null;
                elasticSearchPath = join(".", parentElasticSearchPath, reference.idPath());
            }
        } else if (DomainIndexed.class.isAssignableFrom(type)) {
            elasticSearchPath = join(".", getElasticSearchPath(), "id");
        } else {
            notEmpty(reference.idPath(), () -> new IllegalStateException("'idPath' for field '" + javaFieldName + "' in '" + rootClass + "' is empty"));

            String parentElasticSearchPath = (parent != null) ? parent.getElasticSearchPath() : null;
            elasticSearchPath = join(".", parentElasticSearchPath, reference.idPath());
        }

        Set<String> registeredFields = Arrays.stream(reference.onlyOnChanged()).collect(Collectors.toUnmodifiableSet());
        if (registeredFields.isEmpty()) {
            return new IndexReferenceField(rootClass, reference.referencedClass(), elasticSearchPath);
        } else {
            return new IndexReferenceField(rootClass, reference.referencedClass(), elasticSearchPath, registeredFields);
        }
    }

    @Override
    public int compareTo(IndexFieldNode o) {
        return this.getJavaPath().compareTo(o.getJavaPath());
    }
}
