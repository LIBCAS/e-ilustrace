package cz.inqool.eas.common.domain.index.dynamic;

import cz.inqool.eas.common.domain.DomainIndexed;
import cz.inqool.eas.common.domain.index.field.IndexFieldNode;
import cz.inqool.eas.common.domain.index.field.IndexObjectParser;
import cz.inqool.eas.common.domain.index.field.java.VirtualField;
import cz.inqool.eas.common.exception.GeneralException;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.SORTING;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;
import static org.springframework.core.annotation.AnnotationUtils.synthesizeAnnotation;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

/**
 * Factory for creating Field descriptions dynamically.
 */
@Service
public class DynamicFieldFactory {
    public Set<IndexFieldNode> createField(Class<? extends DomainIndexed<?, ?>> rootClass, String fieldName, DynamicFieldType type) {
        switch (type) {
            case TEXT:
                return createTextField(rootClass, fieldName);
            case NUMBER:
                return createNumberField(rootClass, fieldName);
            case BOOLEAN:
                return createBooleanField(rootClass, fieldName);
            case DATE:
                return createDateField(rootClass, fieldName);
            case DATETIME:
                return createDateTimeField(rootClass, fieldName);
            case TIME:
                return createTimeField(rootClass, fieldName);
            default:
                throw new GeneralException("Unsupported field type " + type);
        }
    }


    public Set<IndexFieldNode> createTextField(Class<? extends DomainIndexed<?, ?>> rootClass, String fieldName) {
        Field mainField = synthesizeAnnotation(Map.of(
                "type", Text,
                "analyzer", TEXT_SHORT_KEYWORD
        ), Field.class, null);

        InnerField foldInnerField = synthesizeAnnotation(Map.of(
                "suffix", FOLD,
                "type", Text,
                "analyzer", FOLDING
        ), InnerField.class, null);

        InnerField searchInnerField = synthesizeAnnotation(Map.of(
                "suffix", SEARCH,
                "type", Text,
                "analyzer", FOLDING_AND_TOKENIZING
        ), InnerField.class, null);

        InnerField sortInnerField = synthesizeAnnotation(Map.of(
                "suffix", SORT,
                "type", Text,
                "analyzer", SORTING,
                "fielddata", true
        ), InnerField.class, null);


        MultiField multiField = synthesizeAnnotation(Map.of(
                "mainField", mainField,
                "otherFields", new InnerField[]{foldInnerField, searchInnerField, sortInnerField}
        ), MultiField.class, null);

        VirtualField field = new VirtualField(fieldName, String.class, List.of(multiField));

        return IndexObjectParser.parse(rootClass, field, null, 5);
    }

    public Set<IndexFieldNode> createNumberField(Class<? extends DomainIndexed<?, ?>> rootClass, String fieldName) {
        Field mainField = synthesizeAnnotation(Map.of(
                "type", FieldType.Integer
        ), Field.class, null);

        VirtualField field = new VirtualField(fieldName, String.class, List.of(mainField));

        return IndexObjectParser.parse(rootClass, field, null, 5);
    }

    public Set<IndexFieldNode> createBooleanField(Class<? extends DomainIndexed<?, ?>> rootClass, String fieldName) {
        Field mainField = synthesizeAnnotation(Map.of(
                "type", FieldType.Boolean
        ), Field.class, null);

        VirtualField field = new VirtualField(fieldName, String.class, List.of(mainField));

        return IndexObjectParser.parse(rootClass, field, null, 5);
    }

    public Set<IndexFieldNode> createDateField(Class<? extends DomainIndexed<?, ?>> rootClass, String fieldName) {
        Field mainField = synthesizeAnnotation(Map.of(
                "type", FieldType.Date,
                "format", DateFormat.date
        ), Field.class, null);

        VirtualField field = new VirtualField(fieldName, String.class, List.of(mainField));

        return IndexObjectParser.parse(rootClass, field, null, 5);
    }

    public Set<IndexFieldNode> createDateTimeField(Class<? extends DomainIndexed<?, ?>> rootClass, String fieldName) {
        Field mainField = synthesizeAnnotation(Map.of(
                "type", FieldType.Date,
                "format", DateFormat.date_optional_time
        ), Field.class, null);

        VirtualField field = new VirtualField(fieldName, String.class, List.of(mainField));

        return IndexObjectParser.parse(rootClass, field, null, 5);
    }

    public Set<IndexFieldNode> createTimeField(Class<? extends DomainIndexed<?, ?>> rootClass, String fieldName) {
        Field mainField = synthesizeAnnotation(Map.of(
                "type", FieldType.Date,
                "format", DateFormat.time
        ), Field.class, null);

        VirtualField field = new VirtualField(fieldName, String.class, List.of(mainField));

        return IndexObjectParser.parse(rootClass, field, null, 5);
    }
}
