package cz.inqool.eas.common.xml;


import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Year;

/**
 * {@code XmlAdapter} mapping JSR-310 {@code Year} to ISO proleptic year number
 * <p>
 * Year number interpretation details:
 * <ul>
 * <li>{@link java.time.Year#parse(CharSequence)}</li>
 * <li>{@link java.time.Year#toString()}</li>
 * </ul>
 *
 * @see javax.xml.bind.annotation.adapters.XmlAdapter
 * @see java.time.Year
 */
public class YearXmlAdapter extends XmlAdapter<String, Year> {
    @Override
    public Year unmarshal(String isoYearStr) {
        return isoYearStr != null ? Year.parse(isoYearStr) : null;
    }

    @Override
    public String marshal(Year year) {
        return year != null ? year.toString() : null;
    }
}

