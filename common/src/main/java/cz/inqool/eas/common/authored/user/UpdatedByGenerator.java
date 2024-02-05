package cz.inqool.eas.common.authored.user;

import cz.inqool.eas.common.authored.Authored;
import cz.inqool.eas.common.authored.store.AuthoredObject;
import cz.inqool.eas.common.authored.system.SystemGenerator;
import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;

import static cz.inqool.eas.common.utils.AssertionUtils.coalesce;

/**
 * Class for hibernate value generating used in {@link AuthoredObject}
 * get {@link UserReference} from ApplicationContext, so it's always currently logged in User
 *
 */
public class UpdatedByGenerator implements ValueGenerator<UserReference> {

    public static ThreadLocal<Boolean> bypassed = ThreadLocal.withInitial(() -> false); //can be used to disable generator and save preset value

    @Override
    public UserReference generateValue(Session session, Object o) {
        if (bypassed.get()) {
            return ((Authored) o).getUpdatedBy();
        }

        return coalesce(UserGenerator.generateValue(), SystemGenerator::generateValue);
    }
}
