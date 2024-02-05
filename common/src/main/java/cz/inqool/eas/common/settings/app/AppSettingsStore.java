package cz.inqool.eas.common.settings.app;

import cz.inqool.eas.common.dated.store.DatedStore;

public class AppSettingsStore extends DatedStore<AppSettings, AppSettings, QAppSettings> {
    public AppSettingsStore() {
        super(AppSettings.class);
    }

    public AppSettings get() {
        QAppSettings model = QAppSettings.appSettings;

        AppSettings settings = query().
                select(model).
                from(model).
                orderBy(model.created.desc()).
                fetchFirst();

        detachAll();

        return settings;
    }
}
