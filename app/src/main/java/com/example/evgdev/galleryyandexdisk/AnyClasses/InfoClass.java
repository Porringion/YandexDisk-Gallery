package com.example.evgdev.galleryyandexdisk.AnyClasses;

import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.RestClient;

//Класс с общей информацией
public class InfoClass {

    public static final String CLIENT_ID = "82e97a6ea25547478f7824c572e7c625";
    public static final String AUTH_URL = "https://oauth.yandex.ru/authorize?response_type=token&client_id="+CLIENT_ID;
    public static final String USERNAME = "example.username";
    public static final String TOKEN = "example.token";

    public static final String LOGIN_FRAGMENT_TAG = "LoginFragment";
    public static final String LIST_FRAGMENT_TAG = "ImageListFragment";
    public static final String EXCEPTION_DOWNLOAD_RESLIST_TAG = "not received resources";

    public static int currentIndexImage = -1;

    public static Credentials credentials;
    public static RestClient client;
}
