package com.example.myapplication.markers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.example.myapplication.GlobalSettings;
import com.example.myapplication.R;
import com.example.myapplication.parking.Parking;
import com.example.myapplication.parking.ParkingService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MarkersManager implements LocationListener {

    private ParkingService parkingService;
    private GoogleMap googleMap;
    private Activity activity;
    private Marker userMarker, carMarker;
    private List<Marker> markerList = new ArrayList<>();

    public MarkersManager()
    {

    }

    public MarkersManager(Context context, GoogleMap googleMap, Activity activity) {

        this.parkingService = new ParkingService(context, false);
        this.googleMap = googleMap;
        this.activity = activity;

        // Get location
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    static public double calculateDistance( double lat, double lon)
    {
        // degrees to radians.
        double lon2 = Math.toRadians(UserLocation.longitude);
        lon = Math.toRadians(lon);
        double lat2 = Math.toRadians(UserLocation.latitude);
        lat = Math.toRadians(lat);

        // Haversine formula
        double dlon = lon2 - lon;
        double dlat = lat2 - lat;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double r = 6371;

        // calculate the result
        return (c * r);
    }

    public void createMarkers() {

        // Get parking list
        List<Parking> all = parkingService.getAll();

        // Remove old markers
        for (Marker marker: markerList) {
            marker.remove();
        }

        // Add new markers
        if (!all.isEmpty()) {
            for (Parking next : all) {
                if (checkFilter(next ) )
                    markerList.add(
                            addMarker(
                                    next.latitude, next.longitude, next.name, next.id, false) );
            }
        }

        // Add car marker if location exists
        updateCarMarker();
    }

    private Marker addMarker(Double latitude, Double longitude, String name, Long tag, boolean user){
        LatLng position = new LatLng(latitude, longitude);
        Marker marker = googleMap.addMarker(new MarkerOptions().position(position).title(name));
        marker.setTag(tag);
        if(!user)
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.locationparkingicon));
        return marker;
    }

    public void updateCarMarker()
    {
        // Get car location from sharedPref
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);

        Long latitude = sharedPref.getLong("latitude", 500L);
        Long longitude = sharedPref.getLong("longitude", 500L);

        if(latitude != 500)
        {
            //Convert to double
            double lat = Double.longBitsToDouble(latitude);
            double lon = Double.longBitsToDouble(longitude);

            if(carMarker == null)
            {
                String name = "Car";
                Long tag = 0L;
                carMarker = addMarker(lat, lon , name, tag, true);
            }
            else
            {
                carMarker.setPosition(new LatLng(lat, lon));
            }
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

        if(userMarker == null) {
            String name = "User";
            Long tag = 0L;
            userMarker = addMarker(position.latitude, position.longitude , name, tag, true);
            UserLocation.setLonLat(position.latitude, position.longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
        }
        else {
            UserLocation.setLonLat(position.latitude, position.longitude);
            userMarker.setPosition(position);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    public void clearMap()
    {
        // Remove old markers
        for (Marker marker: markerList) {
            marker.remove();
        }

        if(userMarker != null)
            userMarker.remove();

        if(carMarker != null)
            carMarker.remove();
    }

    public boolean checkFilter(Parking parking)
    {
        // Time
        if(GlobalSettings.timeSwitchState)
        {
            if(parking.hour != null) {
                LocalTime[] openingTimes = parking.hour.getActualOTLoclaTime();

                if (openingTimes[0] == LocalTime.of(0, 0)
                        && openingTimes[1] == LocalTime.of(0, 0))
                    return false;
                else if (GlobalSettings.timeA.isBefore(openingTimes[0])
                        || GlobalSettings.timeB.isBefore(openingTimes[0])
                        || GlobalSettings.timeB.isAfter(openingTimes[1]) )
                    return false;
            }
        }

        if(GlobalSettings.disabledParkingSpots && !parking.disabledParkingSpots)
            return false;
        if(GlobalSettings.monitoring && !parking.monitoring)
            return false;
        if(GlobalSettings.isFree && !parking.isFree)
            return false;

        return true;
    }

}
