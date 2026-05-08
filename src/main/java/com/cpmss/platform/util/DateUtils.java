package com.cpmss.platform.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Date/time helpers used across the application.
 */
public final class DateUtils {

    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private DateUtils() {}

    /**
     * Formats an {@link Instant} to an ISO-8601 date string in UTC.
     *
     * @param instant the instant to format
     * @return date string, e.g. {@code "2026-05-04"}
     */
    public static String toIsoDate(Instant instant) {
        return ISO_DATE.format(instant.atZone(ZoneOffset.UTC).toLocalDate());
    }

    /**
     * Returns the UTC midnight {@link Instant} for a given {@link LocalDate}.
     *
     * @param date the local date
     * @return the UTC midnight instant for that date
     */
    public static Instant startOfDay(LocalDate date) {
        return date.atStartOfDay(ZoneOffset.UTC).toInstant();
    }
}
