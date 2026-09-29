package cz.inqool.eas.common.domain.index.dto.filter;

/**
 * Filter operation to do.
 */
public final class FilterOperation {

    /** Logical AND. Applicable to sub-filters */
    public static final String AND = "AND";

    /** Contains. Applicable to string attributes */
    public static final String CONTAINS = "CONTAINS";

    /** Custom filter */
    public static final String CUSTOM = "CUSTOM";

    /** Ends with. Applicable to string attributes */
    public static final String END_WITH = "END_WITH";

    /** Equal */
    public static final String EQ = "EQ";

    /** Equal folded. Applicable to string attributes */
    public static final String EQF = "EQF";

    /** Fulltext */
    public static final String FTX = "FTX";

    /** Match with any keyword field */
    public static final String AKF = "AKF";

    /** Fulltext field */
    public static final String FTXF = "FTXF";

    /** Geo-bounding box filter */
    public static final String GEO_BOUNDING_BOX = "GEO_BOUNDING_BOX";

    /** Geo-distance filter */
    public static final String GEO_DISTANCE = "GEO_DISTANCE";

    /** Geo-polygon filter */
    public static final String GEO_POLYGON = "GEO_POLYGON";

    /** Greater than. Applicable to number or date attributes */
    public static final String GT = "GT";

    /** Greater than or equals. Applicable to number or date attributes */
    public static final String GTE = "GTE";

    /** Between. Applicable to number or date attributes */
    public static final String RANGE = "RANGE";

    /** Filters by list of IDs */
    public static final String IDS = "IDS";

    /** Value is one among given values */
    public static final String IN = "IN";

    /** Is not set. Applicable to all attributes */
    public static final String IS_NULL = "IS_NULL";

    /** Less than. Applicable to number or date attributes */
    public static final String LT = "LT";

    /** Less than or equals. Applicable to number or date attributes */
    public static final String LTE = "LTE";

    /** Create a nested filter (to query nested object fields) */
    public static final String NESTED = "NESTED";

    /** Negates inner filter */
    public static final String NOT = "NOT";

    /** Is set. Applicable to all attributes */
    public static final String NOT_NULL = "NOT_NULL";

    /** Logical OR. Applicable to sub-filters */
    public static final String OR = "OR";

    /** Query string filter */
    public static final String QUERY_STRING = "QUERY_STRING";

    /** Starts with. Applicable to string attributes */
    public static final String START_WITH = "START_WITH";
}
