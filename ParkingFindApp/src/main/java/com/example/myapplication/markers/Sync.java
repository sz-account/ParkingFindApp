package com.example.myapplication.markers;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.myapplication.Constants;
import com.example.myapplication.parking.Parking;
import com.example.myapplication.parking.ParkingService;
import com.example.myapplication.parking.desc.Description;
import com.example.myapplication.Converter;
import com.example.myapplication.parking.hours.Hour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Sync {

    private ParkingService parkingService;
    private Context context;
    private MarkersManager markersManager;
    private FragmentActivity activity;
    private final String ip = Constants.EMUIP;


    public Sync(Context context, MarkersManager markersManager, FragmentActivity activity, boolean deleteAll) {
        this.activity = activity;
        this.parkingService = new ParkingService(context, deleteAll);
        this.markersManager = markersManager;
        this.context = context;
    }

    public Sync(Context context) {

        this.parkingService = new ParkingService(context, false);
        this.context = context;
    }

    public void getInRadius(double latitude, double longitude)
    {

        OkHttpClient client = new OkHttpClient();
        String url = ip + "/parking";

        RequestBody formBody = new FormBody.Builder()
                .add("lat", String.valueOf(latitude) )
                .add("lon", String.valueOf(longitude) )
                .build();

        Request request = new Request.Builder()
                .url(url)
                .method("POST", formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                Log.d("NETWORK", "didn't work :<");
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Brak połączenia z serwerem", Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("NETWORK", "worked :>");

                String string = response.body().string();
                List<Parking> parkingList = new ArrayList<>();

                try {
                    JSONArray jsonArray = new JSONArray(string);

                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, "Znaleziono: " + jsonArray.length(), Toast.LENGTH_LONG).show();
                        }
                    });

                    if(jsonArray.length() > 0)
                    {
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Parking parking = new Parking();
                            parking.id = jsonObject.getLong("id");
                            parking.name = jsonObject.getString("name");
                            parking.city = jsonObject.getString("city");
                            parking.address = jsonObject.getString("address");
                            parking.latitude = jsonObject.getDouble("latitude");
                            parking.longitude = jsonObject.getDouble("longitude");
                            parking.longitude = jsonObject.getDouble("longitude");
                            parking.disabledParkingSpots = jsonObject.getBoolean("disabledParkingSpots");
                            parking.monitoring = jsonObject.getBoolean("monitoring");
                            parking.slots = jsonObject.getLong("slots");
                            parking.isFree = jsonObject.getBoolean("free");

                            if (jsonObject.has("hours") && !jsonObject.isNull("hours") ) {

                                JSONObject jsonObject2 = jsonObject.getJSONObject("hours");
                                Log.d("JSON", String.valueOf(jsonObject2.getLong("id")));
                                JSONArray jsonArray2 = jsonObject2.getJSONArray("hoursList");

                                ArrayList<LocalTime> localTimeList = new ArrayList<LocalTime>();

                                for(int j = 0; j < jsonArray2.length(); j++)
                                {
                                    localTimeList.add(LocalTime.parse(jsonArray2.getString(j) ) );
                                }

                                Hour hour = new Hour();
                                hour.hourId = jsonObject2.getLong("id");
                                hour.localTimeList = localTimeList;

                                parking.hour = hour;
                            }

                            parkingList.add(parking);
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                parkingService.deleteAll();
                parkingService.insertList(parkingList);

                activity.runOnUiThread(new Runnable(){
                    public void run(){

                        markersManager.createMarkers();
                    }
                });
            }
        });
    }

    public void getDesc(Long id)
    {
        Optional<Parking> byId = parkingService.findById(id);
        if(byId.isPresent()) {

            OkHttpClient client = new OkHttpClient();
            String url = ip + "/parking/desc";

            RequestBody formBody = new FormBody.Builder()
                    .add("id", String.valueOf(id))
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", formBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    String string = response.body().string();
                    List<Parking> parkingList = new ArrayList<>();


                    try {
                        JSONObject jsonObject = new JSONObject(string);

                        Parking parking = byId.get();


                        Description description = new Description();
                        description.descId = jsonObject.getLong("id");
                        description.des = jsonObject.getString("des");
                        description.price = jsonObject.getString("price");
                        String image = jsonObject.getString("image");
                        if(image != null)
                            description.image = Converter.fromByteArray(Base64.decode(image, Base64.DEFAULT ) );

                        parking.description = description;

                        parkingService.updateParking(parking);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }
}
