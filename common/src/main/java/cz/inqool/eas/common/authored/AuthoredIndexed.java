package cz.inqool.eas.common.authored;

import cz.inqool.eas.common.authored.tenant.TenantReferenceIndexed;
import cz.inqool.eas.common.authored.user.UserReferenceIndexed;
import cz.inqool.eas.common.dated.DatedIndexed;
import cz.inqool.eas.common.projection.Projectable;

public interface AuthoredIndexed<ROOT extends Projectable<ROOT>, PROJECTED extends Projectable<ROOT>> extends DatedIndexed<ROOT, PROJECTED> {
    UserReferenceIndexed getCreatedBy();
    UserReferenceIndexed getUpdatedBy();
    UserReferenceIndexed getDeletedBy();

    TenantReferenceIndexed getCreatedByTenant();
    TenantReferenceIndexed getUpdatedByTenant();
    TenantReferenceIndexed getDeletedByTenant();
}
