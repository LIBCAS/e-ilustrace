package cz.inqool.eas.common.settings.app;

import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

public class AppSettingsService {
    private AppSettingsStore store;

    @Transactional
    public String get() {
        AppSettings appSettings = store.get();
        if (appSettings == null) {
            appSettings = new AppSettings();
            appSettings = store.create(appSettings);
        }
        return appSettings.getSettings();
    }

    @Transactional
    public AppSettings update(String settings) {
        AppSettings entity = new AppSettings();
        entity.setSettings(settings);
        return store.create(entity);
    }

    @Autowired
    public void setStore(AppSettingsStore store) {
        this.store = store;
    }
}
