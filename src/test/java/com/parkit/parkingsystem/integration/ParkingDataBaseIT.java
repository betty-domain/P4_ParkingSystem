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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {


    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    private final String vehicleRegistrationNumber = "ABCDEF";

    private LocalDateTime entryDateTime;

    private ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtilMock;

    @Mock
    private static ClockUtil clockUtilMock;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.setDataBaseConfig(dataBaseTestConfig);
        ticketDAO = new TicketDAO();
        ticketDAO.setDataBaseConfig(dataBaseTestConfig);
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        entryDateTime = LocalDateTime.of(2020,1,1,10,10,10);
        when(clockUtilMock.getCurrentDate()).thenReturn(DateConvertUtil.convertToDate(entryDateTime));

        when(inputReaderUtilMock.readSelection()).thenReturn(1);
        when(inputReaderUtilMock.readVehicleRegistrationNumber()).thenReturn(vehicleRegistrationNumber);
        dataBasePrepareService.clearDataBaseEntries();

        parkingService = new ParkingService(inputReaderUtilMock, parkingSpotDAO, ticketDAO, clockUtilMock);
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){

        parkingService.processIncomingVehicle();

        //check that a ticket is actually saved in DB
        Ticket registeredTicket = ticketDAO.getTicket(vehicleRegistrationNumber);


        assertThat(registeredTicket).isNotNull();

        int availableSpot = parkingSpotDAO.getNextAvailableSlot(registeredTicket.getParkingSpot().getParkingType());
        //check Parking table is updated with availability
        assertThat(availableSpot).isNotEqualTo(registeredTicket.getParkingSpot().getId());
    }

    @Test
    public void testParkingLotExit(){

        LocalDateTime entryDateTime = LocalDateTime.of(2020,1,1,10,10,10);
        int hoursOfParking = 3;
        LocalDateTime exitDateTime = entryDateTime.plusHours(hoursOfParking);

        int initialAvailableSpot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        when(clockUtilMock.getCurrentDate()).thenReturn(DateConvertUtil.convertToDate(entryDateTime));
        parkingService.processIncomingVehicle();

        when(clockUtilMock.getCurrentDate()).thenReturn(DateConvertUtil.convertToDate(exitDateTime));
        parkingService.processExitingVehicle();

        Ticket registeredTicket = ticketDAO.getPaidTickets(vehicleRegistrationNumber).get(0);
        int availableSpot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        //check parkingSpot is free after exiting
        assertEquals(initialAvailableSpot, availableSpot);
        //Check fare and outTime are correctly set
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


        int hoursOfParking = 3;
        LocalDateTime exitDateTime = entryDateTime.plusHours(hoursOfParking);

        //registering first ticket
        parkingService.processIncomingVehicle();

        when(clockUtilMock.getCurrentDate()).thenReturn(DateConvertUtil.convertToDate(exitDateTime));
        parkingService.processExitingVehicle();

        //registering second ticket
        when(clockUtilMock.getCurrentDate()).thenReturn(DateConvertUtil.convertToDate(entryDateTime.plusMonths(1)));
        parkingService.processIncomingVehicle();

        when(clockUtilMock.getCurrentDate()).thenReturn(DateConvertUtil.convertToDate(exitDateTime.plusMonths(1)));
        parkingService.processExitingVehicle();

        List<Ticket> listTickets = ticketDAO.getPaidTickets(vehicleRegistrationNumber);

        //first ticket must be more expensive than second one because of the applied discount
        assertThat(listTickets.get(0).getPrice()).isGreaterThan(listTickets.get(1).getPrice());
    }

}
