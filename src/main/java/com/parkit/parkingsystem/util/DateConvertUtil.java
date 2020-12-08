package com.parkit.parkingsystem.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Utility Class to work with Date object
 */
public class DateConvertUtil {

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

    /**
     * Convert a LocalDateTime object into a Date Object using the Instant object to convert
     * @param localDateTimeToConvert LocalDateTime to convert
     * @return Converted LocalDateTime into Date
     */
    public static Date convertToDate(LocalDateTime localDateTimeToConvert)
    {
        return Date.from(localDateTimeToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Calculate number of hours on a Duration Object, returns decimal result (ex : 0,5 for 30 minutes, 2.25 for 2h15 minutes)
     * @param duration duration objecton which calculate the number of hours
     * @return number of hours in a decimal format (ex : 0,5 for 30 minutes, 2.25 for 2h15 minutes)
     */
    public static double getDecimalHoursFromDuration(Duration duration)
    {
        long minutes = duration.toMinutes();
        double partOfHours = (double)minutes / 60.0;

        return partOfHours;
    }

}
