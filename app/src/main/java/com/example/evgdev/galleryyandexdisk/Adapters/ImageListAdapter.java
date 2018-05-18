package com.example.evgdev.galleryyandexdisk.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.evgdev.galleryyandexdisk.AnyClasses.DataCachingClass;
import com.example.evgdev.galleryyandexdisk.AnyClasses.InternetSettingsClass;
import com.example.evgdev.galleryyandexdisk.DaggerClasses.RoomClasses.YandexDiskData;
import com.example.evgdev.galleryyandexdisk.R;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ImageListAdapter extends BaseAdapter {

    private Context mContext;
    private int imageWidth;
    private int imageHeight;
    private List<YandexDiskData> list = new ArrayList<>();

    public ImageListAdapter(Context context, List<YandexDiskData> resourceList,int imageHeight,int imageWidth){
        this.mContext = context;
        this.list = resourceList;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public List<YandexDiskData> getList() {
        return list;
    }

    public void setList(List<YandexDiskData> list) {
        this.list = list;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public YandexDiskData getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        YandexDiskData item = list.get(position);

        ImageView imageView = (ImageView) convertView;

        if (convertView == null) {
            convertView = new ImageView(mContext);
            imageView = (ImageView) convertView;

        }

        //качаем битмап и устанавливаем его в view
        SetBitmapOnImageView(item.previewURL, item.md5, imageView);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth, imageHeight));

        //устанавливаем тэги с данными о картинке
        imageView.setTag(DataCachingClass.IMAGE_PATH_TAG, item.filePath);
        imageView.setTag(DataCachingClass.MD5_TAG, item.md5);

        return (convertView);
    }

    private void SetBitmapOnImageView(final String url, final String fileChecksumm, final ImageView imageView){

        Observable.just(url)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String strUrl) throws Exception {

                        Bitmap bitmap = null;

                        //Т.к. может не выполниться декодирование файла из массива байтов в битмап и вернется null поэтому декодируем картинку до тех пор пока не хватит памяти
                        while (bitmap == null){

                            if(bitmap == null)
                                bitmap = DataCachingClass.DownloadPreviewImageByUrl(strUrl, fileChecksumm, mContext, imageWidth, imageHeight);
                        }

                        return bitmap;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap previewImage) throws Exception {

                        //устанавливаем битмап в imageView
                        imageView.setImageBitmap(previewImage);
                    }
                })
                .subscribe();

    }
}
