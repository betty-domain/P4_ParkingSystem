package com.parkit.parkingsystem.integration.dao;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.DateConvertUtil;
import com.sun.crypto.provider.TlsKeyMaterialGenerator;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketDAOIT {

    private static DataBaseTestConfig dataBaseTestConfig;
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
        dataBaseTestConfig =  spy(new DataBaseTestConfig());
        dataBasePrepareService.clearDataBaseEntries();
        ticketDAO = new TicketDAO();
        ticketDAO.setDataBaseConfig(dataBaseTestConfig);
        ticket =  new Ticket();
        /*ticket.setId(-1);
        ticket.setPrice(10.5);*/
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR,false));
        vehiclePlateNumber = "myCarPlate";
        ticket.setVehicleRegNumber(vehiclePlateNumber);

        LocalDateTime inDateTime = LocalDateTime.of(2020,10,10,10,50,15);
        ticket.setInTime(DateConvertUtil.convertToDate(inDateTime));
    }

    /**
     * Intialize a ticket into database in order to write independant tests
     * @param ticket : ticket to save in database
     * @throws Exception
     */
    private void insertTicketInDatabase(Ticket ticket) throws Exception
    {
        try (Connection con = dataBaseTestConfig.getConnection(); PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1,ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
            ps.setTimestamp(5, (ticket.getOutTime() == null)?null: (new Timestamp(ticket.getOutTime().getTime())) );
            ps.execute();
            ResultSet resultSetId =ps.getGeneratedKeys();
            resultSetId.next();
            ticket.setId(resultSetId.getInt(1));
        }
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
    public void selectTicketWithSqlException() throws Exception {
        Ticket ticket = new Ticket();

        when(dataBaseTestConfig.getConnection()).thenThrow(new SQLException());
        ticket = ticketDAO.getTicket("abcde");

        assertNull(ticket);
    }

    @Test
    public void saveTicketForExistingParkingSpot(){
        ticketDAO.saveTicket(ticket);
        Ticket selectedTicket =  ticketDAO.getTicket(vehiclePlateNumber);

        this.assertEqualsTickets(ticket,selectedTicket);
    }
    @Test
    public void saveTicketWithSqlException() throws Exception{
        when(dataBaseTestConfig.getConnection()).thenThrow(new SQLException());

        assertFalse(ticketDAO.saveTicket(ticket));
    }

    @Test
    public void saveTicketForUnexistingParkingSpot(){
        ticket.getParkingSpot().setId(-1);
        assertFalse(ticketDAO.saveTicket(ticket));
    }


    @Test
    public void getPaidTicketWithoutPaidTickets() throws Exception {


        try {
            insertTicketInDatabase(ticket);
            assertThat(ticketDAO.getPaidTickets(ticket.getVehicleRegNumber())).isEmpty();
        }
        catch (Exception ex)
        {
            System.out.println("Error in getting Paid Tickets Integration Test");
            throw ex;
        }
    }

    @Test
    public void getPaidTicketWithSqlException() throws Exception {
        when(dataBaseTestConfig.getConnection()).thenThrow(new SQLException());
        assertThat(ticketDAO.getPaidTickets(ticket.getVehicleRegNumber())).isEmpty();
    }

    @Test
    public void updateTicket(){
        Ticket selectedTicket = new Ticket();
        try {
            insertTicketInDatabase(ticket);

            ticket.setPrice(25.0);
            LocalDateTime outUpdatedTime = LocalDateTime.of(2020, 10, 15, 10, 50, 15);
            ticket.setOutTime(DateConvertUtil.convertToDate(outUpdatedTime));
            ticketDAO.updateTicket(ticket);

            selectedTicket = ticketDAO.getPaidTickets(ticket.getVehicleRegNumber()).get(0);
        }
        catch (Exception ex)
        {
            System.out.println("Error in updateTicket Integration Test");
        }
        this.assertEqualsTickets(ticket, selectedTicket);
    }

    @Test
    public void updateTicketWithSqlException() throws Exception{
        when(dataBaseTestConfig.getConnection()).thenThrow(new SQLException());

        assertFalse(ticketDAO.updateTicket(ticket));
    }

}
