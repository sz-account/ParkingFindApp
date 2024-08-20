package com.example.myapplication.parking.hours;

import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;

import com.example.myapplication.R;
import com.example.myapplication.parking.Parking;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Hour {

    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    public Long hourId;

    @ColumnInfo(name = "localTimes")
    public ArrayList<LocalTime> localTimeList;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalTime[] getActualOTLoclaTime()
    {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        //Konwersja na poniedziałek jako 0
        day -= 2;
        if (day == -1)
            day = 6;

        int currentDay = day * 2;
        LocalTime[] openingTimes = new LocalTime[] {localTimeList.get(currentDay), localTimeList.get(currentDay+1)};

        return openingTimes;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getActualOT(Context context)
    {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        //Konwersja na poniedziałek jako 0
        day -= 2;
        if (day == -1)
            day = 6;

        return getOTFromHour(context, day);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getOTFromHour(Context context, int day) {

        int currentDay = day * 2;

        LocalTime dayStart = localTimeList.get(currentDay);
        LocalTime dayEnd = localTimeList.get(currentDay+1);

        LocalTime localTime = LocalTime.parse("00:00");
        LocalTime localTime2 = LocalTime.parse("23:59");

        if (dayStart.equals(localTime) && dayEnd.equals(localTime))
            return "Zamknięte";
        else if (dayStart.equals(localTime) && dayEnd.equals(localTime2) )
            return String.valueOf(context.getString(R.string.czynneCałąDobę) );
        else
            return dayStart + " - " + dayEnd;
    }
}
