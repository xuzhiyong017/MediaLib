package com.sky.medialib.ui.picture.helper;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.util.LruCache;

import com.sky.media.kit.BaseMediaApplication;
import com.sky.medialib.util.FileUtil;

import java.io.File;
import java.util.HashMap;

public class PictureBitmapHolder {

    private static final Point SCREEN_SIZE = new Point(BaseMediaApplication.sContext.getResources().getDisplayMetrics().widthPixels, BaseMediaApplication.sContext.getResources().getDisplayMetrics().heightPixels);
    private static PictureBitmapHolder mInstance = new PictureBitmapHolder();

    private final int MAX_CACHE_SIZE = 9;

    private LruCache<String, Bitmap> mProcessBitmapCache = new LruCache(MAX_CACHE_SIZE);

    private LruCache<String, Bitmap> mOriginalBitmapCache = new LruCache(MAX_CACHE_SIZE);

    private HashMap<String, Uri> mProcessImages = new HashMap();

    private HashMap<String, Uri> mOriginalImages = new HashMap();

    private PictureBitmapHolder() {
    }


    public static PictureBitmapHolder getInstance() {
        return mInstance;
    }

    public void clear() {
        this.mProcessImages.clear();
        this.mOriginalImages.clear();
        this.mProcessBitmapCache.evictAll();
        this.mOriginalBitmapCache.evictAll();
    }


    public Bitmap getOriginalImage(String hashCodeToHold) {

        if (this.mOriginalBitmapCache.get(hashCodeToHold) != null) {
            return (Bitmap) this.mOriginalBitmapCache.get(hashCodeToHold);
        }
        Bitmap a;
        if (this.mOriginalImages.get(hashCodeToHold) != null) {
            Uri uri = (Uri) this.mOriginalImages.get(hashCodeToHold);
            Bitmap a2 = getTempPng(uri);
            if (a2 != null) {
                this.mOriginalBitmapCache.put(hashCodeToHold, a2);
                return a2;
            }
            a = getBitmap(SCREEN_SIZE.x, SCREEN_SIZE.y, uri);
            if (a == null) {
                return a;
            }
            this.mOriginalBitmapCache.put(hashCodeToHold, a);
            return a;
        }
        a = getBitmap(SCREEN_SIZE.x, SCREEN_SIZE.y, Uri.parse(hashCodeToHold));
        if (a == null) {
            return a;
        }
        this.mOriginalBitmapCache.put(hashCodeToHold, a);
        return a;
    }

    private Bitmap getTempPng(Uri uri) {
        try {
            return Media.getBitmap(BaseMediaApplication.sContext.getContentResolver(), uri);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap getBitmap(int i, int i2, Uri uri) {
        String a = FileUtil.INSTANCE.getPathFromUri(BaseMediaApplication.sContext, uri);
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(a, options);
        int i3 = options.outWidth / i;
        int i4 = options.outHeight / i2;
        if (i3 <= i4) {
            i3 = i4;
        }
        if (i3 < 1) {
            i3 = 1;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = i3;
        options.inPreferredConfig = Config.ARGB_8888;
        Bitmap decodeFile = BitmapFactory.decodeFile(a, options);
        if (decodeFile == null) {
            return null;
        }
        int attributeInt;
        float width = ((float) i) / (((float) decodeFile.getWidth()) * 1.0f);
        float height = ((float) i2) / (((float) decodeFile.getHeight()) * 1.0f);
        if (width >= height) {
            width = height;
        }
        try {
            attributeInt = new ExifInterface(a).getAttributeInt("Orientation", 0);
            switch (attributeInt) {
                case 1:
                    attributeInt = 0;
                    break;
                case 3:
                    attributeInt = 180;
                    break;
                case 6:
                    attributeInt = 90;
                    break;
                case 8:
                    attributeInt = 270;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            attributeInt = 0;
        }
        if (attributeInt == 0 && width == 1.0f) {
            return fixBitmap(decodeFile);
        }
        Matrix matrix = new Matrix();
        matrix.postRotate((float) attributeInt);
        matrix.postScale(width, width);
        return fixBitmap(Bitmap.createBitmap(decodeFile, 0, 0, decodeFile.getWidth(), decodeFile.getHeight(), matrix, true));
    }

    private Bitmap fixBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int i;
        boolean z;
        boolean z2 = false;
        int width = bitmap.getWidth();
        if (width % 2 != 0) {
            width++;
            z2 = true;
        }
        int height = bitmap.getHeight();
        if (height % 2 != 0) {
            i = height + 1;
            z = true;
        } else {
            int i2 = height;
            z = z2;
            i = i2;
        }
        if (z) {
            return Bitmap.createScaledBitmap(bitmap, width, i, true);
        }
        return bitmap;
    }

    public Bitmap handleBitmap(Bitmap bitmap, int screenWidth, int screenHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float max = Math.max(screenWidth* 1.0f /  width, screenHeight * 1.0f / height);
        Matrix matrix = new Matrix();
        matrix.postScale(max, max);
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return Bitmap.createBitmap(createBitmap, (createBitmap.getWidth() - screenWidth) / 2, (createBitmap.getHeight() - screenHeight) / 2, screenWidth, screenHeight);
    }
}
