package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

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

        FareCalculatorService fareCalculatorService = new  FareCalculatorService();

        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();

        ParkingSpot parkingSpot = new ParkingSpot(1, parkingTypeUnknown,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));

    }
}
