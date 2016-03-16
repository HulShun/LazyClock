package com.example.lazyclock.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.lazyclock.Config;
import com.example.lazyclock.MyApplication;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.bean.User;
import com.example.lazyclock.bean.Weather;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by Administrator on 2015/12/8.
 */
public class FileUtil {
    public static final String dataJsonPath = "data.txt";
    public static final String weatherJsonPath = "weather.txt";

    /**
     * 保存标志，是保存闹钟列表还是保存天气情况
     */
    public static final int FLAG_ALARM = 0;
    public static final int FLAG_WEATHER = 1;


    private static FileUtil util;
    private static MyApplication app;

    private FileUtil() {

    }

    /**
     * 单例，返回实例
     *
     * @return
     */
    public static FileUtil getInstence() {
        if (util == null) {
            synchronized (FileUtil.class) {
                if (util == null) {
                    util = new FileUtil();
                    app = MyApplication.getInstance();
                }
            }
        }
        return util;
    }

    /**
     * 将json数据储存到本地
     *
     * @param json
     * @return
     */
    public boolean jsonToFile(String json, String dir, String name) {
        FileOutputStream out = null;
        if (isExtralDir()) {
            File fileDir = new File(dir);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            File file = new File(fileDir, name);
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                out = new FileOutputStream(file);
                out.write(json.getBytes());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("file", "保存json文件失败FileNotFoundException");
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("file", "保存json文件失败IOException");
                return false;
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            Log.d("file", "保存" + name + "的json文件成功");
            return true;
        }

        return false;
    }


    /**
     * 资源文件中的题库加载到路径中
     *
     * @param path
     */
    public void dbToPath(Context context, String path) {
        AssetManager am = context.getResources().getAssets();
        try {
            File newFile = new File(path, Config.MATH_DATABASE_NAME);
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            OutputStream out = new FileOutputStream(newFile);
            InputStream in = am.open(Config.MATH_DATABASE_NAME);
            byte[] buffer = new byte[1024];

            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
            LogUtil.i("db", "数学题库加载完成");
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.i("db", "asset下的数据库文件打开失败");
        }

    }


    /**
     * 从本地文件中读写保存的闹钟
     *
     * @return
     */
    public String jsonFromFile(String dir, String name) {
        String json = null;
        FileInputStream inputStream = null;

        StringBuffer stringBuffer = new StringBuffer();
        File file = new File(dir + "/" + name);
        int size = (int) file.length();
        //如果文件内容为空，就直接返回
        if (size == 0) {
            return "";
        }

        byte[] buffer = new byte[(int) file.length()];

        if (!file.exists()) {
            Log.d("file", "json文件不存在");
            return null;
        }
        try {
            inputStream = new FileInputStream(file);
            int b;
            while ((b = (inputStream.read(buffer))) != -1) {
                String m = new String(buffer);
                //避免最后出现乱字符填充
                // m = m.substring(0, m.lastIndexOf("]") + 1);
                stringBuffer.append(m);
            }
            json = stringBuffer.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("file", "打开json文件失败FileNotFoundException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("file", "打开json文件失败FileNotFoundException");
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e("file", name + "的json文件打开成功");
        return json;
    }


    /**
     * 复制文件
     *
     * @param filePath    旧文件
     * @param newFilePath 复制后的文件
     * @return
     */
    public boolean copyFile(String filePath, String newFilePath) throws IOException {
        String name = "data.txt";
        boolean b = false;
        InputStream in = null;
        OutputStream out = null;
        File oldFile = new File(filePath + "/" + name);
        if (!oldFile.exists()) {
            return false;
        }
        File newFile = new File(newFilePath + "/" + name);
        //如果目录不存在，就新建
        File newDir = newFile.getParentFile();
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        in = new FileInputStream(oldFile);
        out = new FileOutputStream(newFile);
        byte[] buffer;
        int size = (int) oldFile.length();
        if (size > 1024) {
            buffer = new byte[1024];
        } else if (size == 0) {
            return true;
        } else {
            buffer = new byte[(int) oldFile.length()];
        }
        int m;
        while ((m = in.read(buffer)) != -1) {
            out.write(buffer, 0, m);
        }

        out.close();
        in.close();

        return true;
    }


    /**
     * 保存用户设置到本地
     */
    public void saveSetting(MyApplication app) {
        User user = app.getMyUser();
        SharedPreferences settting = app.getSharedPreferences(Config.PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settting.edit();
        editor.putBoolean("isFirstTime", app.isFirstTime());
        editor.putString("name", user.mName);
        editor.putString("headPath", user.mHeadPath);
        editor.putString("weatherDay", user.mWeatherDay);
        editor.putInt("level", user.level);
        editor.putInt("weakupDays", user.getWeakupDays());
        editor.putBoolean("isLock", user.isLock);
        editor.putString("passwords", user.passwordsMD5);
        editor.apply();
    }

    public void readSettings(MyApplication app) {
        SharedPreferences settings = app.getSharedPreferences(Config.PREFERENCES_NAME, Activity.MODE_PRIVATE);
        User user = new User();
        app.setFirstTime(settings.getBoolean("isFirstTime", true));
        user.mName = settings.getString("name", "用户");
        user.mHeadPath = settings.getString("headPath", null);
        user.mWeatherDay = settings.getString("weatherDay", null);
        user.level = settings.getInt("level", 0);
        user.setWeakUpDays(settings.getInt("weakupDays", 0));
        user.isLock = settings.getBoolean("isLock", false);
        user.passwordsMD5 = settings.getString("passwords", null);
        app.setMyUser(user);
    }


    /**
     * @param path  路径
     * @param flags 保存的标识，是保存闹钟还是天气信息
     */
    public boolean saveToFile(final MyApplication app, final String path, final int flags) {

        final FileUtil fileUtil = FileUtil.getInstence();
        final JsonUtil jsonUtil = JsonUtil.getInstence();
        final boolean flag[] = new boolean[1];

        new Thread(new Runnable() {
            @Override
            public void run() {
                String json;
                //闹钟列表写出文件

                if (flags == FileUtil.FLAG_ALARM) {
                    List<AlarmBean> list = app.getAlarDates();
                    json = jsonUtil.listToJson(list);
                    if (json != null) {
                        flag[0] = fileUtil.jsonToFile(json, path, FileUtil.dataJsonPath);
                    }
                }
                if (flags == FileUtil.FLAG_WEATHER) {
                    Weather weather = app.getWeather();
                    //天气情况写出文件
                    json = jsonUtil.beanToJson(weather);
                    if (json != null) {
                        flag[0] = fileUtil.jsonToFile(json, path, FileUtil.weatherJsonPath);
                    }
                }

            }
        }).start();

        return flag[0];
    }


    /**
     * 将所有保存在本地的数据读取   （线程处理）
     */
    public void readFromFile(final MyApplication app, final String path, final int flags) {
        final FileUtil fileUtil = FileUtil.getInstence();
        final JsonUtil jsonUtil = JsonUtil.getInstence();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String json;
                if (flags == FileUtil.FLAG_ALARM) {
                    LogUtil.d("", "去加载闹钟");
                    json = fileUtil.jsonFromFile(path, FileUtil.dataJsonPath);
                    if (json != null) {
                        app.setAlarDates((List<AlarmBean>) jsonUtil.toObjects(json, new TypeToken<List<AlarmBean>>() {
                        }.getType()));
                    }
                }

                //加载天气
                if (WeatherUtil.isTodayWeather(app)) {
                    LogUtil.d("file", "去加载天气");
                    json = fileUtil.jsonFromFile(app.getDefalut_path(), FileUtil.weatherJsonPath);
                    if (json != null) {
                        Weather weather = jsonUtil.toObjects(json, Weather.class);
                        app.setWeather(weather);
                    }
                }
            }
        }).start();

    }

    /**
     * 备份闹钟列表
     *
     * @param app
     * @return
     */
    public boolean backupList(MyApplication app) {
        boolean flag;
        FileUtil util = FileUtil.getInstence();
        if (!util.isExtralDir()) {
            Toast.makeText(app.getApplicationContext(), "外部储存设备不存在！", Toast.LENGTH_LONG).show();
        }
        String path = Environment.getExternalStorageDirectory().getPath() + "/LazyClok";
        flag = util.saveToFile(app, path, FileUtil.FLAG_ALARM);
        return flag;
    }

    /**
     * 恢复闹钟列表
     *
     * @param context
     * @return
     */
    public boolean recoverList(MyApplication context) {
        boolean flag;
        if (!isExtralDir()) {
            Toast.makeText(context, "外部储存设备不存在！", Toast.LENGTH_LONG).show();
            return false;
        }
        String path = Environment.getExternalStorageDirectory().getPath() + "/LazyClok";
        readFromFile(context, path, FileUtil.FLAG_ALARM);
        return true;
    }


    /**
     * Uri中获取真实地址
     * 从Uri获取文件绝对路径
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        //对4.4以上版本的uri操作
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }


            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }

        //对4.4以下版本的uri操作
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */

    private String getDataColumn(Context context, Uri uri, String selection,
                                 String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 判断外部储存设备是否存在
     *
     * @return
     */
    public boolean isExtralDir() {

        if (Environment.isExternalStorageEmulated() ||
                !(Environment.isExternalStorageRemovable()) &&
                        Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;

        }
        return false;
    }


}
