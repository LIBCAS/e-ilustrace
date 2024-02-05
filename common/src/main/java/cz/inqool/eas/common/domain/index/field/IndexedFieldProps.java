package cz.inqool.eas.common.domain.index.field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author Lukas Jane (inQool) 12.01.2021.
 */
@Getter
@AllArgsConstructor
public class IndexedFieldProps {
    private FieldType fieldType;
    private boolean indexed = true;
    private String analyzer;
    private boolean fieldData;

    public IndexedFieldProps(FieldType fieldType) {
        this.fieldType = fieldType;
    }
}
