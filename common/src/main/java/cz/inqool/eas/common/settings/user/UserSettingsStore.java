package cz.inqool.eas.common.settings.user;

import cz.inqool.eas.common.dated.store.DatedStore;


public class UserSettingsStore extends DatedStore<UserSettings, UserSettings, QUserSettings> {
    public UserSettingsStore() {
        super(UserSettings.class);
    }

    public UserSettings findBy(String userId) {
        QUserSettings model = QUserSettings.userSettings;

        UserSettings settings = query().
                select(model).
                from(model).
                where(model.user.id.eq(userId)).
                fetchFirst();

        detachAll();

        return settings;
    }
}
