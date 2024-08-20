package com.example.myapplication.ui.map;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.myapplication.GlobalSettings;
import com.example.myapplication.R;
import com.example.myapplication.parking.Parking;
import com.example.myapplication.parking.ParkingService;
import java.util.Optional;

public class ShowInfoFragment extends Fragment implements View.OnClickListener {

    private Long id;
    private TextView textName, textAddress, textTime, textDistance;
    private Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            this.id = bundle.getLong("id");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_info, container, false);

        textName = view.findViewById(R.id.descriptionName);
        textAddress = view.findViewById(R.id.textAddress);
        textTime = view.findViewById(R.id.textTime);
        textDistance = view.findViewById(R.id.textDistance);

        button = view.findViewById(R.id.button);
        button.setOnClickListener(this);

        updateInfo();

        return view;
    }

    private void updateInfo(){

        ParkingService parkingService = new ParkingService(getActivity().getApplicationContext(), false);
        Optional<Parking> byId = parkingService.findById(this.id);

        if(byId.isPresent())
        {
            Parking parking = byId.get();

            textName.setText(parking.name);
            String str = parking.city + ", ul. " + parking.address;
            textAddress.setText(str);
            if(parking.hour != null)
                textTime.setText(parking.hour.getActualOT(getActivity().getApplicationContext() ) );
            str = Math.round((parking.getDistanceToUser()*100.0 ) )/100.0 + " Km";
            textDistance.setText(str);

            if(parking.slots != null)
                button.setText(parking.slots.toString() );
        }
    }

    @Override
    public void onClick(View v) {

        if (id != null)
        {
            GlobalSettings.selectedId = id;
        }
        Navigation.findNavController(v).navigate(R.id.action_map_to_navigation_notifications2);
    }
}