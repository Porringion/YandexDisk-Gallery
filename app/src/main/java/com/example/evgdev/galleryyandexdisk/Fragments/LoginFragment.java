package com.example.evgdev.galleryyandexdisk.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.evgdev.galleryyandexdisk.AnyClasses.InfoClass;
import com.example.evgdev.galleryyandexdisk.R;

public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // указываем страницу загрузки
        webView = (WebView) getActivity().findViewById(R.id.authWebView);
        webView.loadUrl(InfoClass.AUTH_URL);

    }

    //Обнуляем созданные view
    private void NullifyView(){
        webView = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        NullifyView();
    }

}
