package com.example.myapplication.parking;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.myapplication.markers.MarkersManager;
import com.example.myapplication.parking.desc.Description;
import com.example.myapplication.parking.hours.Hour;
import com.google.gson.annotations.SerializedName;

import kotlin.jvm.Transient;

@Entity
public class Parking implements Comparable<Parking>{

    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "slots")
    public Long slots;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "city")
    public String city;

    @ColumnInfo(name = "disabledParkingSpots")
    public boolean disabledParkingSpots;

    @ColumnInfo(name = "monitoring")
    public boolean monitoring;

    @ColumnInfo(name = "isFree")
    public boolean isFree;

    @SerializedName("hours")
    @Embedded
    public Hour hour;

    @SerializedName("description")
    @Embedded
    public Description description;

    @Transient
    private double distanceToUser;

    public double getDistanceToUser() {
        return MarkersManager.calculateDistance(latitude, longitude);
    }

    public void setDistanceToUser(double distanceToUser) {
    }

    @Override
    public int compareTo(Parking o) {
        double distanceToUser = getDistanceToUser();
        double distanceToUserO = o.getDistanceToUser();

        if (distanceToUser == distanceToUserO )
            return 0;
        else if(distanceToUser < distanceToUserO)
            return -1;
        else
            return 1;
    }
}
