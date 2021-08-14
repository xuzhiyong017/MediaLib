package com.sky.medialib.ui.kit.effect;

import android.content.Context;
import android.text.TextUtils;

import com.sky.media.image.core.util.BitmapUtil;
import com.sky.media.kit.render.sticker.Sticker;
import com.sky.medialib.util.NumberUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;

public class EffectReader {
    public static final String EFFECT_JSON = "effect.json";

    public interface IEffectParser {
        Effect parse(String str) throws Exception;
    }

    public static Effect readEffect(Context context, String str, IEffectParser iEffectParser) {
        return readEffect(context, str, iEffectParser, context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels);
    }


    public static Effect readEffect(Context context,String str, IEffectParser iEffectParser, int i, int i2){
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        InputStream inputStream = null;
        Effect effect = null;
        try {
            inputStream = BitmapUtil.Scheme.ASSETS.belongsTo(str) ? context.getAssets().open(BitmapUtil.Scheme.ASSETS.crop(str) + EFFECT_JSON) : BitmapUtil.Scheme.FILE.belongsTo(str) ? new FileInputStream(new File(BitmapUtil.Scheme.FILE.crop(str)) + EFFECT_JSON) : new FileInputStream(new File(str + EFFECT_JSON));
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                sb.append(readLine);
            }
            effect = iEffectParser.parse(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e1) {
                   e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e3) {
                   e3.printStackTrace();
                }
            }
        }

        if (effect != null) {
            if (!TextUtils.isEmpty(effect.audio)) {
                effect.audio = str + effect.audio;
            }
            if (effect.filter != null) {
                for (Effect.Filter filter : effect.filter) {
                    if (filter.type == 6 || filter.type == 7) {
                        filter.src = str + filter.src;
                    } else if (filter.type == 4) {
                        if (filter.component != null && !filter.component.isEmpty()) {
                            float min = ((float) Math.min(i, i2)) / 720.0f;
                            LinkedHashMap linkedHashMap = new LinkedHashMap();
                            for (Sticker.Component component : filter.component) {
                                component.src = str + component.src;
                                if (!TextUtils.isEmpty(component.sound)) {
                                    component.sound = str + component.sound;
                                }
                                if (component.type == 1) {
                                    component.width = Math.round(((float) component.width) * min);
                                    component.height = Math.round(((float) component.height) * min);
                                    component.left = Math.round(((float) component.left) * min);
                                    component.right = Math.round(((float) component.right) * min);
                                    component.top = Math.round(((float) component.top) * min);
                                    component.bottom = Math.round(((float) component.bottom) * min);
                                } else if (component.type == 2) {
                                    component.faces = new ArrayList();
                                    Sticker.FacePoint facePoint = new Sticker.FacePoint();
                                    facePoint.id = 35;
                                    facePoint.x = 145;
                                    facePoint.y = 82;
                                    Sticker.FacePoint facePoint2 = new Sticker.FacePoint();
                                    facePoint2.id = 40;
                                    facePoint2.x = 360;
                                    facePoint2.y = 81;
                                    component.faces.add(facePoint);
                                    component.faces.add(facePoint2);
                                }
                                String str2 = component.src;
                                String[] strArr = null;
                                try {
                                    strArr = BitmapUtil.Scheme.ASSETS.belongsTo(str2) ? context.getAssets().list(BitmapUtil.Scheme.ASSETS.crop(str2)) : BitmapUtil.Scheme.FILE.belongsTo(str2) ? new File(BitmapUtil.Scheme.FILE.crop(str2)).list() : new File(str2).list();
                                } catch (Exception e14) {
                                    e14.printStackTrace();
                                }
                                ArrayList arrayList = new ArrayList();
                                if (strArr != null) {
                                    for (String str3 : strArr) {
                                        if (!str3.toLowerCase().contains(".DS_Store".toLowerCase()) && !str3.toLowerCase().contains("Thumbs.db".toLowerCase())) {
                                            arrayList.add(component.src + "/" + str3);
                                        }
                                    }
                                }
                                Collections.sort(arrayList, new Comparator<String>() {
                                    @Override
                                    public int compare(String str, String str2) {
                                        String[] split = str.split("_");
                                        String[] split2 = str2.split("_");
                                        String str3 = split[split.length - 1];
                                        if (str3.contains(".")) {
                                            str3 = str3.substring(0, str3.indexOf("."));
                                        }
                                        if (!TextUtils.isDigitsOnly(str3)) {
                                            str3 = str3.substring(str3.length() - 2);
                                        }
                                        String str4 = split2[split2.length - 1];
                                        if (str4.contains(".")) {
                                            str4 = str4.substring(0, str4.indexOf("."));
                                        }
                                        if (!TextUtils.isDigitsOnly(str4)) {
                                            str4 = str4.substring(str4.length() - 2);
                                        }
                                        return NumberUtil.valueOfInt(str3) - NumberUtil.valueOfInt(str4);
                                    }
                                });
                                linkedHashMap.put(component, arrayList);
                            }
                            filter.componentResourceMap = linkedHashMap;
                        }
                    } else if (filter.type == 5 && filter.textures != null && !filter.textures.isEmpty()) {
                        ArrayList arrayList2 = new ArrayList();
                        for (String str4 : filter.textures) {
                            arrayList2.add(str + str4);
                        }
                        filter.textures = arrayList2;
                    }
                }
            }
        }
        return effect;
    }
}
