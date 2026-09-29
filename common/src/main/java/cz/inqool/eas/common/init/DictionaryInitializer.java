package cz.inqool.eas.common.init;

import cz.inqool.eas.common.dictionary.Dictionary;
import cz.inqool.eas.common.dictionary.DictionaryRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class DictionaryInitializer<
        ROOT extends Dictionary<ROOT>,
        REPOSITORY extends DictionaryRepository<ROOT, ?, ?, ?, ?>
        > extends DatedInitializer<ROOT, REPOSITORY> {

    protected <TYPE extends ROOT> TYPE findByCode(String code, Class<TYPE> type) {
        return getRepository().findByCode(type, code);
    }
}
