package com.example.myapplication;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;

public class ConverterTest {

    @Before
    public void setUp()
    {
    }

    @Test
    public void converter_CorrectDataConvert_fromArrayListToString()
    {
        ArrayList<LocalTime> localTimeArrayList;
        String s = null;

        s = Converter.fromArrayList(null);
        assertTrue(s.equals("null") );

        localTimeArrayList = new ArrayList<LocalTime>();
        s = Converter.fromArrayList(localTimeArrayList);
        assertTrue(s.equals("[]") );

        localTimeArrayList.add(LocalTime.of(23,30,10));
        s = Converter.fromArrayList(localTimeArrayList);
        assertTrue(s.equals("[{\"hour\":23,\"minute\":30,\"second\":10,\"nano\":0}]") );
    }

    @Test
    public void converter_CorrectDataConvert_fromStringToArrayList()
    {
        ArrayList<LocalTime> localTimeArrayList = null;
        String s = null;

        localTimeArrayList = Converter.fromString(null);
        assertTrue(localTimeArrayList == null);

        s = "[{\"hour\":23,\"minute\":30,\"second\":10,\"nano\":0}]";
        localTimeArrayList = Converter.fromString(s);

        assertTrue(localTimeArrayList.get(0).equals(LocalTime.of(23,30,10)) );
    }
}