package cz.inqool.eas.common.authored.system;

import cz.inqool.eas.common.authored.store.AuthoredObject;
import cz.inqool.eas.common.authored.user.UserReference;
import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;

/**
 * Class for hibernate value generating used in {@link AuthoredObject}
 * get {@link UserReference} from ApplicationContext, so it's always currently logged in User
 *
 */
public class SystemGenerator implements ValueGenerator<UserReference> {
    protected static UserReference system = null;

    @Override
    public UserReference generateValue(Session session, Object o) {
        return generateValue();
    }

    public static UserReference generateValue() {
        return system;
    }
}
