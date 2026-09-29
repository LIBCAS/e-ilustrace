package cz.inqool.eas.common.domain.index.reindex.reference;

import cz.inqool.eas.common.domain.DomainRepository;
import cz.inqool.eas.common.domain.index.field.IndexFieldLeafNode;
import cz.inqool.eas.common.domain.index.field.IndexFieldNode;
import cz.inqool.eas.common.domain.index.field.IndexObjectParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.lang.NonNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static cz.inqool.eas.common.utils.AssertionUtils.eq;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * Scans indexed type classes from all domain repositories for index references. Used to be able to listen to the
 * reference updates and automatically reindex affected entries.
 */
@Slf4j
@Order(HIGHEST_PRECEDENCE)
public class IndexReferenceScanner implements ApplicationListener<ApplicationReadyEvent> {

    private List<DomainRepository<?, ?, ?, ?, ?>> domainRepositories = List.of();

    private IndexReferenceHolder indexReferenceHolder;


    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        scanIndexedClasses();
    }

    private void scanIndexedClasses() {
        log.info("Scanning index object classes...");

        Set<IndexReferenceField> indexReferenceFields = new LinkedHashSet<>();
        domainRepositories.stream()
                .map(DomainRepository::getIndexableType)
                .map(IndexObjectParser::parse)
                .forEach(fields -> {
                    for (IndexFieldNode node : fields.values()) {
                        for (IndexReferenceField indexReferenceField : node.getIndexReferences()) {
                            IndexFieldLeafNode idField = fields.get(indexReferenceField.getElasticSearchPath(), IndexFieldLeafNode.class);
                            eq(idField.getType(), FieldType.Keyword, () -> new IllegalStateException("ID field '" + idField.getJavaFieldName() + "' must be of the type keyword."));
                            indexReferenceFields.add(indexReferenceField);
                        }
                    }
                });

        log.info("Scanning completed, {} references found.", indexReferenceFields.size());

        if (!indexReferenceFields.isEmpty()) {
            indexReferenceHolder.registerReferences(indexReferenceFields);
        }
    }

    @Autowired(required = false)
    public void setDomainRepositories(List<DomainRepository<?, ?, ?, ?, ?>> domainRepositories) {
        this.domainRepositories = domainRepositories;
    }

    @Autowired
    public void setIndexReferenceHolder(IndexReferenceHolder holder) {
        this.indexReferenceHolder = holder;
    }
}
