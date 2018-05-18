package com.example.evgdev.galleryyandexdisk.DaggerClasses.RoomClasses;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

//интерфейс с методами которые используются при работе с бд
@Dao
public interface YandexDiskDataDao {

    //Получаем весь список данных в БД
    @Query("Select * From YandexDiskData")
    List<YandexDiskData> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void Insert(List<YandexDiskData> recordList);

    @Query("Delete From YandexDiskData Where filePath NOT IN (:containsPath)")
    int deleteByNotInList(List<String> containsPath);

}
