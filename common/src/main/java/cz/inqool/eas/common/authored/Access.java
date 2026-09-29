package cz.inqool.eas.common.authored;

/**
 * Defines access to resources based on author and his tenant.
 */
public enum Access {
    /**
     * None access.
     */
    NONE,

    /**
     * Access to all objects.
     */
    ALL,

    /**
     * Access to object created by user tenant.
     */
    TENANT,

    /**
     * Access to object created by self.
     */
    SELF
}
