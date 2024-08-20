package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.ArrayList;

public class Converter {

    @TypeConverter
    public static ArrayList<LocalTime> fromString(String value) {
        Type listType = new TypeToken<ArrayList<LocalTime>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<LocalTime> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static byte[] fromBitmap(Bitmap bitmap) {

        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }
        else
        {
            return new byte[1];
        }
    }
    @TypeConverter
    public static Bitmap fromByteArray(byte[] byteArray) {

        if(byteArray.length > 1) {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
        else
            return null;
    }
}
