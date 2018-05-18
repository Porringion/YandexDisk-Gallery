package com.example.evgdev.galleryyandexdisk.Fragments;

import android.arch.persistence.room.RoomDatabase;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.example.evgdev.galleryyandexdisk.Adapters.ImageListAdapter;
import com.example.evgdev.galleryyandexdisk.AnyClasses.DataCachingClass;
import com.example.evgdev.galleryyandexdisk.AnyClasses.FragmentHelperClass;
import com.example.evgdev.galleryyandexdisk.AnyClasses.InfoClass;
import com.example.evgdev.galleryyandexdisk.AnyClasses.InternetSettingsClass;
import com.example.evgdev.galleryyandexdisk.App;
import com.example.evgdev.galleryyandexdisk.DaggerClasses.RoomClasses.AppDataBase;
import com.example.evgdev.galleryyandexdisk.DaggerClasses.RoomClasses.YandexDiskData;
import com.example.evgdev.galleryyandexdisk.R;
import com.example.evgdev.galleryyandexdisk.RestClientUtil;
import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.ResourcesHandler;
import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.json.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ImageListFragment extends Fragment {


    public ImageListFragment() {
        super();
    }

    GridView gridView;
    boolean isDownload = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        App.getComponent().inject(this);

        InfoClass.credentials = getCredentials();
        InfoClass.client = RestClientUtil.getInstance(InfoClass.credentials);
    }

    private boolean getDataFromDB(){

        if(dataBase == null)
            return false;

        dataList = dataBase.YandexDiskDataDao().getAll();

        return dataList.size() > 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_list, container, false);
    }

    int imageWidth, imageHeight;

    private void setPreviewImageSize(){
        //Размер превью изображения
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        imageWidth = displayMetrics.widthPixels / 2 -5;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            imageHeight = displayMetrics.heightPixels / 2;
        else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            imageHeight = displayMetrics.heightPixels / 5;
        }
    }

    List<YandexDiskData> dataList = new ArrayList<>();
    @Inject
    AppDataBase dataBase;

    SwipeRefreshLayout container;
    ImageListAdapter adapter;

//Инициилизируем view
    private void InitViews(View view){

        container = view.findViewById(R.id.previewListContainer);

        //JОбработчик обновления layout
        container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Блокируем gridview во время обновления
                gridView.setEnabled(false);

                Observable.just("")
                        .observeOn(Schedulers.io())
                        .map(new Function<String, Boolean>() {
                            @Override
                            public Boolean apply(String s) throws Exception {

//                        Проверяем подключение к интернету с помощью подключения к сайту google.com
                                if(getContext() != null && !InternetSettingsClass.CheckInternetConnection(getContext())) {

                                    //Если подключения к интернету нет пытаемся считать записи из базы
                                    if(dataBase != null){
                                        dataList.clear();
                                        dataList.addAll(dataBase.YandexDiskDataDao().getAll());
                                        return true;
                                    }
                                }

                                return DownloadDataFromYandexDisk();
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {

                            //обновляем gridview если данные были считаны
                                if(aBoolean){

                                    if(!adapter.getList().equals(dataList))
                                        adapter.setList(dataList);

                                    adapter.notifyDataSetChanged();
                                    adapter.notifyDataSetInvalidated();
                                    gridView.invalidateViews();
                                    container.setRefreshing(false);
                                    gridView.setEnabled(true);

                                }
                                else
                                    container.setRefreshing(false);
                            }
                        })
                        .subscribe();
            }
        });


        if(adapter == null)
            adapter = new ImageListAdapter(getContext(), dataList, imageHeight, imageWidth);
        else {
            adapter.setImageHeight(imageHeight);
            adapter.setImageWidth(imageWidth);
        }

        if(gridView == null ){

            gridView = view.findViewById(R.id.grid_view);
            gridView.setColumnWidth(imageWidth);
            gridView.setAdapter(adapter);
            gridView.refreshDrawableState();

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String fullImagePath = (String) view.getTag(DataCachingClass.IMAGE_PATH_TAG);
                    String fileChecksumm = (String) view.getTag(DataCachingClass.MD5_TAG);

                    if(fullImagePath != null) {

                        InfoClass.currentIndexImage = gridView.getFirstVisiblePosition();

                        FullImageFragment fullImageFragment = FullImageFragment.newInstance(fileChecksumm, fullImagePath);

                        FragmentHelperClass.ShowFragmentFromFragment(fullImageFragment, getActivity(), true);

                    }
                }
            });
        }
        else {
            gridView.setColumnWidth(imageWidth);
            gridView.refreshDrawableState();
        }
    }


    //Собираем пути файлов в лист чтобы удалить не существующие пути
    private List<String> getExistingFilePath(){

        List<String> existingFilePath = new ArrayList<>();

        for (YandexDiskData item:dataList) {
            existingFilePath.add(item.filePath);
        }

        return existingFilePath;
    }

    private boolean DownloadDataFromYandexDisk(){

        try {
            //Получаем данные о картинках на диске
            dataList.clear();

            InfoClass.client.getFlatResourceList(new ResourcesArgs.Builder()
                    .setMediaType("image")
                    .setLimit(2147000)
                    .setParsingHandler(new ResourcesHandler() {
                        @Override
                        public void handleItem(Resource item) {

                            //Заполняем данными лист
                            YandexDiskData record = YandexDiskData.ConvertFromResource(item);
                            dataList.add(record);
                        }
                    })
                    .build());

            dataBase.YandexDiskDataDao().Insert(dataList);

            //удаляем из таблицы отсутствующие строки
            dataBase.YandexDiskDataDao().deleteByNotInList(getExistingFilePath());

            return true;
        } catch (IOException | ServerException ex) {
            Log.e(InfoClass.EXCEPTION_DOWNLOAD_RESLIST_TAG, "Download resource list: ",ex);
            return false;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //получаем размеры картинок
        setPreviewImageSize();

        setHasOptionsMenu(true);

        //запускаем поток считывания данных
        Observable.just("")
                .observeOn(Schedulers.io())
                .map(new Function<String, Boolean>() {
                    @Override
                    public Boolean apply(String s) throws Exception {

                        //если это не первый запуск тогда тогда читаем данные из БД
                        if(!DataCachingClass.firstLaunch && getDataFromDB()){
                            return true;
                        }

//                        Проверяем подключение к интернету с помощью подключения к сайту google.com
                        if(getContext() != null && !InternetSettingsClass.CheckInternetConnection(getContext())) {

                            //Если подключения к интернету нет пытаемся считать записи из базы
                            return getDataFromDB();

                        }

                        return DownloadDataFromYandexDisk();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {

                        //после загрузки данных инициилизируем view и устанавливаем позицию gridview
                        InitViews(view);

                        if(InfoClass.currentIndexImage != -1 && InfoClass.currentIndexImage <= dataList.size())
                            gridView.setSelection(InfoClass.currentIndexImage);

                        else if(InfoClass.currentIndexImage > dataList.size())
                            gridView.setSelection(dataList.size());

                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

                        DataCachingClass.firstLaunch = false;
                    }
                })
                .subscribe();
    }

    //метод получения токена
    private Credentials getCredentials(){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = preferences.getString(InfoClass.USERNAME, "");
        String token = preferences.getString(InfoClass.TOKEN, "");

        return new Credentials(username, token);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    //обработчик выбора пунктов меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.MainMenuItemAbout:
                FragmentHelperClass.ShowFragmentFromFragment(new AboutFragment(), getActivity(), true);
                break;

            case R.id.MainMenuItemLogin:
                DataCachingClass.firstLaunch = true;
                FragmentHelperClass.ShowFragmentFromFragment(new LoginFragment(), getActivity(), false);
                break;

            case R.id.MainMenuItemSettings:
                FragmentHelperClass.ShowFragmentFromFragment(new SettingsFragment(), getActivity(), true);
                break;

                //Если нажали на очистку кэша тогда открываем DialogFragment который запускает процесс очистки кэша
            case R.id.MainMenuItemClearCache:
                ClearCacheDialogFragment dialogFragment = new ClearCacheDialogFragment();
                dialogFragment.show(getActivity().getFragmentManager(), "123");
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        //получпем индекс gridView перед уходом с фрагмента
        if(gridView != null)
            InfoClass.currentIndexImage = gridView.getFirstVisiblePosition();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        gridView = null;
    }
}
