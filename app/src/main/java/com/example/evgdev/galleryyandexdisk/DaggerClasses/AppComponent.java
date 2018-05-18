package com.example.evgdev.galleryyandexdisk.DaggerClasses;


import com.example.evgdev.galleryyandexdisk.DaggerClasses.RoomClasses.RoomModule;
import com.example.evgdev.galleryyandexdisk.Fragments.ImageListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {RoomModule.class})
@Singleton
public interface AppComponent {
//с помощью этого метода я перемещаю инициализированные значения в фрагмент
    void inject(ImageListFragment imageListFragment);
}
