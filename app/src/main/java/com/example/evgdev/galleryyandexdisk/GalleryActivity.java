package com.example.evgdev.galleryyandexdisk;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.evgdev.galleryyandexdisk.AnyClasses.DataCachingClass;
import com.example.evgdev.galleryyandexdisk.AnyClasses.InfoClass;
import com.example.evgdev.galleryyandexdisk.AnyClasses.FragmentHelperClass;
import com.example.evgdev.galleryyandexdisk.Fragments.ImageListFragment;
import com.example.evgdev.galleryyandexdisk.Fragments.LoginFragment;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GalleryActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);

        DataCachingClass.setCacheDir(this);

        setTitle(R.string.activityTitleString);

        if (getIntent() != null && getIntent().getData() != null) {
            getTokenFromIntentData();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = preferences.getString(InfoClass.TOKEN, null);
        //если в настройках отсутствует токен значит это первый вход, тогда сохраняем токен в настройки
        if (token == null) {
            FragmentHelperClass.ShowFragmentFromActivity(getSupportFragmentManager(), new LoginFragment(), InfoClass.LOGIN_FRAGMENT_TAG);
            return;
        }

        if(savedInstanceState == null){
            setRequestedOrientation(getResources().getConfiguration().orientation);
            FragmentHelperClass.ShowFragmentFromActivity(getSupportFragmentManager(), new ImageListFragment(), InfoClass.LIST_FRAGMENT_TAG);
        }

    }


    public void getTokenFromIntentData(){
        Uri data = getIntent().getData();
        setIntent(null);
        //Выбираем токен для подключения к апи из данных интента
        Pattern pattern = Pattern.compile("access_token=(.*?)(&|$)");
        Matcher matcher = pattern.matcher(data.toString());

        if (matcher.find()) {
            final String token = matcher.group(1);
            if (!TextUtils.isEmpty(token)) {
                //сохраняем токен в настройки
                saveToken(token);
            }
        }
    }

    public void saveToken(String token){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(InfoClass.USERNAME, "");
        editor.putString(InfoClass.TOKEN, token);
        editor.apply();
    }

}


