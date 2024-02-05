package cz.inqool.eas.common.authored;

import cz.inqool.eas.common.authored.tenant.TenantReference;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.dated.Dated;

public interface Authored<ROOT> extends Dated<ROOT> {
    void setCreatedBy(UserReference user);
    void setUpdatedBy(UserReference user);
    void setDeletedBy(UserReference user);

    void setCreatedByTenant(TenantReference tenant);
    void setUpdatedByTenant(TenantReference tenant);
    void setDeletedByTenant(TenantReference tenant);

    UserReference getCreatedBy();
    UserReference getUpdatedBy();
    UserReference getDeletedBy();

    TenantReference getCreatedByTenant();
    TenantReference getUpdatedByTenant();
    TenantReference getDeletedByTenant();
}
