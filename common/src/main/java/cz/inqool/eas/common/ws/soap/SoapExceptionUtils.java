package cz.inqool.eas.common.ws.soap;

import cz.inqool.eas.common.authored.user.UserGenerator;
import cz.inqool.eas.common.authored.user.UserReference;

import java.time.LocalDateTime;

import static cz.inqool.eas.common.utils.DateUtils.toXMLGregorianCalendar;

/**
 * Utility class with SOAP exception handling / processing methods
 */
public class SoapExceptionUtils {

    /**
     * Get current timestamp value
     */
    public static String getTimestamp() {
        return toXMLGregorianCalendar(LocalDateTime.now()).toXMLFormat();
    }

    /**
     * Generate user reference of calling user (if possible)
     */
    public static UserReference getUserReference() {
        return UserGenerator.generateValue();
    }
}
