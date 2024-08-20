package com.example.myapplication.parking.desc;

import android.graphics.Bitmap;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

public class Description {

    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    public Long descId;

    @ColumnInfo(name = "description")
    public String des;

    @ColumnInfo(name = "price")
    public String price;

    @ColumnInfo(name = "image")
    public Bitmap image;
}
