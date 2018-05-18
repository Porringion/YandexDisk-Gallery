package com.example.evgdev.galleryyandexdisk.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.evgdev.galleryyandexdisk.AnyClasses.DataCachingClass;
import com.example.evgdev.galleryyandexdisk.AnyClasses.InfoClass;
import com.example.evgdev.galleryyandexdisk.R;
import com.yandex.disk.rest.ProgressListener;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class FullImageFragment extends Fragment {

    public FullImageFragment() {
        // Required empty public constructor
    }

    String currentFullImagePath;
    String fileChecksumm;

    private final String nameParamFullImagePath = "FullImagePath";
    private final String nameParamfileChecksumm = "FileChecksumm";

    private String paramFullImagePath;
    private String paramFileChecksumm;

    public static FullImageFragment newInstance(String fileChecksumm, String fullImagePath) {

        Bundle args = new Bundle();

        FullImageFragment fragment = new FullImageFragment();

        args.putString(fragment.nameParamfileChecksumm, fileChecksumm);
        args.putString(fragment.nameParamFullImagePath, fullImagePath);

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if(getArguments() != null){
            currentFullImagePath = getArguments().getString(nameParamFullImagePath, "");
            fileChecksumm = getArguments().getString(nameParamfileChecksumm, "");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_full_image, container, false);
    }

    ImageView imageView;
    File result;
    ProgressBar progressBar;

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        container = view.findViewById(R.id.fullImageContainer);

        DownloadFullImageByPath(view);
    }

    Bitmap curBitmap;


    private void DownloadFullImageByPath(final View view){

        Observable.just(currentFullImagePath)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, Boolean>() {
                    @Override
                    public Boolean apply(String imagePath) throws Exception {

                        try {

                            result = new File(DataCachingClass.CacheDir, new File( DataCachingClass.PREFIX_FULLIMAGE_NAME + fileChecksumm).getName());

                            //Если такой файл существует то мы считываем его в битмап из файла
                            if (result.exists()) {

                                curBitmap =  DataCachingClass.CheckDecodingBitmap(result, getContext());

                                return curBitmap != null;
                            }

                            //Скачиваем файл с Я.Д. если такого файла у нас нет
                            InfoClass.client.downloadFile(imagePath, result, new ProgressListener() {
                                @Override
                                public void updateProgress(long loaded, long total) {

                                    Log.d("LOADING IMAGE", "updateProgress: " + String.valueOf(loaded) + " " + String.valueOf(total));
                                }

                                @Override
                                public boolean hasCancelled() {
                                    return false;
                                }
                            });

                            curBitmap = DataCachingClass.CheckDecodingBitmap(result, getContext());

                            return curBitmap != null;

                        }
                        catch (Exception e){
                            return false;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean flag) throws Exception {

                        InitViews(view);
                        progressBar.setVisibility(View.INVISIBLE);

                        if(!flag)
                            return;

                        imageView.setImageBitmap(curBitmap);

                        if(!DataCachingClass.getPreferenceUseCache(getContext()))
                            result.delete();
                    }
                })
                .subscribe();
    }

    private void InitViews(View view){

        progressBar = view.findViewById(R.id.FullImageProgressBar);
        imageView = view.findViewById(R.id.FullImageView);
    }


//    String keyImagePath = "ImagePath";
//    String keyMD5 = "MD5";
//
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        outState.putString(keyImagePath, currentFullImagePath);
//        outState.putString(keyMD5, fileChecksumm);
//    }

    private void NullableViews(){
        imageView = null;
        progressBar = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        NullableViews();
    }
}
