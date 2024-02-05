package cz.inqool.eas.eil.keyword;

import cz.inqool.eas.common.domain.DomainRepository;
import cz.inqool.eas.common.domain.index.DomainIndex;
import cz.inqool.eas.common.domain.store.DomainStore;
import org.springframework.stereotype.Repository;

@Repository
public class KeywordRepository extends DomainRepository<
        Keyword,
        Keyword,
        KeywordIndexedObject,
        DomainStore<Keyword, Keyword, QKeyword>,
        DomainIndex<Keyword, Keyword, KeywordIndexedObject>> {

    public Keyword findByLabel(String label) {
        if (label == null) {
            return null;
        }
        QKeyword model = getStore().getMetaModel();

        Keyword keyword = query()
                .select(model)
                .from(model)
                .where(model.label.eq(label))
                .fetchOne();

        detachAll();

        return keyword;
    }
}

