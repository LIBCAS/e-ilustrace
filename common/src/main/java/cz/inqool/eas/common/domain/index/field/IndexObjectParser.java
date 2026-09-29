package cz.inqool.eas.common.domain.index.field;

import cz.inqool.eas.common.domain.DomainIndexed;
import cz.inqool.eas.common.domain.index.field.java.Field;
import cz.inqool.eas.common.domain.index.field.java.JavaField;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.elasticsearch.annotations.*;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.TreeSet;

import static cz.inqool.eas.common.domain.index.field.ES.Suffix.SEARCH;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IndexObjectParser {

    /**
     * Max depth to which the parser scans fields of an indexed object
     */
    private static final int MAX_PROPERTY_DEPTH = 5;

    public static IndexObjectFields parse(Class<? extends DomainIndexed<?, ?>> indexedType) {
        IndexObjectFields fields = new IndexObjectFields();

        parse(indexedType, fields);

        return fields;
    }

    public static void parse(Class<? extends DomainIndexed<?, ?>> indexedType, IndexObjectFields indexObjectFields) {
        log.debug("Parsing indexed fields of class '{}'", indexedType.getSimpleName());

        for (java.lang.reflect.Field field : FieldUtils.getAllFields(indexedType)) {
            parse(indexedType, new JavaField(field), null, MAX_PROPERTY_DEPTH)
                    .forEach(indexField -> indexObjectFields.put(indexField.getElasticSearchPath(), indexField));
        }
    }

    public static Set<IndexFieldNode> parse(Class<? extends DomainIndexed<?, ?>> rootClass, Field field, @Nullable IndexFieldInnerNode parent, int maxDepth) {
        log.trace("Parsing field '{}'", field.getName());

        Set<IndexFieldNode> indexFieldNodes = new TreeSet<>();

        GeoPointField geoPointFieldAnnotation = field.getAnnotation(GeoPointField.class);
        if (geoPointFieldAnnotation != null) {
            indexFieldNodes.add(parseGeoPointLeaf(geoPointFieldAnnotation, rootClass, field, parent));
        }

        org.springframework.data.elasticsearch.annotations.Field fieldAnnotation = field.getAnnotation(org.springframework.data.elasticsearch.annotations.Field.class);
        if (fieldAnnotation != null) {
            if (fieldAnnotation.type() == FieldType.Object || fieldAnnotation.type() == FieldType.Nested) {
                indexFieldNodes.addAll(parseFieldObject(fieldAnnotation, rootClass, field, parent, maxDepth));
            } else {
                indexFieldNodes.add(parseFieldLeaf(fieldAnnotation, rootClass, field, parent));
            }
        }

        MultiField multiFieldAnnotation = field.getAnnotation(MultiField.class);
        if (multiFieldAnnotation != null) {
            indexFieldNodes.add(parseMultifieldLeaf(multiFieldAnnotation, rootClass, field, parent));
        }

        return indexFieldNodes;
    }

    private static Set<IndexFieldNode> parseFieldObject(org.springframework.data.elasticsearch.annotations.Field fieldAnnotation, Class<? extends DomainIndexed<?, ?>> rootClass, Field field, @Nullable IndexFieldInnerNode parent, int maxDepth) {
        if (maxDepth <= 0) {
            log.debug("Max depth reached, skipping parsing of field '{}' of root class '{}'", field.getName(), rootClass.getSimpleName());
            return Set.of();
        }
        --maxDepth;

        IndexFieldInnerNode node = new IndexFieldInnerNode(rootClass, field, fieldAnnotation, parent);

        if (fieldAnnotation.type() == FieldType.Nested) {
            // Shouldn't use nested fields due to low performance and scalability. Also they do not work properly in conjunction with logical filters.
            log.warn("Nested field encountered: " + node.getJavaPath());
        }

        Boost boost = field.getAnnotation(Boost.class);
        if (boost != null) {
            node.setBoost(boost.value());
        }

        Set<IndexFieldNode> indexFieldNodes = new TreeSet<>();
        indexFieldNodes.add(node);

        for (java.lang.reflect.Field nestedField : FieldUtils.getAllFields(field.resolveType())) {
            indexFieldNodes.addAll(parse(rootClass, new JavaField(nestedField), node, maxDepth));
        }

        return indexFieldNodes;
    }

    private static IndexFieldLeafNode parseFieldLeaf(org.springframework.data.elasticsearch.annotations.Field fieldAnnotation, Class<? extends DomainIndexed<?, ?>> rootClass, Field field, @Nullable IndexFieldInnerNode parent) {
        IndexFieldLeafNode leaf = new IndexFieldLeafNode(rootClass, field, fieldAnnotation, parent);

        // keyword and text fields are available for fulltext search
        leaf.setFulltext(fieldAnnotation.type() == FieldType.Text);

        Boost boost = field.getAnnotation(Boost.class);
        if (boost != null) {
            leaf.setBoost(boost.value());
        }

        return leaf;
    }

    private static IndexFieldLeafNode parseMultifieldLeaf(MultiField multiFieldAnnotation, Class<? extends DomainIndexed<?, ?>> rootClass, Field field, @Nullable IndexFieldInnerNode parent) {
        IndexFieldLeafNode leaf = new IndexFieldLeafNode(rootClass, field, multiFieldAnnotation.mainField(), parent);
        leaf.setFulltext(multiFieldAnnotation.mainField().type() == FieldType.Text);

        for (InnerField innerField : multiFieldAnnotation.otherFields()) {
            leaf.registerInnerField(innerField.suffix(), innerField);
            if (SEARCH.equals(innerField.suffix())) {
                leaf.setFulltext(innerField.type() == FieldType.Text);
            }
        }

        Boost boost = field.getAnnotation(Boost.class);
        if (boost != null) {
            leaf.setBoost(boost.value());
        }

        return leaf;
    }

    private static IndexFieldGeoPointLeafNode parseGeoPointLeaf(GeoPointField geoPointFieldAnnotation, Class<? extends DomainIndexed<?, ?>> rootClass, Field field, @Nullable IndexFieldInnerNode parent) {
        return new IndexFieldGeoPointLeafNode(rootClass, field, geoPointFieldAnnotation, parent);
    }
}
