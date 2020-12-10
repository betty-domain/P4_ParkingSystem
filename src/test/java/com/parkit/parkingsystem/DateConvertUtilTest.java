package com.parkit.parkingsystem;

import com.parkit.parkingsystem.util.DateConvertUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateConvertUtilTest {

    private static DateConvertUtil dateConvertUtil;

    private final LocalDateTime startDateTime = LocalDateTime.of(2020,1,1,10,0,0);

    @BeforeAll
    private static void setUpAllTest()
    {

    }

    @Test
    public void getDecimalHoursFromOneHourDuration(){
        LocalDateTime endDateTime = startDateTime.plusHours(1);
        Duration duration = Duration.between(startDateTime,endDateTime);

        double result =DateConvertUtil.getDecimalHoursFromDuration(duration);
        assertEquals( 1.0, result);
    }

    @Test
    public void getDecimalHoursFromNegativeOneHourDuration(){

        LocalDateTime endDateTime = startDateTime.minusHours(1);
        Duration duration = Duration.between(startDateTime,endDateTime);

        double result =DateConvertUtil.getDecimalHoursFromDuration(duration);
        assertEquals(-1.0, result );
    }

    @Test
    public void getDecimalHoursFromDurationWithLessThanOneHour(){

        LocalDateTime endDateTime = startDateTime.plusMinutes(45);
        Duration duration = Duration.between(startDateTime,endDateTime);
        double result =DateConvertUtil.getDecimalHoursFromDuration(duration);
        assertEquals(0.75, result );
    }

    @Test
    public void getDecimalHoursFromDurationWithMoreThanOneHour(){

        LocalDateTime endDateTime = startDateTime.plusMinutes(45).plusHours(2);

        Duration duration = Duration.between(startDateTime,endDateTime);
        double result =DateConvertUtil.getDecimalHoursFromDuration(duration);
        assertEquals(2.75, result );
    }

    @Test
    public void getDecimalHoursFromDurationWithMoreThanOneDay(){

        LocalDateTime endDateTime = startDateTime.plusMinutes(45).plusHours(2).plusDays(2);

        Duration duration = Duration.between(startDateTime,endDateTime);
        double result =DateConvertUtil.getDecimalHoursFromDuration(duration);
        assertEquals(50.75 , result );
    }
}
