package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.util.DateConvertUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;


@RunWith(PowerMockRunner.class)
public class FareCalculatorServicePowerMockTest {

    @Test
    @PrepareForTest(ParkingType.class)
    public void calculateFareUnknownType() throws Exception {

        ParkingType parkingTypeUnknown = PowerMockito.mock(ParkingType.class);
        Whitebox.setInternalState(parkingTypeUnknown, "name", "Unknown");
        Whitebox.setInternalState(parkingTypeUnknown, "ordinal", ParkingType.values().length);

        PowerMockito.mockStatic(ParkingType.class);
        PowerMockito.when(ParkingType.values()).thenReturn(new ParkingType[]{ParkingType.CAR, ParkingType.BIKE, parkingTypeUnknown});

        TicketDAO ticketDAO = new TicketDAO();
        FareCalculatorService fareCalculatorService = new  FareCalculatorService(ticketDAO);

        LocalDateTime inDateTime = LocalDateTime.of(2020,1,1,12,0,0);
        LocalDateTime outDateTime = inDateTime.plusHours(1);

        ParkingSpot parkingSpot = new ParkingSpot(1, parkingTypeUnknown,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(DateConvertUtil.convertToDate(inDateTime));
        ticket.setOutTime(DateConvertUtil.convertToDate(outDateTime));
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));

    }
}
