package com.example.myapplication.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.parking.Parking;
import com.example.myapplication.parking.ParkingService;
import java.util.Collections;
import java.util.List;

public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ParkingService parkingService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parkingService = new ParkingService(getActivity().getApplicationContext(),false);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        List<Parking> all = parkingService.getAll();
        Collections.sort(all);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getActivity().getApplicationContext(), all);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        return view;
    }
}