package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.util.DateConvertUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar(){

        LocalDateTime inDateTime = LocalDateTime.of(2020,1,1,12,0,0);
        LocalDateTime outDateTime = inDateTime.plusHours(1);
        ticket.setInTime(DateConvertUtil.convertToDate(inDateTime));
        ticket.setOutTime(DateConvertUtil.convertToDate(outDateTime));

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike(){
        LocalDateTime inDateTime = LocalDateTime.of(2020,1,1,12,0,0);
        LocalDateTime outDateTime = inDateTime.plusHours(1);
        ticket.setInTime(DateConvertUtil.convertToDate(inDateTime));
        ticket.setOutTime(DateConvertUtil.convertToDate(outDateTime));

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareNullParkingType(){
        LocalDateTime inDateTime = LocalDateTime.of(2020,1,1,12,0,0);
        LocalDateTime outDateTime = inDateTime.plusHours(1);
        ticket.setInTime(DateConvertUtil.convertToDate(inDateTime));
        ticket.setOutTime(DateConvertUtil.convertToDate(outDateTime));

        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        LocalDateTime inDateTime = LocalDateTime.of(2020,1,1,12,0,0);
        LocalDateTime outDateTime = inDateTime.minusMinutes(1);
        ticket.setInTime(DateConvertUtil.convertToDate(inDateTime));
        ticket.setOutTime(DateConvertUtil.convertToDate(outDateTime));
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        LocalDateTime inDateTime = LocalDateTime.of(2020,1,1,12,0,0);
        LocalDateTime outDateTime = inDateTime.plusMinutes(45);
        ticket.setInTime(DateConvertUtil.convertToDate(inDateTime));
        ticket.setOutTime(DateConvertUtil.convertToDate(outDateTime));
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

         ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        LocalDateTime inDateTime = LocalDateTime.of(2020,1,1,12,0,0);
        LocalDateTime outDateTime = inDateTime.plusMinutes(45);
        ticket.setInTime(DateConvertUtil.convertToDate(inDateTime));
        ticket.setOutTime(DateConvertUtil.convertToDate(outDateTime));
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        LocalDateTime inDateTime = LocalDateTime.of(2020,1,1,12,0,0);
        LocalDateTime outDateTime = inDateTime.plusHours(24);
        ticket.setInTime(DateConvertUtil.convertToDate(inDateTime));
        ticket.setOutTime(DateConvertUtil.convertToDate(outDateTime));
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithoutOutTime(){
        LocalDateTime inDateTime = LocalDateTime.of(2020,1,1,12,0,0);

        ticket.setInTime(DateConvertUtil.convertToDate(inDateTime));
        ticket.setOutTime(null);

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setParkingSpot(parkingSpot);

        assertThrows(IllegalArgumentException.class,() -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareCarLessThan30minutes()
    {
        LocalDateTime inDateTime = LocalDateTime.of(2020,1,1,12,0,0);
        LocalDateTime outDateTime = inDateTime.plusMinutes(29);
        ticket.setInTime(DateConvertUtil.convertToDate(inDateTime));
        ticket.setOutTime(DateConvertUtil.convertToDate(outDateTime));

        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.CAR,false);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        assertEquals(0.0,ticket.getPrice());
    }

    @Test
    public void calculateFareBikeLessThan30minutes()
    {
        LocalDateTime inDateTime = LocalDateTime.of(2020,1,1,12,0,0);
        LocalDateTime outDateTime = inDateTime.plusMinutes(29);

        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.BIKE,false);
        ticket.setInTime(DateConvertUtil.convertToDate(inDateTime));
        ticket.setOutTime(DateConvertUtil.convertToDate(outDateTime));
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        assertEquals(0.0,ticket.getPrice());
    }

}
