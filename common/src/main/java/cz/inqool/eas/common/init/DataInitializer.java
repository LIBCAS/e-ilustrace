package cz.inqool.eas.common.init;

import cz.inqool.eas.common.authored.store.AuthoredObject;
import cz.inqool.eas.common.authored.user.UserReference;

public interface DataInitializer {
    /** Initializer ID */
    String INITIALIZER_ID = "076e64be-817c-4588-87b7-8972e9347768";

    /** Demo ID */
    String DEMO_ID = "f3ecb61c-e71b-4d0d-a43b-6d3657162430";

    /**
     * User reference that can be used to set
     * {@link AuthoredObject#setCreatedBy(UserReference)} or {@link AuthoredObject#setUpdatedBy(UserReference)} fields.
     *
     * Use when real data should be initialized.
     */
    UserReference INITIALIZER_REF = new UserReference(INITIALIZER_ID, "Inicializace");

    /**
     * User reference that can be used to set
     * {@link AuthoredObject#setCreatedBy(UserReference)} or {@link AuthoredObject#setUpdatedBy(UserReference)} fields.
     *
     * Use when demo data should be initialized.
     */
    UserReference DEMO_REF = new UserReference(DEMO_ID, "Demo");

    /**
     * Creates / updates data in database.
     *
     * Be aware that the same data may already exist in database (from previous initializations), so proper update needs
     * to be done.
     */
    void initialize() throws Exception;

    /**
     * Removes data in database.
     *
     */
    default void initializeBackward() throws Exception {

    }
}
