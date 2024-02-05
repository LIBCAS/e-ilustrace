package cz.inqool.eas.common.domain.index.reindex.reference.entity;

import cz.inqool.eas.common.domain.DomainIndexed;
import cz.inqool.eas.common.domain.store.DomainStore;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class IndexReferenceUpdateStore extends DomainStore<IndexReferenceUpdate, IndexReferenceUpdate, QIndexReferenceUpdate> {

    public IndexReferenceUpdateStore() {
        super(IndexReferenceUpdate.class);
    }


    public Class<? extends DomainIndexed<?, ?>> findFirstIndexedType() {
        return query().select(metaModel.indexedType)
                .from(metaModel)
                .orderBy(metaModel.created.asc())
                .fetchFirst();
    }

    public List<IndexReferenceUpdate> listFirstOf(Class<? extends DomainIndexed<?, ?>> indexedType, long limit) {
        return query().select(metaModel)
                .from(metaModel)
                .where(metaModel.indexedType.eq(indexedType))
                .orderBy(metaModel.created.asc())
                .limit(limit)
                .fetch();
    }

    public void delete(List<IndexReferenceUpdate> indexReferenceUpdates) {
        Set<String> ids = indexReferenceUpdates.stream()
                .map(IndexReferenceUpdate::getId)
                .collect(Collectors.toSet());

        super.delete(ids);
    }
}
