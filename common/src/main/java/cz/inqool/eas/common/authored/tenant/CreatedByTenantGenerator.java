package cz.inqool.eas.common.authored.tenant;

import cz.inqool.eas.common.authored.Authored;
import cz.inqool.eas.common.authored.store.AuthoredObject;
import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;

/**
 * Class for hibernate value generating used in {@link AuthoredObject}
 * get {@link TenantReference} from ApplicationContext, so it's always currently logged in users Tenant
 *
 */
public class CreatedByTenantGenerator implements ValueGenerator<TenantReference> {

    public static ThreadLocal<Boolean> bypassed = ThreadLocal.withInitial(() -> false); //can be used to disable generator and save preset value

    @Override
    public TenantReference generateValue(Session session, Object o) {
        return generateValue((Authored) o);
    }

    public static TenantReference generateValue(Authored authored) {
        if (bypassed.get()) {
            return authored.getCreatedByTenant();
        }
        return TenantGenerator.generateValue();
    }
}
