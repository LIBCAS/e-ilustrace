package cz.inqool.eas.common.domain.index.dynamic;

import cz.inqool.eas.common.exception.GeneralException;
import org.springframework.stereotype.Service;

import java.util.Map;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;
import static java.util.Map.of;

/**
 * Factory for creating Elasticsearch mapping dynamically.
 */
@Service
public class DynamicMappingFactory {
    public Map<String, Object> createMapping(String fieldName, DynamicFieldType type) {
        switch (type) {
            case TEXT:
                return createTextMapping(fieldName);
            case NUMBER:
                return createNumberMapping(fieldName);
            case BOOLEAN:
                return createBooleanMapping(fieldName);
            case DATE:
                return createDateMapping(fieldName);
            case DATETIME:
                return createDateTimeMapping(fieldName);
            case TIME:
                return createTimeMapping(fieldName);
            default:
                throw new GeneralException("Unsupported field type " + type);
        }
    }

    public Map<String, Object> createTextMapping(String fieldName) {
        return of(
                fieldName, of(
                        "type", "text",
                        "analyzer", TEXT_SHORT_KEYWORD,
                        "search_analyzer", TEXT_SHORT_KEYWORD,
                        "fields", of(
                                FOLD, of(
                                        "type", "text",
                                        "analyzer", FOLDING,
                                        "search_analyzer", FOLDING
                                ),

                                SEARCH, of(
                                        "type", "text",
                                        "analyzer", FOLDING_AND_TOKENIZING,
                                        "search_analyzer", FOLDING_AND_TOKENIZING
                                ),

                                SORT, of(
                                        "type", "text",
                                        "analyzer", SORTING,
                                        "search_analyzer", SORTING,
                                        "fielddata", true
                                )
                        )
                )
        );
    }

    public Map<String, Object> createNumberMapping(String fieldName) {
        return of(
                fieldName, of(
                        "type", "integer"
                )
        );
    }

    public Map<String, Object> createBooleanMapping(String fieldName) {
        return of(
                fieldName, of(
                        "type", "boolean"
                )
        );
    }

    public Map<String, Object> createDateMapping(String fieldName) {
        return of(
                fieldName, of(
                        "type", "date",
                        "format", "date"
                )
        );
    }

    public Map<String, Object> createDateTimeMapping(String fieldName) {
        return of(
                fieldName, of(
                        "type", "date",
                        "format", "date_optional_time"
                )
        );
    }

    public Map<String, Object> createTimeMapping(String fieldName) {
        return of(
                fieldName, of(
                        "type", "date",
                        "format", "time"
                )
        );
    }
}
