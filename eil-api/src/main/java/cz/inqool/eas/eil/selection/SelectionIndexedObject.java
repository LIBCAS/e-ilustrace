package cz.inqool.eas.eil.selection;

import cz.inqool.eas.common.dated.index.DatedIndexedObject;
import cz.inqool.eas.eil.record.RecordIndexedObject;
import cz.inqool.eas.eil.user.UserIndexedObject;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Document(indexName = "selection")
@FieldNameConstants(innerTypeName = "IxFields")
public class SelectionIndexedObject extends DatedIndexedObject<Selection, Selection> {

    @Override
    public void toIndexedObject(Selection obj) {
        super.toIndexedObject(obj);
    }
}
