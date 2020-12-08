package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.ClockUtil;
import com.parkit.parkingsystem.util.DateConvertUtil;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Date;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {


    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static DateConvertUtil dateConvertUtil;
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    private final String vehicleRegistrationNumber = "ABCDEF";

    private ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtilMock;

    @Mock
    private static ClockUtil clockUtilMock;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dateConvertUtil = new DateConvertUtil();
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtilMock.readSelection()).thenReturn(ParkingType.CAR.ordinal());
        when(inputReaderUtilMock.readVehicleRegistrationNumber()).thenReturn(vehicleRegistrationNumber);
        dataBasePrepareService.clearDataBaseEntries();

        parkingService = new ParkingService(inputReaderUtilMock, parkingSpotDAO, ticketDAO, clockUtilMock);
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){

        when(clockUtilMock.getCurrentDate()).thenReturn(new Date());
        parkingService.processIncomingVehicle();

        //TODO : voir deux asserts dans la même méthode

        //check that a ticket is actually saved in DB
        Ticket registeredTicket = ticketDAO.getTicket(vehicleRegistrationNumber);

        assertNotNull(registeredTicket);

        int availableSpot = parkingSpotDAO.getNextAvailableSlot(registeredTicket.getParkingSpot().getParkingType());
        //check Parking table is updated with availability
        assertNotEquals(availableSpot, registeredTicket.getParkingSpot().getId());

    }

    @Test
    public void testParkingLotExit(){

        LocalDateTime entryDateTime = LocalDateTime.of(2020,1,1,10,10,10);
        int hoursOfParking = 3;
        LocalDateTime exitDateTime = entryDateTime.plusHours(hoursOfParking);

        int initialAvailableSpot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        when(clockUtilMock.getCurrentDate()).thenReturn(dateConvertUtil.convertToDate(entryDateTime));
        parkingService.processIncomingVehicle();

        when(clockUtilMock.getCurrentDate()).thenReturn(dateConvertUtil.convertToDate(exitDateTime));
        parkingService.processExitingVehicle();

        Ticket registeredTicket = ticketDAO.getTicket(vehicleRegistrationNumber);
        int availableSpot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        //TODO : à revoir car plusieurs asserts sur cette méthode
        assertEquals(initialAvailableSpot, availableSpot);
        assertEquals(Fare.CAR_RATE_PER_HOUR * hoursOfParking,registeredTicket.getPrice());
        assertEquals(DateConvertUtil.convertToDate(exitDateTime), registeredTicket.getOutTime());
    }

    @Test
    public void testNextAvailableParkingSlot()
    {
        ParkingSpot firstParkingSpot = parkingService.getNextParkingNumberIfAvailable();
        parkingService.processIncomingVehicle();

        ParkingSpot secondParkingSpot = parkingService.getNextParkingNumberIfAvailable();

        assertNotEquals(firstParkingSpot,secondParkingSpot);

    }

    @Test
    public void testParkingRecurrentCar(){

        LocalDateTime entryDateTime = LocalDateTime.of(2020,1,1,10,10,10);
        int hoursOfParking = 3;
        LocalDateTime exitDateTime = entryDateTime.plusHours(hoursOfParking);

        //registering first ticket
        when(clockUtilMock.getCurrentDate()).thenReturn(dateConvertUtil.convertToDate(entryDateTime));
        parkingService.processIncomingVehicle();

        when(clockUtilMock.getCurrentDate()).thenReturn(dateConvertUtil.convertToDate(exitDateTime));
        parkingService.processExitingVehicle();

        Ticket firstTicket = ticketDAO.getTicket(vehicleRegistrationNumber);

        //registering second ticket
        when(clockUtilMock.getCurrentDate()).thenReturn(dateConvertUtil.convertToDate(entryDateTime));
        parkingService.processIncomingVehicle();

        when(clockUtilMock.getCurrentDate()).thenReturn(dateConvertUtil.convertToDate(exitDateTime));
        parkingService.processExitingVehicle();

        Ticket secondTicket = ticketDAO.getTicket(vehicleRegistrationNumber);

        assertTrue(secondTicket.getPrice()<firstTicket.getPrice());
    }

}
