package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.ClockUtil;
import com.parkit.parkingsystem.util.DateConvertUtil;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Date;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    private static ClockUtil clockUtil;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeAll
    private static void setUp() {

    }

    @BeforeEach
    private void setUpPerTest() {
        clockUtil = new ClockUtil();
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, clockUtil);
        //lenient().when(dateConvertUtil.getCurrentDate()).thenReturn(new Date());

    }


    @Test
    public void getNextParkingNumberIfAvailableWhenUnavailableVehicleType() {
        when(inputReaderUtil.readSelection()).thenReturn(3);
        assertNull(parkingService.getNextParkingNumberIfAvailable());
    }

    @Test
    public void getNextParkingNumberIfAvailableWhenAvailableTypeCar() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        ParkingSpot expectedSpot = new ParkingSpot(1, ParkingType.CAR, true);
        ParkingSpot resultSpot = parkingService.getNextParkingNumberIfAvailable();

        assertEquals(resultSpot, expectedSpot);
    }

    @Test
    public void getNextParkingNumberIfAvailableWhenAvailableTypeBike() {
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(1);
        ParkingSpot expectedSpot = new ParkingSpot(1, ParkingType.BIKE, true);
        ParkingSpot resultSpot = parkingService.getNextParkingNumberIfAvailable();

        assertEquals(resultSpot, expectedSpot);
    }

    @Test
    public void getNextParkingNumberIfAvailableWhenNotAvailable() {
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(-1);

        assertNull(parkingService.getNextParkingNumberIfAvailable() );
    }

    @Test
    public void processExitingVehicleTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            Ticket ticket = new Ticket();

            LocalDateTime entryDateTime = LocalDateTime.of(2020,1,1,10,10,10);

            ticket.setInTime(DateConvertUtil.convertToDate(entryDateTime));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
            parkingService.processExitingVehicle();
            verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");

        }

    }

    @Test
    public void processExitingVehicleWithTicketUpdateErrorTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

            LocalDateTime entryDateTime = LocalDateTime.of(2020,1,1,10,10,10);
            Ticket ticket = new Ticket();
            ticket.setInTime(DateConvertUtil.convertToDate(entryDateTime));

            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
            parkingService.processExitingVehicle();
            verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");

        }

    }

    @Test
    public void processIncomingVehicleTestWithAvailableParkingSpot() {
        try {
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            parkingService.processIncomingVehicle();

            verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
            //TODO : faut-il implémenter une vérification plus précise sur les objets sauvegardés et leurs propriétés ?

            verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");

        }
    }

    @Test
    public void processIncomingVehicleTestWithUnavailableParkingSpot() {
        try {
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(-1);

            parkingService.processIncomingVehicle();

            verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
            verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");

        }
    }

    @Test
    public void processIncomingVehicleTestWithIncorrectVehicleType() {
        try {
            when(inputReaderUtil.readSelection()).thenReturn(-1);

            parkingService.processIncomingVehicle();

            verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
            verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");

        }
    }

    @Test
    public void processIncomingVehicleTestWithRecurrentVehicle() {
        try {
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            parkingService.processIncomingVehicle();

            verify(ticketDAO, Mockito.times(1)).getPaidTickets("ABCDEF");
            //TODO : voir comment vérifier l'affichage du message sur la console de sortie pour dissocier
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");

        }
    }

    @Test
    public void processIncomingVehicleTestWithRecurrentFreeParkingVehicle() {
        try {
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            parkingService.processIncomingVehicle();

            verify(ticketDAO, Mockito.times(1)).getPaidTickets("ABCDEF");
            //TODO : voir comment vérifier l'affichage du message sur la console de sortie pour dissocier
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");

        }
    }

}
