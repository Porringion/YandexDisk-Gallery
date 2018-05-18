package com.example.evgdev.galleryyandexdisk;

import android.app.Application;

import com.example.evgdev.galleryyandexdisk.DaggerClasses.AppComponent;
import com.example.evgdev.galleryyandexdisk.DaggerClasses.DaggerAppComponent;
import com.example.evgdev.galleryyandexdisk.DaggerClasses.RoomClasses.RoomModule;

public class App extends Application {

    public static int DB_VERSION;

    private static AppComponent component;

    public static AppComponent getComponent(){
        return component;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        component = buildComponent();
    }

    protected AppComponent buildComponent(){

        return DaggerAppComponent.builder()
                .roomModule(new RoomModule(this))
                .build();
    }

}
