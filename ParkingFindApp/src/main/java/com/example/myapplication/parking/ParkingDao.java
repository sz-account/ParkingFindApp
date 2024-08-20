package com.example.myapplication.parking;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Optional;

@Dao
public interface ParkingDao {

    @Query("SELECT * FROM parking")
    List<Parking> getAll();

    @Query("SELECT * FROM parking WHERE id==:id")
    Optional<Parking> findById(Long id);

    @Insert
    void insert(Parking parking);

    @Insert
    void insertAll(List<Parking> parkingList);

    @Query("DELETE FROM parking")
    void deleteAll();

    @Update
    int updateParking(Parking parking);
}
