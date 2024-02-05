package cz.inqool.eas.common.domain.index.field;

import cz.inqool.eas.common.domain.DomainIndexed;
import cz.inqool.eas.common.domain.index.field.java.Field;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

/**
 * Represents a field in ElasticSearch mapping, annotated with {@link GeoPointField}.
 */
@Slf4j
public class IndexFieldGeoPointLeafNode extends IndexFieldNode {

    private final IndexedFieldProps mainField;

    public IndexFieldGeoPointLeafNode(Class<? extends DomainIndexed<?, ?>> rootClass, Field javaField, GeoPointField mainField, IndexFieldInnerNode parent) {
        super(rootClass, javaField, parent);
        //not implemented
        this.mainField = new IndexedFieldProps(FieldType.Auto);
    }

    @Override
    public FieldType getType() {
        return null;
    }
}
