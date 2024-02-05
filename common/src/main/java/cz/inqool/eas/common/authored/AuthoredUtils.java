package cz.inqool.eas.common.authored;

import cz.inqool.eas.common.authored.tenant.TenantGenerator;
import cz.inqool.eas.common.authored.tenant.TenantReference;
import cz.inqool.eas.common.authored.user.UserGenerator;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.domain.index.dto.filter.EqFilter;
import cz.inqool.eas.common.domain.index.dto.filter.Filter;
import cz.inqool.eas.common.domain.index.dto.filter.IdsFilter;

import static java.util.Collections.emptySet;

public class AuthoredUtils {
    /**
     * Constructs filter for entities created by logged-in user.
     *
     * @return Filter for entities created by logged-in user or
     *         filter that makes query return empty result if there is no authenticated principal.
     */
    public static Filter userFilter() {
        UserReference user = UserGenerator.generateValue();

        if (user != null) {
            return new EqFilter("createdBy.id", user.getId());
        } else {
            return new IdsFilter(emptySet());
        }
    }

    /**
     * Constructs filter for entities created by logged-in user's tenant.
     *
     * @return Filter for entities created by logged-in user's tenant or
     *         filter that makes query return empty result if there is no authenticated principal.
     */
    public static Filter tenantFilter() {
        TenantReference tenant = TenantGenerator.generateValue();

        if (tenant != null) {
            return new EqFilter("createdByTenant.id", tenant.getId());
        } else {
            return new IdsFilter(emptySet());
        }
    }
}
