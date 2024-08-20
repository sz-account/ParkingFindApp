package com.example.myapplication;

import java.time.LocalTime;

public class GlobalSettings {

    public static boolean timeSwitchState = false;
    public static boolean disabledParkingSpots = false;
    public static boolean monitoring = false;
    public static boolean isFree = false;

    public static LocalTime timeA = LocalTime.of(12,0);
    public static LocalTime timeB = LocalTime.of(20,0);

    public static Long selectedId;
}
