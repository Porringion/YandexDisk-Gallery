package com.example.evgdev.galleryyandexdisk.AnyClasses;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

//класс помощник для работы с фрагментами
public class FragmentHelperClass {

    public static void ShowFragmentFromFragment(android.support.v4.app.Fragment fragment, FragmentActivity activity, boolean addToBackstack){

        if(addToBackstack) {
            activity
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .addToBackStack(null)
                    .commit();
        }
        else {
            activity
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .commit();
            }
    }

    public static void ShowFragmentFromActivity(FragmentManager fragmentManager, Fragment fragment, String tag){
        fragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment, tag)
                .commit();
    }

}
