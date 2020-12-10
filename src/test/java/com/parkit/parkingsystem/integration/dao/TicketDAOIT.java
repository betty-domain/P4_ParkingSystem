package com.parkit.parkingsystem.integration.dao;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.DateConvertUtil;
import javassist.expr.Instanceof;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;


public class TicketDAOIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static DataBasePrepareService dataBasePrepareService;

    private TicketDAO ticketDAO;
    private Ticket ticket;
    private String vehiclePlateNumber;

    @BeforeAll
    private static void setUp() throws Exception{
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        dataBasePrepareService.clearDataBaseEntries();
        ticketDAO = new TicketDAO();
        ticketDAO.setDataBaseConfig(dataBaseTestConfig);
        ticket =  new Ticket();
        ticket.setId(-1);
        ticket.setPrice(10.5);
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR,false));
        vehiclePlateNumber = "myCarPlate";
        ticket.setVehicleRegNumber(vehiclePlateNumber);

        LocalDateTime inDateTime = LocalDateTime.of(2020,10,10,10,50,15);
        ticket.setInTime(DateConvertUtil.convertToDate(inDateTime));
    }

    private void assertEqualsTickets(Ticket expectedTicket, Ticket resultTicket)
    {
        assertEquals(expectedTicket.getPrice(),resultTicket.getPrice());
        assertEquals(expectedTicket.getParkingSpot(),resultTicket.getParkingSpot());
        assertEquals(expectedTicket.getInTime(),resultTicket.getInTime());
        assertEquals(expectedTicket.getOutTime(),resultTicket.getOutTime());

        assertEquals(expectedTicket.getVehicleRegNumber(),resultTicket.getVehicleRegNumber());
    }

    @Test
    public void selectTicketWithoutTicketTest(){
        Ticket selectedTicket =  ticketDAO.getTicket("ABCDE");
        assertNull(selectedTicket);
    }

    @Test
    public void saveTicketForExistingParkingSpot(){
        ticketDAO.saveTicket(ticket);
        Ticket selectedTicket =  ticketDAO.getTicket(vehiclePlateNumber);

        this.assertEqualsTickets(ticket,selectedTicket);
    }

    @Test
    public void saveTicketForUnexistingParkingSpot(){
        ticket.getParkingSpot().setId(-1);
        assertFalse(ticketDAO.saveTicket(ticket));
    }

    @Test
    public void updateTicket(){
//TODO : à revoir si test qui appelle toutes les méthodes du DAO est pertinent, car pas indépendant
        ticketDAO.saveTicket(ticket);

        Ticket savedTicket = ticketDAO.getTicket(ticket.getVehicleRegNumber());
        savedTicket.setPrice(25.0);
        LocalDateTime outUpdatedTime = LocalDateTime.of(2020,10,15,10,50,15);
        savedTicket.setOutTime(DateConvertUtil.convertToDate(outUpdatedTime));
        ticketDAO.updateTicket(savedTicket);

        Ticket selectedTicket = ticketDAO.getPaidTickets(savedTicket.getVehicleRegNumber()).get(0);

        this.assertEqualsTickets(savedTicket,selectedTicket);
    }

}
