package cz.inqool.eas.common.domain.index.dto.params;

import com.google.common.annotations.Beta;

/**
 * Constants for search request modification. Can be used to specify to not return items in a special state. Subprojects
 * can extend this class to add their own constants.
 */
public class Exclude {

    /**
     * Do not return deleted items
     */
    @Beta
    public static final String DELETED = "DELETED";

    /**
     * Do not return deactivated items (in case of dictionaries)
     */
    @Beta
    public static final String DEACTIVATED = "DEACTIVATED";

    /**
     * Do not return items with invalid {@code validFrom} and/or {@code validTo} values (in case of dictionaries)
     */
    @Beta
    public static final String INVALID = "INVALID";
}
