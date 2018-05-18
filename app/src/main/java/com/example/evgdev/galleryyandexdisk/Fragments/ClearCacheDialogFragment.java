package com.example.evgdev.galleryyandexdisk.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.evgdev.galleryyandexdisk.AnyClasses.DataCachingClass;
import com.example.evgdev.galleryyandexdisk.R;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ClearCacheDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //устанавливаем макет фрагмента
        View v = inflater.inflate(R.layout.dialog_clear_cache, null);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //устанавливаем заголовок и запрещаем закрытие фрагмента при клике в не диалога
        getDialog().setTitle(R.string.clearCacheDialogTitle);
        getDialog().setCanceledOnTouchOutside(false);

        Observable.just("")
                .subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                        try {

                            //очищаем папки с кэшем
                            DataCachingClass.deleteFilesFromCacheDir(getActivity().getExternalCacheDir());
                            DataCachingClass.deleteFilesFromCacheDir(getActivity().getCacheDir());
                            DataCachingClass.deleteFilesFromCacheDir(getActivity().getFilesDir());

                        }catch (Exception e){
                            Log.d("ClearCache", "accept: ", e);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //по окончанию процесса очистки кэша мы разрешаем поворот экрана и закрываем диалог
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        getDialog().cancel();
                    }
                })
                .subscribe();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Запрещаем поворачивать экрана т.к. при повороте закрывается диалог
        getActivity().setRequestedOrientation(getActivity().getResources().getConfiguration().orientation);

        return super.onCreateDialog(savedInstanceState);
    }
}
