package com.example.myapplication.markers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.myapplication.GlobalSettings;
import com.example.myapplication.parking.Parking;
import com.example.myapplication.parking.hours.Hour;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalTime;

public class MarkersManagerTest {

    public MarkersManager markersManager;

    @Before
    public void setUp() throws Exception {
        markersManager = new MarkersManager();

        GlobalSettings.isFree = false;
        GlobalSettings.monitoring = false;
        GlobalSettings.timeSwitchState = false;
    }

    @Test
    public void MarkersManager_checkFilter_differentHours()
    {
        Parking parking = mock(Parking.class);
        parking.hour = mock(Hour.class);
        GlobalSettings.timeSwitchState = true;
        GlobalSettings.timeA = LocalTime.of(12,0);
        GlobalSettings.timeB = LocalTime.of(16,0);

        LocalTime[] openingTimes = new LocalTime[] {LocalTime.of(13, 0), LocalTime.of(14, 0)};
        when(parking.hour.getActualOTLoclaTime()).thenReturn(openingTimes);
        assertFalse(markersManager.checkFilter(parking) );

        openingTimes = new LocalTime[] {LocalTime.of(12, 0), LocalTime.of(16, 0)};
        when(parking.hour.getActualOTLoclaTime()).thenReturn(openingTimes);
        assertTrue(markersManager.checkFilter(parking) );

        openingTimes = new LocalTime[] {LocalTime.of(12, 0), LocalTime.of(17, 0)};
        when(parking.hour.getActualOTLoclaTime()).thenReturn(openingTimes);
        assertTrue(markersManager.checkFilter(parking) );

        openingTimes = new LocalTime[] {LocalTime.of(12, 0), LocalTime.of(15, 0)};
        when(parking.hour.getActualOTLoclaTime()).thenReturn(openingTimes);
        assertFalse(markersManager.checkFilter(parking) );

    }

    @Test
    public void MarkersManager_checkFilter_noConditions()
    {
        Parking parking = new Parking();
        assertTrue(markersManager.checkFilter(parking) );
    }

    @Test
    public void MarkersManager_checkFilter_someConditions()
    {
        Parking parking = new Parking();
        GlobalSettings.isFree = true;
        GlobalSettings.monitoring = true;

        assertFalse(markersManager.checkFilter(parking) );

        parking.monitoring = true;
        assertFalse(markersManager.checkFilter(parking) );

        parking.isFree = true;
        assertTrue(markersManager.checkFilter(parking) );
    }

}