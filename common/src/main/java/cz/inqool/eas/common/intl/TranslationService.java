package cz.inqool.eas.common.intl;

import com.fasterxml.jackson.core.type.TypeReference;
import cz.inqool.eas.common.dictionary.DictionaryService;
import cz.inqool.eas.common.storage.file.FileManager;
import cz.inqool.eas.common.storage.file.OpenedFile;
import cz.inqool.eas.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@CacheConfig(cacheNames={"translations"})
public class TranslationService extends DictionaryService<
        Translation,
        TranslationDetail,
        TranslationList,
        TranslationCreate,
        TranslationUpdate,
        TranslationRepository
        > {

    private FileManager fileManager;

    private CacheManager cacheManager;

    @Cacheable(key = "#root.args[0]")
    public Map<String, String> load(Language language) {
        log.info("Creating cache for language {}.", language);
        List<Translation> translations = repository.findByLanguage(language);
        if (translations.size() == 0) {
            log.warn("No translation for language {} found.", language);
            return Collections.emptyMap();
        }

        Map<String, String> values = new HashMap<>();

        for (Translation translation : translations) {
            OpenedFile openedFile = fileManager.open(translation.content.getId());

            try (InputStream stream = openedFile.getStream()) {
                Map<String, String> fileValues = JsonUtils.fromJsonStream(stream, new MapTypeReference());
                values.putAll(fileValues);
            } catch (IOException e) {
                log.error("Failed to construct translations");
            }
        }

        return values;
    }

    @Override
    protected void preCreateHook(@NotNull Translation translation) {
        super.preCreateHook(translation);

        fileManager.preCreateHook(translation, Translation::getContent);
    }

    @Override
    protected void postCreateHook(@NotNull Translation object) {
        super.postCreateHook(object);

        evictCache(object.getLanguage());
    }

    @Override
    protected void preUpdateHook(@NotNull Translation translation) {
        super.preUpdateHook(translation);

        Translation oldTranslation = getInternal(Translation.class, translation.getId());
        fileManager.preUpdateHook(translation, oldTranslation, Translation::getContent);
    }

    @Override
    protected void postUpdateHook(@NotNull Translation object) {
        super.postUpdateHook(object);

        evictCache(object.getLanguage());
    }

    @Override
    protected void preDeleteHook(@NotNull String id) {
        super.preDeleteHook(id);

        Translation translation = getInternal(Translation.class, id);

        fileManager.preDeleteHook(translation, Translation::getContent);
    }

    @Override
    protected void postDeleteHook(@NotNull Translation object) {
        super.postDeleteHook(object);

        evictCache(object.getLanguage());
    }

    protected void evictCache(Language language) {
        Cache cache = cacheManager.getCache("translations");

        if (cache != null) {
            cache.evictIfPresent(language);
            log.info("Discarding cache for language {}.", language);
        }
    }

    @Autowired
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Autowired
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    private static class MapTypeReference extends TypeReference<Map<String, String>> {
    }
}
