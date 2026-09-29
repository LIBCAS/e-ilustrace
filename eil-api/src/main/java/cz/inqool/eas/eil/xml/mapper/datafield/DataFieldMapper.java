package cz.inqool.eas.eil.xml.mapper.datafield;

import cz.inqool.eas.eil.record.Record;
import gov.loc.marc21.slim.DataFieldType;

public interface DataFieldMapper {

    void toAttribute(DataFieldType field, Record record);

    String getTag();

    default boolean isIllustrationMapper() { return true; }

    default boolean isBookMapper() { return true; }

//    default boolean isAuthority() { return false; }
}
