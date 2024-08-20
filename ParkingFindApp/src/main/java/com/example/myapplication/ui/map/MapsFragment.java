package com.example.myapplication.ui.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.example.myapplication.FilterDialogFragment;
import com.example.myapplication.R;
import com.example.myapplication.markers.MarkersManager;
import com.example.myapplication.markers.Sync;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends Fragment implements GoogleMap.OnCameraIdleListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener, TextWatcher {

    private MarkersManager markersManager;
    private Sync sync;
    private Context context;
    private GoogleMap googleMap;
    private Button button, filterButton, carButton;
    private EditText searchBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("FRAGMENT", "MapsFragment CREATED!");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        this.googleMap = googleMap;
        this.context = getActivity().getApplicationContext();

        if(this.markersManager == null) {
            this.markersManager = new MarkersManager(context, googleMap, getActivity() );
            markersManager.createMarkers();
        }

        if(this.sync == null) {
            this.sync = new Sync(this.context,this.markersManager, getActivity() ,false);
        }

        this.googleMap.setOnCameraIdleListener(this);
        this.googleMap.setOnMarkerClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        button = view.findViewById(R.id.findCloseButton);
        carButton = view.findViewById(R.id.button_car);
        filterButton = view.findViewById(R.id.FilterButton);
        searchBar = view.findViewById(R.id.searchText);
        searchBar.addTextChangedListener(this);
        button.setOnClickListener(this);
        filterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FilterDialogFragment filterDialogFragment = new FilterDialogFragment(markersManager);
                filterDialogFragment.show(getChildFragmentManager(), "Filter");
            }
        });

        carButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                CameraPosition cameraPosition = googleMap.getCameraPosition();
                LatLng target = cameraPosition.target;
                editor.putLong("latitude", Double.doubleToRawLongBits(target.latitude) );
                editor.putLong("longitude", Double.doubleToRawLongBits(target.longitude) );
                editor.apply();

                markersManager.updateCarMarker();
            }
        });
    }

    @Override
    public void onCameraIdle() {

        button.setVisibility(View.VISIBLE); // Schowaj przycisk
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Long id = (Long) marker.getTag();
        Log.d("TAGS", "Marker id: " + id.toString());

        if(id != 0L)
        {
            Bundle bundle = new Bundle();
            bundle.putLong("id", id);

            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ShowInfoFragment.class, bundle)
                    .setReorderingAllowed(true)
                    .addToBackStack("name") // name can be null
                    .commit();
            return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        CameraPosition cameraPosition = googleMap.getCameraPosition();
        double latitude = cameraPosition.target.latitude;
        double longitude = cameraPosition.target.longitude;

        sync.getInRadius(latitude, longitude); // Pobierz dostępne parkinig na danym obasarze

        button.setVisibility(View.INVISIBLE); // Schowaj przycisk


    }

    private void findLocation(String adderess)
    {
        Geocoder geoCoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
        try
        {
            List<Address> addresses = geoCoder.getFromLocationName(adderess, 1);
            if (addresses.size() > 0)
            {
                Double lat = (double) (addresses.get(0).getLatitude());
                Double lon = (double) (addresses.get(0).getLongitude());

                Log.d("lat-long", "" + lat + "......." + lon);
                final LatLng user = new LatLng(lat, lon);

                // Move the camera instantly to hamburg with a zoom of 15.
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 15));

                // Zoom in, animating the camera.
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //Search Bar
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
            findLocation(s.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        markersManager.clearMap();
    }
}