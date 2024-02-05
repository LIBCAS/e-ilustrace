package cz.inqool.eas.common.utils;

import org.joda.time.Interval;
import org.springframework.lang.Nullable;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.*;
import java.util.GregorianCalendar;

public class DateUtils {

    private static DatatypeFactory factory;

    static {
        try {
            factory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if range computed from {@code validFrom} and {@code validTo} covers present date.
     */
    public static boolean isValid(@Nullable LocalDate validFrom, @Nullable LocalDate validTo) {
        LocalDate now = LocalDate.now();

        LocalDate start = (validFrom != null) ? validFrom : LocalDate.MIN;
        LocalDate end   = (validTo   != null) ? validTo   : LocalDate.MAX;

        return !now.isBefore(start) && !now.isAfter(end);
    }

    /**
     * Check if range computed from {@code validFrom} and {@code validTo} covers present datetime.
     */
    public static boolean isValid(@Nullable LocalDateTime validFrom, @Nullable LocalDateTime validTo) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime start = (validFrom != null) ? validFrom : LocalDateTime.MIN;
        LocalDateTime end   = (validTo   != null) ? validTo   : LocalDateTime.MAX;

        return !now.isBefore(start) && !now.isAfter(end);
    }

    /**
     * Check if range computed from {@code validFrom} and {@code validTo} covers present moment.
     */
    public static boolean isValid(@Nullable Instant validFrom, @Nullable Instant validTo) {
        Instant now = Instant.now();

        Instant start = (validFrom != null) ? validFrom : Instant.MIN;
        Instant end   = (validTo   != null) ? validTo   : Instant.MAX;

        return !now.isBefore(start) && !now.isAfter(end);
    }

    /**
     * Converts given {@link Instant} value to {@link XMLGregorianCalendar}
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(@Nullable Instant value) {
        if (value == null) {
            return null;
        }

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(value.toEpochMilli());

        return factory.newXMLGregorianCalendar(calendar);
    }

    /**
     * Converts given {@link LocalDateTime} value to {@link XMLGregorianCalendar}
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(@Nullable LocalDateTime value) {
        if (value == null) {
            return null;
        }

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(value.atZone(ZoneId.systemDefault()).toEpochSecond() * 1_000);

        return factory.newXMLGregorianCalendar(calendar);
    }

    /**
     * Converts given {@link LocalDate} value to {@link XMLGregorianCalendar}
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(@Nullable LocalDate value) {
        if (value == null) {
            return null;
        }

        return toXMLGregorianCalendar(value.atStartOfDay());
    }

    /**
     * Converts given {@link XMLGregorianCalendar} value to {@link Instant}
     */
    public static Instant toInstant(@Nullable XMLGregorianCalendar value) {
        if (value == null) {
            return null;
        }

        return value.toGregorianCalendar().toInstant();
    }

    /**
     * Converts given {@link Instant} value to {@link LocalDateTime}
     */
    public static LocalDateTime toLocalDateTime(@Nullable Instant value) {
        if (value == null) {
            return null;
        }

        return LocalDateTime.ofInstant(value, ZoneId.systemDefault());
    }

    /**
     * Converts given {@link XMLGregorianCalendar} value to {@link LocalDateTime}
     */
    public static LocalDateTime toLocalDateTime(@Nullable XMLGregorianCalendar value) {
        if (value == null) {
            return null;
        }

        return LocalDateTime.ofInstant(toInstant(value), ZoneId.systemDefault());
    }

    /**
     * Converts given {@link XMLGregorianCalendar} value to {@link LocalDate}
     */
    public static LocalDate toLocalDate(@Nullable XMLGregorianCalendar value) {
        if (value == null) {
            return null;
        }

        return LocalDate.ofInstant(toInstant(value), ZoneId.systemDefault());
    }

    /**
     * Creates an interval from given {@link LocalDateTime} values ({@code null} value means unbounded interval)
     */
    public static Interval toInterval(@Nullable LocalDateTime from, @Nullable LocalDateTime to) {
        ZoneId zoneId = ZoneOffset.systemDefault();

        long start = (from != null) ? from.atZone(zoneId).toInstant().toEpochMilli() : Long.MIN_VALUE;
        long end   = (to   != null) ? to.atZone(zoneId).toInstant().toEpochMilli()   : Long.MAX_VALUE;

        return new Interval(start, end);
    }
}
