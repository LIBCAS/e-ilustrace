package cz.inqool.eas.common.intl;

import cz.inqool.eas.common.dictionary.DictionaryRepository;
import cz.inqool.eas.common.dictionary.index.DictionaryIndex;
import cz.inqool.eas.common.dictionary.store.DictionaryStore;

import java.util.List;


public class TranslationRepository extends DictionaryRepository<
        Translation,
        Translation,
        TranslationIndexedObject,
        DictionaryStore<Translation, Translation, QTranslation>,
        DictionaryIndex<Translation, Translation, TranslationIndexedObject>> {

    public List<Translation> findByLanguage(Language language) {
        QTranslation model = QTranslation.translation;

        List<Translation> translations = query().
                select(model).
                from(model).
                where(model.deleted.isNull()).
                where(model.active.isTrue()).
                where(model.language.eq(language)).
                orderBy(model.order.asc()).
                fetch();

        detachAll();

        return translations;
    }
}
