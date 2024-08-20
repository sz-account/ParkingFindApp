package com.example.myapplication.ui.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.GlobalSettings;
import com.example.myapplication.R;
import com.example.myapplication.parking.Parking;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>{

    private Context context;
    private List<Parking> parkingList;

    public RecyclerViewAdapter(Context context, List<Parking> parkingList) {
        this.context = context;
        this.parkingList = parkingList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_show_info, parent, false);
        return new RecyclerViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Parking parking = parkingList.get(position);
        String address = parking.city + ", ul. " + parking.address;
        holder.textName.setText( parking.name );
        holder.textAddress.setText(address);
        String distance = Math.round((parking.getDistanceToUser() * 100.0)) / 100.0 + " Km";
        holder.textDistance.setText(distance);
        if(parking.hour != null)
            holder.textHours.setText(parking.hour.getActualOT(context) );

        holder.button.setText(parking.slots.toString() );
        holder.button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                GlobalSettings.selectedId = parking.id;
                Navigation.findNavController(v).navigate(R.id.action_map_to_navigation_notifications2);
            }
        });
    }

    @Override
    public int getItemCount() {
        return parkingList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView textName, textAddress, textDistance, textHours;
        Button button;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.descriptionName);
            textAddress = itemView.findViewById(R.id.textAddress);
            textDistance = itemView.findViewById(R.id.textDistance);
            button = itemView.findViewById(R.id.button);
            textHours = itemView.findViewById(R.id.textTime);
        }

    }
}
