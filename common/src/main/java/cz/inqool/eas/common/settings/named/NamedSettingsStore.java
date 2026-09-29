package cz.inqool.eas.common.settings.named;

import cz.inqool.eas.common.authored.store.AuthoredStore;

import java.util.List;


public class NamedSettingsStore extends AuthoredStore<NamedSettings, NamedSettings, QNamedSettings> {
    public NamedSettingsStore() {
        super(NamedSettings.class);
    }

    public List<NamedSettings> findByUserAndTag(String userId, String tag) {
        QNamedSettings model = QNamedSettings.namedSettings;

        List<NamedSettings> list = query().
                select(model).
                from(model).
                where(model.deleted.isNull()).
                where(model.tag.eq(tag)).
                where(model.user.id.eq(userId).or(model.shared.isTrue())).
                fetch();

        detachAll();

        return list;
    }
}
