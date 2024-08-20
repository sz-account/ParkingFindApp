package com.example.myapplication.parking;

import android.content.Context;
import com.example.myapplication.AppDatabase;
import java.util.List;
import java.util.Optional;

public class ParkingService {

    AppDatabase db;
    ParkingDao parkingDao;

    public ParkingService(Context context) {
        db = AppDatabase.getInstance(context);
        parkingDao = db.parkingDao();
        parkingDao.deleteAll();
    }

    public ParkingService(Context context, boolean deleteAll)
    {
        db = AppDatabase.getInstance(context);
        parkingDao = db.parkingDao();

        if (deleteAll)
            parkingDao.deleteAll();
    }

    public List<Parking> getAll()
    {
        return parkingDao.getAll();
    }

    public void insertList(List<Parking> parkingList)
    {
        parkingDao.insertAll(parkingList);
    }

    public void save(Parking parking)
    {
        parkingDao.insert(parking);
    }

    public void updateParking(Parking parking)
    {
        parkingDao.updateParking(parking);
    }

    public Optional<Parking> findById(Long id)
    {
        return parkingDao.findById(id);
    }

    public void deleteAll()
    {
        parkingDao.deleteAll();
    }
}
