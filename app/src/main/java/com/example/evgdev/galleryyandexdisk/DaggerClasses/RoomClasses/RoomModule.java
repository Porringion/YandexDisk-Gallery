package com.example.evgdev.galleryyandexdisk.DaggerClasses.RoomClasses;

import android.arch.persistence.room.Room;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

//модуль для инициализации в даггер чтобы не создавать все это в классе Application
@Module
public class RoomModule {

    private Context mContext;

    public RoomModule(Context context){
        this.mContext = context;
    }

    @Provides
    @Singleton
    public AppDataBase provideAppDataBase(){
        return Room.databaseBuilder(
                mContext,
                AppDataBase.class,
                "yandex_disk_data")
                .build();
    }

}
