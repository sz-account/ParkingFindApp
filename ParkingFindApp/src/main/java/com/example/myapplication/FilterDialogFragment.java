package com.example.myapplication;

import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;
import com.example.myapplication.markers.MarkersManager;
import java.time.LocalTime;
import java.util.Locale;

public class FilterDialogFragment extends DialogFragment {

    private int[] timeArray = new int[4];
    private Button[] buttons= new Button[3];
    private Switch aSwitch;
    private MarkersManager markersManager;
    private CheckBox checkBoxMonitoring, checkBoxSlots, checkBoxIsFree;

    public FilterDialogFragment(MarkersManager markersManager) {
        this.markersManager = markersManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_dialog, container, false);

        checkBoxMonitoring = view.findViewById(R.id.checkBox_monitoring);
        checkBoxSlots = view.findViewById(R.id.checkBox_mdn);
        checkBoxIsFree = view.findViewById(R.id.checkBox_isFree);

        aSwitch = view.findViewById(R.id.switch1);
        buttons[0] = view.findViewById(R.id.timeButton1);
        buttons[2] = view.findViewById(R.id.timeButton2);

        buttons[0].setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createTimePicker(0);

            }
        });
        buttons[2].setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createTimePicker(2);

            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    buttons[0].setEnabled(true);
                    buttons[2].setEnabled(true);
                    GlobalSettings.timeSwitchState = true;

                }
                else
                {
                    buttons[0].setEnabled(false);
                    buttons[2].setEnabled(false);
                    GlobalSettings.timeSwitchState = false;
                }
            }
        });

        checkBoxMonitoring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                GlobalSettings.monitoring = isChecked;
            }
        });

        checkBoxIsFree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                GlobalSettings.isFree = isChecked;
            }
        });

        checkBoxSlots.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                GlobalSettings.disabledParkingSpots = isChecked;
            }
        });

        setSetting();

        return view;
    }

    private void setSetting()
    {
        buttons[0].setText(GlobalSettings.timeA.toString());
        buttons[2].setText(GlobalSettings.timeB.toString());

        if(GlobalSettings.timeSwitchState)
        {
            aSwitch.setChecked(true);
            buttons[0].setEnabled(true);
            buttons[2].setEnabled(true);
        }
        else
        {
            buttons[0].setEnabled(false);
            buttons[2].setEnabled(false);
        }

        checkBoxSlots.setChecked(GlobalSettings.disabledParkingSpots);
        checkBoxMonitoring.setChecked(GlobalSettings.monitoring);
        checkBoxIsFree.setChecked(GlobalSettings.isFree);
    }


    private void createTimePicker(int i)
    {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeArray[i] = hourOfDay;
                timeArray[i+1] = minute;
                buttons[i].setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                if (i == 0)
                {
                    GlobalSettings.timeA = LocalTime.of(hourOfDay,minute );
                }
                else
                {
                    GlobalSettings.timeB = LocalTime.of(hourOfDay,minute );
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), onTimeSetListener, timeArray[i], timeArray[i+1], true);
        timePickerDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        markersManager.createMarkers();
    }
}