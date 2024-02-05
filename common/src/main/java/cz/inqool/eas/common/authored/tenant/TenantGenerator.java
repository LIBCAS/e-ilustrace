package cz.inqool.eas.common.authored.tenant;

import cz.inqool.eas.common.authored.store.AuthoredObject;
import cz.inqool.eas.common.security.Tenant;
import cz.inqool.eas.common.security.User;
import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Class for hibernate value generating used in {@link AuthoredObject}
 * get {@link TenantReference} from ApplicationContext, so it's always currently logged in users Tenant
 */
public class TenantGenerator implements ValueGenerator<TenantReference> {
    @Override
    public TenantReference generateValue(Session session, Object o) {
        return generateValue();
    }

    /**
     * Constructs TenantReference of logged-in user's tenant.
     *
     * @return TenantReference of logged-in user's tenant or null if no authenticated principal is in the context.
     */
    public static TenantReference generateValue() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof User) {
                User user = (User) principal;
                Tenant tenant = user.getTenant();

                if (tenant != null) {
                    return new TenantReference(tenant.getId(), tenant.getName());
                }
            }
        }

        return null;
    }
}
