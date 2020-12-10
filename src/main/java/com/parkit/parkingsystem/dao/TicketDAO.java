package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");

    private DataBaseConfig dataBaseConfig = new DataBaseConfig();
    public DataBaseConfig getDataBaseConfig() {
        return dataBaseConfig;
    }

    public void setDataBaseConfig(DataBaseConfig dataBaseConfig) {
        this.dataBaseConfig = dataBaseConfig;
    }
    
    public boolean saveTicket(Ticket ticket){

        try (Connection con = dataBaseConfig.getConnection(); PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET)){
            ps.setInt(1,ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
            ps.setTimestamp(5, (ticket.getOutTime() == null)?null: (new Timestamp(ticket.getOutTime().getTime())) );
            ps.execute();
            return true;
        }catch (SQLException | ClassNotFoundException ex){
            logger.error("Error saving ticket",ex);
            return false;
        }
    }

    public Ticket getTicket(String vehicleRegNumber) {

        Ticket ticket = null;

        try (Connection con = dataBaseConfig.getConnection(); PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET)){

            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1,vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)),false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(2));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(3));
                ticket.setInTime(rs.getTimestamp(4));
                ticket.setOutTime(rs.getTimestamp(5));
            }
            dataBaseConfig.closeResultSet(rs);
        }
        catch (SQLException | ClassNotFoundException ex){
            logger.error("Error getting Ticket" ,ex);
        }
        return ticket;
    }

    public boolean updateTicket(Ticket ticket) {

        try (Connection con =  dataBaseConfig.getConnection();PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET)){

            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
            ps.setInt(3,ticket.getId());
            ps.execute();
            return true;
        }catch (SQLException | ClassNotFoundException ex){

            logger.error("Error updating ticket info",ex);
        }
        return false;
    }

    /**
     * Collect all Paid tickets for a vehicleRegistrationNumber
     * @param vehicleRegistrationNumber found vehicle registration number
     * @return List of paid tickets relative to vehicle registration number
     */
    public List<Ticket> getPaidTickets(String vehicleRegistrationNumber) {

        List<Ticket> listTickets = new ArrayList<>();
        Ticket ticket ;
        try (Connection con =  dataBaseConfig.getConnection(); PreparedStatement ps = con.prepareStatement(DBConstants.GET_PAID_TICKET)){
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1,vehicleRegistrationNumber);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)),rs.getBoolean(7));
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(2));
                ticket.setVehicleRegNumber(vehicleRegistrationNumber);
                ticket.setPrice(rs.getDouble(3));
                ticket.setInTime(rs.getTimestamp(4));
                ticket.setOutTime(rs.getTimestamp(5));
                listTickets.add(ticket);
            }
            dataBaseConfig.closeResultSet(rs);
        }catch (SQLException | ClassNotFoundException ex){
            logger.error("Error getting Paid Tickets",ex);
        }
        return listTickets;
    }


}
