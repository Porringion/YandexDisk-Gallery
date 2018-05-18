package com.example.evgdev.galleryyandexdisk.AnyClasses;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.yandex.disk.rest.OkHttpClientFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

//класс для работы с интернет соединением
public class InternetSettingsClass {

    //OkHTTP
    public static OkHttpClient okHttpClient = new OkHttpClient();

    //Возможные заголовки GET запросов
    public static final String AUTHORIZATION_HEADER = "Authorization"; //Пример заголовка в гет запросе (AUTHORIZATION_HEADER, "OAuth " + getToken()));
    public static final String AUTHORIZATION_HEADER_VALUE_PREFIX = "OAuth ";
    public static final String CACHECONTROL_HEADER = "Cache-control"; // Пример ("Cache-control", "public")
    public static final String CONTENT_TYPE_HEADER = "Content-Type"; //Пример ("Content-Type", "image/jpeg")
    public static final String CONTENT_TRANSFER_ENCODING_HEADER = "binary"; // Пример ("Content-Transfer-Encoding", "binary")
    public static final String PRAGMA_HEADER = "Pragma"; //Пример addHeader("Pragma", "public")

    public static final String MEDIA_TYPE_IMAGE = "image";

//метод подготавливает гет запрос для получения картинки preview
    public static Request getRequestByURL(String url){

        return  new Request.Builder()
                .url(url)
                .get()
                .addHeader(CONTENT_TYPE_HEADER, "image/jpeg")
                .addHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_VALUE_PREFIX + InfoClass.credentials.getToken())
                .build();
    }

    //метод проверки подключения к интеренету
    public static boolean CheckInternetConnection(Context context){

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm == null)
            return false;

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        // проверка подключения
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                // тест доступности внешнего ресурса
                URL url = new URL("https://www.google.com/");
                HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
                urlc.setConnectTimeout(1000);
                urlc.connect();

                // статус ресурса OK
                if (urlc.getResponseCode() == 200)
                    return true;

                // иначе проверка провалилась
                return false;

            } catch (IOException e) {
                Log.d("my_tag", "Ошибка подключения к интернету", e);
                return false;
            }
        }

        return false;
    }
}
