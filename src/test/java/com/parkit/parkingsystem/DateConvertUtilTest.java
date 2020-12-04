package com.parkit.parkingsystem;

import com.parkit.parkingsystem.util.DateConvertUtil;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateConvertUtilTest {

    private final LocalDateTime startDateTime = LocalDateTime.of(2020,1,1,10,0,0);

    @Test
    public void getDecimalHoursFromOneHourDuration(){
        LocalDateTime endDateTime = startDateTime.plusHours(1);
        Duration duration = Duration.between(startDateTime,endDateTime);

        double result =DateConvertUtil.getDecimalHoursFromDuration(duration);
        assertEquals(result, 1.0);
    }

    @Test
    public void getDecimalHoursFromNegativeOneHourDuration(){

        LocalDateTime endDateTime = startDateTime.minusHours(1);
        Duration duration = Duration.between(startDateTime,endDateTime);

        double result =DateConvertUtil.getDecimalHoursFromDuration(duration);
        assertEquals(result, -1.0);
    }

    @Test
    public void getDecimalHoursFromDurationWithLessThanOneHour(){

        LocalDateTime endDateTime = startDateTime.plusMinutes(45);
        Duration duration = Duration.between(startDateTime,endDateTime);
        double result =DateConvertUtil.getDecimalHoursFromDuration(duration);
        assertEquals(result, 0.75);
    }

    @Test
    public void getDecimalHoursFromDurationWithMoreThanOneHour(){

        LocalDateTime endDateTime = startDateTime.plusMinutes(45).plusHours(2);

        Duration duration = Duration.between(startDateTime,endDateTime);
        double result =DateConvertUtil.getDecimalHoursFromDuration(duration);
        assertEquals(result, 2.75);
    }

    @Test
    public void getDecimalHoursFromDurationWithMoreThanOneDay(){

        LocalDateTime endDateTime = startDateTime.plusMinutes(45).plusHours(2).plusDays(2);

        Duration duration = Duration.between(startDateTime,endDateTime);
        double result =DateConvertUtil.getDecimalHoursFromDuration(duration);
        assertEquals(result, 50.75);
    }
}
