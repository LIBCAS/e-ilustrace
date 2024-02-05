package cz.inqool.eas.common.state.app;

import cz.inqool.eas.common.dated.store.DatedStore;

public class AppStateStore extends DatedStore<AppState, AppState, QAppState> {
    public AppStateStore() {
        super(AppState.class);
    }

    public AppState get() {
        QAppState model = QAppState.appState;

        AppState state = query().
                select(model).
                from(model).
                orderBy(model.created.desc()).
                fetchFirst();

        detachAll();

        return state;
    }
}
