package cz.inqool.eas.common.state.app;

import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

public class AppStateService {
    private AppStateStore store;

    @Transactional
    public String get() {
        AppState appState = store.get();
        if (appState == null) {
            appState = new AppState();
            appState = store.create(appState);
        }
        return appState.getState();
    }

    @Transactional
    public AppState update(String state) {
        AppState entity = new AppState();
        entity.setState(state);
        return store.create(entity);
    }

    @Autowired
    public void setStore(AppStateStore store) {
        this.store = store;
    }
}
