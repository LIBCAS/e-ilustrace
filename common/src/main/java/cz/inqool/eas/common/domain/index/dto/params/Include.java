package cz.inqool.eas.common.domain.index.dto.params;

/**
 * Constants for search request modification. Can be used to specify to return items in a special state. Subprojects
 * can extend this class to add their own constants.
 */
public class Include {

    /**
     * Return deleted items
     */
    public static final String DELETED = "DELETED";

    /**
     * Return deactivated items (in case of dictionaries)
     */
    public static final String DEACTIVATED = "DEACTIVATED";

    /**
     * Return items with invalid {@code validFrom} and/or {@code validTo} values (in case of dictionaries)
     */
    public static final String INVALID = "INVALID";
}
