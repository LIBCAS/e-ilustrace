package cz.inqool.eas.common.authored.index;

import cz.inqool.eas.common.authored.AuthoredIndexed;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.projection.Projectable;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.data.elasticsearch.core.document.Document;

/**
 * Index store for objects extending {@link AuthoredIndexedObject} with standard CRUD operations.
 * <p>
 * Objects are not removed on delete, rather a delete flag is set.
 *
 * @param <ROOT>      Root of the projection type system
 * @param <PROJECTED> Index projection type
 * @param <INDEXED>   Indexed object type
 */
@Slf4j
@Setter
public class AuthoredIndex<ROOT extends Projectable<ROOT>, PROJECTED extends Projectable<ROOT>, INDEXED extends AuthoredIndexed<ROOT, PROJECTED>> extends DatedIndex<ROOT, PROJECTED, INDEXED> {

    public AuthoredIndex(Class<INDEXED> indexedType) {
        super(indexedType);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Also set deletedBy flag.
     */
    @Override
    protected UpdateRequest createDeleteRequest(INDEXED obj) {
        String deleted = obj.getDeleted() != null ? dateConverter.format(obj.getDeleted()) : null;
        Document deletedBy = obj.getDeletedBy() != null ? converter.mapObject(obj.getDeletedBy()) : null;
        Document deletedByTenant = obj.getDeletedByTenant() != null ? converter.mapObject(obj.getDeletedByTenant()) : null;

        return super.createDeleteRequest(obj).
                doc("deleted", deleted,
                        "deletedBy", deletedBy,
                        "deletedByTenant", deletedByTenant
                );
    }
}
