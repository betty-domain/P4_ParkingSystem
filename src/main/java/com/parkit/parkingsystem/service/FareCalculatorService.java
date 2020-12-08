package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.DateConvertUtil;

import java.time.Duration;
import java.time.LocalDateTime;


public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is null or incorrect");
        }

        // calculate duration for Current Ticket
        double hoursBetweenInAndOut = calculateHoursBetweenInAndOut(ticket);

        setTicketPrice(ticket,hoursBetweenInAndOut);
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
     * Set price ticket with ticket and number of hours of parking
     * @param ticket to complete with price
     * @param hoursOfParking number of hours of parking
     */
    private void setTicketPrice(Ticket ticket, double hoursOfParking)
    {
        if (!isFreeParking(hoursOfParking)) {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice(hoursOfParking * Fare.CAR_RATE_PER_HOUR);
                    break;
                }
                case BIKE: {
                    ticket.setPrice(hoursOfParking * Fare.BIKE_RATE_PER_HOUR);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown Parking Type");
            }
        }
        else
        {
            ticket.setPrice(0.0);
        }
    }

    private boolean isFreeParking(double hoursOfParking)
    {
        return hoursOfParking<0.5;
    }
}