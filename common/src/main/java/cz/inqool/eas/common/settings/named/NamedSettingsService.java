package cz.inqool.eas.common.settings.named;

import cz.inqool.eas.common.authored.user.UserGenerator;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.exception.v2.ForbiddenObject;
import cz.inqool.eas.common.exception.v2.ForbiddenOperation;
import cz.inqool.eas.common.exception.v2.MissingObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.List;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.*;
import static cz.inqool.eas.common.utils.AssertionUtils.eq;
import static cz.inqool.eas.common.utils.AssertionUtils.notNull;

public class NamedSettingsService {
    private NamedSettingsStore store;

    @Transactional
    public List<NamedSettings> findSettings(String tag) {
        UserReference user = UserGenerator.generateValue();
        notNull(user, () -> new ForbiddenOperation(OPERATION_ACCESS_DENIED)
                .details(details -> details.property("type", NamedSettings.class.getSimpleName()))
                .debugInfo(info -> info.clazz(NamedSettings.class).property("tag", tag)));

        return store.findByUserAndTag(user.getId(), tag);
    }

    @Transactional
    public NamedSettings create(NamedSettingsCreate settings) {
        UserReference user = UserGenerator.generateValue();
        notNull(user, () -> new ForbiddenOperation(OPERATION_ACCESS_DENIED)
                .details(details -> details.property("type", NamedSettings.class.getSimpleName()))
                .debugInfo(info -> info.clazz(NamedSettings.class)));

        NamedSettings entity = NamedSettingsCreate.toEntity(settings);
        entity.setUser(user);
        return store.create(entity);
    }

    @Transactional
    public void delete(String id) {
        UserReference user = UserGenerator.generateValue();
        notNull(user, () -> new ForbiddenOperation(OPERATION_ACCESS_DENIED)
                .details(details -> details.id(id).property("type", NamedSettings.class.getSimpleName()))
                .debugInfo(info -> info.clazz(NamedSettings.class)));

        NamedSettings settings = store.find(id);
        notNull(settings, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(id).property("type", NamedSettings.class.getSimpleName()))
                .debugInfo(info -> info.clazz(NamedSettings.class))
                .logAll());
        eq(settings.getUser(), user, () -> new ForbiddenObject(OBJECT_ACCESS_DENIED)
                .details(details -> details.id(id).property("type", NamedSettings.class.getSimpleName()))
                .debugInfo(info -> info.clazz(NamedSettings.class)));

        store.delete(id);
    }

    @Autowired
    public void setStore(NamedSettingsStore store) {
        this.store = store;
    }
}
