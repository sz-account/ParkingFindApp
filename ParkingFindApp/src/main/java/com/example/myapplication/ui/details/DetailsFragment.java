package com.example.myapplication.ui.details;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import com.example.myapplication.GlobalSettings;
import com.example.myapplication.R;
import com.example.myapplication.markers.Sync;
import com.example.myapplication.parking.Parking;
import com.example.myapplication.parking.ParkingService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DetailsFragment extends Fragment {

    private Long id;
    private ParkingService parkingService;
    private Sync sync;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parkingService = new ParkingService(getActivity().getApplicationContext(), false);
        this.sync = new Sync(this.getActivity().getApplicationContext());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        id = GlobalSettings.selectedId;

        Optional<Parking> byId = parkingService.findById(id);
        
        if(byId.isPresent())
        {
            Parking parking = byId.get();

            // Show price list
            TextView priceList = view.findViewById(R.id.textView_Cennik);
            if(parking.isFree)
                priceList.setText("Bezpłatny");

            if (parking.description != null)
            {
                // Show image
                if(parking.description.image != null) {
                    ImageView imageView = view.findViewById(R.id.imageView);
                    imageView.setImageBitmap(parking.description.image);
                    imageView.setImageBitmap(parking.description.image);
                }

                // Show price list
                if(parking.description.price != null && !parking.isFree)
                    priceList.setText(parking.description.price);
            }
            else
            {
                sync.getDesc(id);
            }

            TextView textName = view.findViewById(R.id.descriptionName);
            textName.setText(parking.name);

            String address = parking.city + ", ul. " + parking.address;

            TextView textAddress = view.findViewById(R.id.descriptionAddress);
            textAddress.setText(address);

            // Show opening hours
            if(parking.hour != null)
            {
                List<TextView> textViewList = new ArrayList<>();
                textViewList.add(view.findViewById(R.id.textViewP) );
                textViewList.add(view.findViewById(R.id.textViewW) );
                textViewList.add(view.findViewById(R.id.textViewS) );
                textViewList.add(view.findViewById(R.id.textViewC) );
                textViewList.add(view.findViewById(R.id.textViewPi) );
                textViewList.add(view.findViewById(R.id.textViewSo) );
                textViewList.add(view.findViewById(R.id.textViewN) );

                for (int i = 0; i < 7; i++) {
                    textViewList.get(i).setText(parking.hour.getOTFromHour(getActivity().getApplicationContext(), i));
                }
            }

            LinearLayout linearLayout = view.findViewById(R.id.linearLayout4);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            Resources r = getActivity().getApplicationContext().getResources();
            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    20,
                    r.getDisplayMetrics()
            );
            layoutParams.setMargins(px,0,0,0);

            if(parking.monitoring)
            {
                TextView tv = new TextView(getActivity().getApplicationContext() );
                tv.setText("Monitoring");
                tv.setLayoutParams(layoutParams);
                tv.setTextAppearance(R.style.TextAppearance_AppCompat_Body1);
                linearLayout.addView(tv);
            }

            if(parking.disabledParkingSpots)
            {
                TextView tv = new TextView(getActivity().getApplicationContext() );
                tv.setText("Miejsca dla niepełnosprawnych");
                tv.setLayoutParams(layoutParams);
                tv.setTextAppearance(R.style.TextAppearance_AppCompat_Body1);
                linearLayout.addView(tv);
            }

        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}