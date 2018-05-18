package com.example.evgdev.galleryyandexdisk.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.evgdev.galleryyandexdisk.AnyClasses.DataCachingClass;
import com.example.evgdev.galleryyandexdisk.R;

//Фрагмент настроек
public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    CheckBox checkBox;
    SharedPreferences.Editor preferencesEditor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        preferencesEditor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());


        checkBox = view.findViewById(R.id.checkBoxCacheData);

        checkBox.setChecked(preferences.getBoolean(DataCachingClass.PREFFERENCE_KEY_USE_CACHE, false));

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferencesEditor.putBoolean(DataCachingClass.PREFFERENCE_KEY_USE_CACHE, isChecked);
                preferencesEditor.apply();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
