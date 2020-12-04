package com.parkit.parkingsystem.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Utility Class to convert Date object
 */
public final class DateConvertUtil {

    /**
     * Private Constructor for Utility Class
     */
    private DateConvertUtil()
    {

    }

    /**
     * Convert a Date object into a LocalDateTime Object using the Instant object to convert
     * @param dateToConvert Date to convert
     * @return Converted Date into LocalDateTime
     */
    public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert)
    {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static double getDecimalHoursFromDuration(Duration duration)
    {
        long minutes = duration.toMinutes();
        double partOfHours = minutes / 60.0;

        return partOfHours;
    }
}
