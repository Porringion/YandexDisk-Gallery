package com.example.evgdev.galleryyandexdisk.AnyClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.evgdev.galleryyandexdisk.R;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

//Класс для работы с кэшированием данныъ
public class DataCachingClass {

    public static final int IMAGE_PATH_TAG = R.id.idFilePath;
    public static final int MD5_TAG = R.id.idFileChecksumm;
    public static final String PREFIX_PREVIEWIMAGE_NAME = "PREVIEW";
    public static final String PREFIX_FULLIMAGE_NAME = "FULL";
    public static File CacheDir;
    public static final String PREFFERENCE_KEY_USE_CACHE = "USE_CACHE";
    public static boolean firstLaunch = true;

    //Устанавливаем папку кэша
    public static void setCacheDir(Context context){

        if(context.getExternalCacheDir() != null)
            CacheDir = context.getExternalCacheDir();

        else if (context.getFilesDir() != null)
            CacheDir = context.getFilesDir();

        else if(context.getCacheDir() != null)
            CacheDir = context.getCacheDir();
    }

    //Получаем текущие настройки кэширования
    public static boolean getPreferenceUseCache(Context context){

        if(context == null)
            return false;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PREFFERENCE_KEY_USE_CACHE, false);
    }

//    метод создания файла из битмапы
    public static void CreateCachedFile(File cacedFile, Bitmap bitmap){

        try {

            FileOutputStream fileOutputStream = new FileOutputStream(cacedFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, fileOutputStream);

            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (Exception e) {
            Log.e("Save file log", e.toString());
        }

    }

    //метод попытки декодировать файл в bitmap
    public static Bitmap CheckDecodingBitmap(File result, Context mContext){

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(result.getAbsolutePath());

            //если считывание не удалось помещаем возвращаем картинку ошибки
            if (bitmap == null) {
                //Файл мб не корректный поэтому его удаляем
                if (result.exists())
                    result.delete();

                return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.errorimagefull);

            }else
                return bitmap;

        }catch (Exception e){
            return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.errorimagefull);
        }

    }

    //метод удаления данных из папки кеша
    public static void deleteFilesFromCacheDir(File cacheDir){
        if(cacheDir != null && cacheDir.exists())
            for (File file:cacheDir.listFiles())
                file.delete();
    }

    //метод скачивания preview с яндекс диска
    public static Bitmap DownloadPreviewImageByUrl(String strUrl, String fileChecksumm, Context mContext, int imageWidth, int imageHeight){

        try {

            String fileName = DataCachingClass.PREFIX_PREVIEWIMAGE_NAME + fileChecksumm;

            File cachedPreviewImage = new File(DataCachingClass.CacheDir, new File(fileName).getName());

            //проверяем наличие закэшированного файла
            if (cachedPreviewImage.exists())
                return CheckDecodingBitmap(cachedPreviewImage, mContext);

            //генерируем гет запрос для получения массива previewImage для того чтобы получить картинку нужно отправить в шапке запроса токен
            Request request = InternetSettingsClass.getRequestByURL(strUrl);

            Response response = InternetSettingsClass.okHttpClient.newCall(request).execute();

            ResponseBody responseBody = response.body();

            //Если тип данных в гет запросе не картинка тогда выходим
            if(!String.valueOf(responseBody.contentType()).contains(InternetSettingsClass.MEDIA_TYPE_IMAGE)){

                throw new Exception("Данные не соответствуют картинке");

            }else if(responseBody.contentLength() == 0){

                throw new Exception("Ошибка ответ от пуст");

            }

            //преобразовываем полученный массив байтов в картинку
            Bitmap bitmap = BitmapFactory.decodeByteArray(responseBody.bytes(), 0, (int) responseBody.contentLength());

            //создаем файл
            DataCachingClass.CreateCachedFile(cachedPreviewImage, bitmap);

            return bitmap;
        } catch (Exception e) {

            Log.e("Error", e.getMessage());
            e.printStackTrace();

            //если произошла ошибка выводим картинку ошибки из ресурсов
            return Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.drawable.errorimagepreview),
                    imageWidth,
                    imageHeight,
                    false);
        }
    }
}
