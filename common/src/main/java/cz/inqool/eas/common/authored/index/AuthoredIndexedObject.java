package cz.inqool.eas.common.authored.index;

import cz.inqool.eas.common.authored.Authored;
import cz.inqool.eas.common.authored.AuthoredIndexed;
import cz.inqool.eas.common.authored.tenant.TenantReferenceIndexed;
import cz.inqool.eas.common.authored.user.UserReferenceIndexed;
import cz.inqool.eas.common.dated.index.DatedIndexedObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Building block for Index entities, which want to track creation, update and deletion.
 * <p>
 * Provides attributes {@link AuthoredIndexedObject#created}, {@link AuthoredIndexedObject#updated} and {@link
 * AuthoredIndexedObject#deleted}.
 * <p>
 *
 * @param <ROOT> Root of the projection type system
 * @param <PROJECTED> Index projection type
 */
@Getter
@Setter
@FieldNameConstants
abstract public class AuthoredIndexedObject<ROOT extends Authored<ROOT>, PROJECTED extends Authored<ROOT>> extends DatedIndexedObject<ROOT, PROJECTED> implements AuthoredIndexed<ROOT, PROJECTED> {

    @Field(type = FieldType.Object)
    protected UserReferenceIndexed createdBy;

    @Field(type = FieldType.Object)
    protected UserReferenceIndexed updatedBy;

    @Field(type = FieldType.Object)
    protected UserReferenceIndexed deletedBy;

    @Field(type = FieldType.Object)
    protected TenantReferenceIndexed createdByTenant;

    @Field(type = FieldType.Object)
    protected TenantReferenceIndexed updatedByTenant;

    @Field(type = FieldType.Object)
    protected TenantReferenceIndexed deletedByTenant;

    @Override
    public void toIndexedObject(PROJECTED obj) {
        super.toIndexedObject(obj);

        this.createdBy = UserReferenceIndexed.of(obj.getCreatedBy());
        this.updatedBy = UserReferenceIndexed.of(obj.getUpdatedBy());
        this.deletedBy = UserReferenceIndexed.of(obj.getDeletedBy());

        this.createdByTenant = TenantReferenceIndexed.of(obj.getCreatedByTenant());
        this.updatedByTenant = TenantReferenceIndexed.of(obj.getUpdatedByTenant());
        this.deletedByTenant = TenantReferenceIndexed.of(obj.getDeletedByTenant());
    }
}
