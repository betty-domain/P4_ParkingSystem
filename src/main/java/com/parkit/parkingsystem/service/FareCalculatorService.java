package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.DateConvertUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FareCalculatorService {

    private final TicketDAO ticketDAO;
    public FareCalculatorService(TicketDAO ticketDAO)
    {
        this.ticketDAO = ticketDAO;
    }
    /**
     * Calculate Fare for given ticket
     * @param ticket ticket to use to calculate fare
     */
    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is null or incorrect");
        }

        LocalDateTime inLocalDatTime = DateConvertUtil.convertToLocalDateTimeViaInstant(ticket.getInTime());
        LocalDateTime outLocalDatTime = DateConvertUtil.convertToLocalDateTimeViaInstant(ticket.getOutTime());
        Duration durationBetweenInAndOut = Duration.between(inLocalDatTime, outLocalDatTime);

        //calculate and affect ticket price
        ticket.setPrice(getTicketPrice(ticket.getParkingSpot().getParkingType(), durationBetweenInAndOut ));

        //test if ticket is eligible to discount
        if (isEligibleToDiscount(ticket))
        {
            ticket.setPrice(getDiscountPrice(ticket.getPrice()));
        }

    }



    /**
     * Get price ticket relative to ParkingType and number of hours of parking
     * @param parkingType type of parking
     * @param parkingDuration parking duration representation
     */
    private double getTicketPrice(ParkingType parkingType, Duration parkingDuration)
    {
        if (!isFreeParking(parkingDuration)) {
            double nbHoursParkingDuration = DateConvertUtil.getDecimalHoursFromDuration(parkingDuration);
            switch (parkingType) {
                case CAR:
                    return nbHoursParkingDuration * Fare.CAR_RATE_PER_HOUR;

                case BIKE:
                    return nbHoursParkingDuration * Fare.BIKE_RATE_PER_HOUR;

                default:
                    throw new IllegalArgumentException("Unknown Parking Type");
            }
        }
        else
        {
            return 0.0;
        }
    }

    /**
     * Check if parking is free relative to parking duration in decimal hours
     * @param duration parking duration object
     * @return true if parking can be free, false otherwise
     */
    private boolean isFreeParking(Duration duration)
    {
        return duration.toMinutes()<Fare.NB_MINUTES_BEFORE_PAID_PARKING;
    }

    /**
     * Search and Apply discount
     * @param ticket ticket applying for discount
     * @return true if ticket is eligible to discount
     */
    public boolean isEligibleToDiscount(Ticket ticket)
    {
        if (ticket.getVehicleRegNumber()!=null) {
            List<Ticket> previousPaidTickets = ticketDAO.getPaidTickets(ticket.getVehicleRegNumber());
            return !previousPaidTickets.isEmpty();
        }
        else
        {
            return false;
        }
    }

    /**
     * Calculate Discount Price
     * @param price price on which apply discount
     * @return
     */
    private double getDiscountPrice(double price) {
        return price * (100.0 - Fare.DISCOUNT_PERCENTAGE_FOR_REGULAR_USER) / 100.0;
    }
}