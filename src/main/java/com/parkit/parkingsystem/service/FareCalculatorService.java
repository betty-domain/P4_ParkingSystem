package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.DateConvertUtil;

import java.time.Duration;
import java.time.LocalDateTime;


public class FareCalculatorService {

    /**
     * Calculate Fare for given ticket
     * @param ticket ticket to use to calculate fare
     */
    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is null or incorrect");
        }

        // calculate duration for Current Ticket
        double hoursBetweenInAndOut = calculateHoursBetweenInAndOut(ticket);

        //apply price rules
        ticket.setPrice(getTicketPrice(ticket.getParkingSpot().getParkingType(),hoursBetweenInAndOut));
    }

    /**
     * Calculate duration for current ticket
     * @param ticket where in and out Date will be used
     * @return number of hours with decimal format between in and out date of ticket
     */
    private double calculateHoursBetweenInAndOut(Ticket ticket) {

        LocalDateTime inLocalDatTime = DateConvertUtil.convertToLocalDateTimeViaInstant(ticket.getInTime());
        LocalDateTime outLocalDatTime = DateConvertUtil.convertToLocalDateTimeViaInstant(ticket.getOutTime());
        Duration durationBetweenInAndOut = Duration.between(inLocalDatTime, outLocalDatTime);
        return DateConvertUtil.getDecimalHoursFromDuration(durationBetweenInAndOut);
    }

    /**
     * Get price ticket relative to ParkingType and number of hours of parking
     * @param parkingType type of parking
     * @param hoursOfParking number of hours of parking (in decimal format ex : 1.5 for 1h30 minutes)
     */
    private double getTicketPrice(ParkingType parkingType, double hoursOfParking)
    {
        if (!isFreeParking(hoursOfParking)) {
            switch (parkingType) {
                case CAR:
                    return hoursOfParking * Fare.CAR_RATE_PER_HOUR;

                case BIKE:
                    return hoursOfParking * Fare.BIKE_RATE_PER_HOUR;

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
     * @param hoursOfParking number of hours of parking (in decimal format ex : 1.5 for 1h30 minutes)
     * @return true if parking can be free, false otherwise
     */
    private boolean isFreeParking(double hoursOfParking)
    {
        return hoursOfParking<Fare.NB_HOURS_BEFORE_PAID_PARKING;
    }
}