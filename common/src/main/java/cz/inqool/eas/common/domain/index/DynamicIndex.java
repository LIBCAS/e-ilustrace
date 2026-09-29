package cz.inqool.eas.common.domain.index;

import cz.inqool.eas.common.domain.index.field.IndexFieldNode;

import java.util.Map;

/**
 * Used by implementations of DomainIndex that wish to add additional, dynamically indexed fields to ElasticSearch
 * (fields that are not in the code of given Indexed class but determined from configuration on startup)
 * @author Lukas Jane (inQool) 18.11.2020.
 *
 * @deprecated Use {@link DomainIndex#customFieldsHook} instead.
 */
@Deprecated
public interface DynamicIndex {
    Map<String, IndexFieldNode> getDynamicFields();
}
