package com.sky.medialib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Environment;

import com.sky.media.kit.BaseMediaApplication;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Storage {

    private static final String EXTERNAL_DIR = Environment.getExternalStorageDirectory().toString();

    private static final String DCIM_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();

    private static final String Publish_Dir = (DCIM_DIR + File.separator + "MEDIA_LIB" + File.separator);

    private static final String External_File = (EXTERNAL_DIR + File.separator + ".medialib" + File.separator);

    private static final String Temp_Image = (External_File + "temp_image" + File.separator);

    private static final String Temp_Video = (External_File + "temp_video" + File.separator);

    private static final String Temp_Music = (External_File + "temp_music" + File.separator);

    private static final String Temp_Advertise = (External_File + "temp_advertise" + File.separator);

    private static final String Video_Draft = (External_File + "video_draft" + File.separator);

    private static final String Video_Cache = (External_File + "video_cache" + File.separator);

    private static final String Music_Cache = (External_File + "music_cache" + File.separator);

    private static final String Advertise_Cache = (External_File + "advertise_cache" + File.separator);

    private static final String Crash_Dir = (External_File + "crash" + File.separator);

    private static final String Magics_Dir = (External_File + "magics" + File.separator);

    private static final String Filters_Dir = (External_File + "filters" + File.separator);

    private static final String Effect_Dir = (External_File + "effect_cache" + File.separator);

    public static String getFilePathByType(int type) {
        String str = "";
        switch (type) {
            case 0:
                str = Storage.getFileDir(External_File);
                Storage.makeFileNoMedia(External_File);
                break;
            case 1:
                str = Storage.getFileDir(Publish_Dir);
                break;
            case 2:
                str = Storage.getFileDir(Temp_Image);
                Storage.makeFileNoMedia(Temp_Image);
                break;
            case 3:
                str = Storage.getFileDir(Temp_Video);
                Storage.makeFileNoMedia(Temp_Video);
                break;
            case 4:
                str = Storage.getFileDir(Temp_Music);
                Storage.makeFileNoMedia(Temp_Music);
                break;
            case 5:
                str = Storage.getFileDir(Temp_Advertise);
                Storage.makeFileNoMedia(Temp_Advertise);
                break;
            case 6:
                str = Storage.getFileDir(Video_Draft);
                Storage.makeFileNoMedia(Video_Draft);
                break;
            case 11:
                str = Storage.getFileDir(Video_Cache);
                Storage.makeFileNoMedia(Video_Cache);
                break;
            case 12:
                str = Storage.getFileDir(Music_Cache);
                break;
            case 13:
                str = Storage.getFileDir(Advertise_Cache);
                Storage.makeFileNoMedia(Advertise_Cache);
                break;
            case 21:
                str = Storage.getFileDir(Crash_Dir);
                break;
            case 22:
                str = Storage.getFileDir(Magics_Dir);
                Storage.makeFileNoMedia(Magics_Dir);
                break;
            case 23:
                str = Storage.getFileDir(Filters_Dir);
                Storage.makeFileNoMedia(Filters_Dir);
                break;
            case 24:
                str = Storage.getFileDir(Effect_Dir);
                Storage.makeFileNoMedia(Effect_Dir);
                break;
        }
        if (str.endsWith(File.separator)) {
            return str;
        }
        return str + File.separator;
    }


    private static String getFileDir(String str) {
        File file = new File(str);
        if (!(file.exists() && file.isDirectory())) {
            file.mkdirs();
        }
        return str;
    }

    public static String storageToTempPngPath(Bitmap bitmap) {
        String str = Storage.getFilePathByType(2) + Util.getNewPngPath();
        Storage.saveBitmapToFile(bitmap, str, null, 0, false);
        return str;
    }

    public static String storageToTempJpgPath(Bitmap bitmap) {
        String str = Storage.getFilePathByType(2) + Util.getNewJpgPath();
        Storage.saveBitmapToFile(bitmap, str, null, 0, false);
        return str;
    }

    public static String storageToTempJpgPath(Bitmap bitmap, Location location, int i) {
        String str = Storage.getFilePathByType(2) + Util.getNewJpgPath();
        Storage.saveBitmapToFile(bitmap, str, location, i, true);
        return str;
    }

    public static String storageToDCIMJpgPath(Bitmap bitmap) {
        String str = Storage.getFilePathByType(1) + Util.getNewJpgPath();
        Storage.saveBitmapToFile(bitmap, str, null, 0, true);
        return str;
    }


    public static String storageToDCIMJpgPath(Bitmap bitmap, Location location, int i) {
        String str = Storage.getFilePathByType(1) + Util.getNewJpgPath();
        Storage.saveBitmapToFile(bitmap, str, location, i, true);
        return str;
    }


    public static void saveBitmapToFile(Bitmap bitmap, String str, Location location, int i, boolean z) {
        BufferedOutputStream bufferedOutputStream = null;
        int i2;
        try {
            File newFile = new File(str);
            if(!newFile.exists()){
                newFile.createNewFile();
            }
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(newFile));
            bitmap.compress(str.endsWith(".png") ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
            bufferedOutputStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(bufferedOutputStream != null){
                try {
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        if (Publish_Dir.equals(str.substring(0, str.lastIndexOf("/") + 1))) {
            Util.notifyMediaCenter((Context) BaseMediaApplication.sContext, str);
        }
        if (z && str.endsWith(".jpg")) {
            try {
                ExifInterface exifInterface = new ExifInterface(str);
                exifInterface.setAttribute("DateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date()));
                if (location != null) {
                    exifInterface.setAttribute("GPSLatitude", Util.getGPSLocation(location.getLatitude()));
                    exifInterface.setAttribute("GPSLatitudeRef", location.getLatitude() > 0.0d ? "N" : "S");
                    exifInterface.setAttribute("GPSLongitude", Util.getGPSLocation(location.getLongitude()));
                    exifInterface.setAttribute("GPSLongitudeRef", location.getLongitude() > 0.0d ? "E" : "W");
                }
                switch (i) {
                    case 0:
                        i2 = 1;
                        break;
                    case 90:
                        i2 = 6;
                        break;
                    case 180:
                        i2 = 3;
                        break;
                    case 270:
                        i2 = 8;
                        break;
                    default:
                        i2 = 1;
                        break;
                }
                exifInterface.setAttribute("Orientation", String.valueOf(i2));
                exifInterface.saveAttributes();
            } catch (Throwable th3) {
                th3.printStackTrace();
            }
        }

    }

    private static void makeFileNoMedia(String str) {
        File file = new File(str + ".nomedia");
        try {
            if (!file.exists() || !file.isFile()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
