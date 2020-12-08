package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.util.DateConvertUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @Mock
    private TicketDAO ticketDAO;

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

    @Test
    public void calculateFareCar30minutesParking()
    {
        LocalDateTime inDateTime = LocalDateTime.of(2020,1,1,12,0,0);
        LocalDateTime outDateTime = inDateTime.plusMinutes(30);
        ticket.setInTime(DateConvertUtil.convertToDate(inDateTime));
        ticket.setOutTime(DateConvertUtil.convertToDate(outDateTime));

        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.CAR,false);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        assertEquals(0.5 * Fare.CAR_RATE_PER_HOUR,ticket.getPrice());
    }

    @Test
    public void calculateFareBike30minutesParking()
    {
        LocalDateTime inDateTime = LocalDateTime.of(2020,1,1,12,0,0);
        LocalDateTime outDateTime = inDateTime.plusMinutes(30);

        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.BIKE,false);
        ticket.setInTime(DateConvertUtil.convertToDate(inDateTime));
        ticket.setOutTime(DateConvertUtil.convertToDate(outDateTime));
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        assertEquals(0.5 * Fare.BIKE_RATE_PER_HOUR,ticket.getPrice());
    }


    @Test
    public void calculateFareForRecurrentCar()
    {
        String vehicleRegistrationNumber = "myCar";

        //Initialize first ticket to inform system that vehicle has already come
        List<Ticket> lstPreviousTickets = new ArrayList<Ticket>() ;
        Ticket firstTicket = new Ticket();
        firstTicket.setPrice(10.0);
        firstTicket.setVehicleRegNumber(vehicleRegistrationNumber);
        LocalDateTime inLocalDateTime = LocalDateTime.of(2020,5,15,15,0,0);
        LocalDateTime outLocalDateTime = inLocalDateTime.plusHours(3);

        firstTicket.setOutTime(DateConvertUtil.convertToDate(inLocalDateTime));
        firstTicket.setInTime(DateConvertUtil.convertToDate(outLocalDateTime));
        lstPreviousTickets.add(firstTicket);

        //mock getPaidTickets (free tickets are excluded of discount)
        when (ticketDAO.getPaidTickets(vehicleRegistrationNumber)).thenReturn(lstPreviousTickets);

        Ticket secondTicket = new Ticket();
        secondTicket.setVehicleRegNumber(vehicleRegistrationNumber);
        long hoursParkingDuration = 3;

        inLocalDateTime = LocalDateTime.of(2020,5,15,15,0,0);
        outLocalDateTime = inLocalDateTime.plusHours(hoursParkingDuration);
        secondTicket.setInTime(DateConvertUtil.convertToDate(inLocalDateTime));
        secondTicket.setOutTime(DateConvertUtil.convertToDate(outLocalDateTime));

        ParkingSpot currentParkingSpot = new ParkingSpot(1,ParkingType.CAR,false);
        secondTicket.setParkingSpot(currentParkingSpot);

        fareCalculatorService.calculateFare(secondTicket);

        assertEquals(Fare.DISCOUNT_PERCENT_FOR_REGULAR_USER * Fare.CAR_RATE_PER_HOUR * hoursParkingDuration, secondTicket.getPrice());
    }

    @Test
    public void calculateFareForRecurrentBike()
    {
        String vehicleRegistrationNumber = "myCar";

        //Initialize first ticket to inform system that vehicle has already come
        List<Ticket> lstPreviousTickets = new ArrayList<Ticket>() ;
        Ticket firstTicket = new Ticket();
        firstTicket.setPrice(10.0);
        firstTicket.setVehicleRegNumber(vehicleRegistrationNumber);
        LocalDateTime inLocalDateTime = LocalDateTime.of(2020,5,15,15,0,0);
        LocalDateTime outLocalDateTime = inLocalDateTime.plusHours(3);

        firstTicket.setOutTime(DateConvertUtil.convertToDate(inLocalDateTime));
        firstTicket.setInTime(DateConvertUtil.convertToDate(outLocalDateTime));
        lstPreviousTickets.add(firstTicket);

        //mock getPaidTickets (free tickets are excluded of discount)
        when (ticketDAO.getPaidTickets(vehicleRegistrationNumber)).thenReturn(lstPreviousTickets);

        Ticket secondTicket = new Ticket();
        secondTicket.setVehicleRegNumber(vehicleRegistrationNumber);
        long hoursParkingDuration = 3;

        inLocalDateTime = LocalDateTime.of(2020,5,15,15,0,0);
        outLocalDateTime = inLocalDateTime.plusHours(hoursParkingDuration);
        secondTicket.setInTime(DateConvertUtil.convertToDate(inLocalDateTime));
        secondTicket.setOutTime(DateConvertUtil.convertToDate(outLocalDateTime));

        ParkingSpot currentParkingSpot = new ParkingSpot(1,ParkingType.BIKE,false);
        secondTicket.setParkingSpot(currentParkingSpot);

        fareCalculatorService.calculateFare(secondTicket);

        assertEquals(Fare.DISCOUNT_PERCENT_FOR_REGULAR_USER * Fare.BIKE_RATE_PER_HOUR * hoursParkingDuration, secondTicket.getPrice());
    }

}
