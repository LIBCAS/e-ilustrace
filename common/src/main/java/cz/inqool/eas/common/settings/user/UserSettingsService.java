package cz.inqool.eas.common.settings.user;

import cz.inqool.eas.common.authored.user.UserGenerator;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.exception.v2.ForbiddenOperation;
import cz.inqool.eas.common.exception.v2.MissingObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.ENTITY_NOT_EXIST;
import static cz.inqool.eas.common.exception.v2.ExceptionCode.OPERATION_ACCESS_DENIED;
import static cz.inqool.eas.common.utils.AssertionUtils.notNull;

public class UserSettingsService {
    private UserSettingsStore store;

    @Transactional
    public String findUserSettings() {
        UserReference user = UserGenerator.generateValue();
        notNull(user, () -> new ForbiddenOperation(OPERATION_ACCESS_DENIED)
                .details(details -> details.property("type", UserSettings.class.getSimpleName()))
                .debugInfo(info -> info.clazz(UserSettings.class)));

        UserSettings userSettings = store.findBy(user.getId());
        if (userSettings == null) {
            userSettings = new UserSettings();
            userSettings.setUser(user);
            userSettings = store.create(userSettings);
        }
        return userSettings.getSettings();
    }

    @Transactional
    public UserSettings update(String settings) {
        UserReference user = UserGenerator.generateValue();
        UserSettings entity = store.findBy(user.getId());
        notNull(entity, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.property("type", UserSettings.class.getSimpleName()).property("user", user.getId()))
                .debugInfo(info -> info.clazz(UserSettings.class))
                .logAll());
        entity.setSettings(settings);
        return store.update(entity);
    }

    @Transactional
    public void clear() {
        UserReference user = UserGenerator.generateValue();
        UserSettings entity = store.findBy(user.getId());
        notNull(entity, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.property("type", UserSettings.class.getSimpleName()).property("user", user.getId()))
                .debugInfo(info -> info.clazz(UserSettings.class))
                .logAll());
        entity.setSettings("{}");
        store.update(entity);
    }

    @Autowired
    public void setStore(UserSettingsStore store) {
        this.store = store;
    }
}
