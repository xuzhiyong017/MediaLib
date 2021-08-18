package com.sky.medialib.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Field;

public class InputMethodUtil {
    public static void hideSoftInput(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive() && activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getApplicationWindowToken(), 2);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void toggleSoftInput(EditText editText) {
        try {
            ((InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0, 2);
        } catch (Throwable e) {
           e.printStackTrace();
        }
    }

    public static void destroyView(Context context) {
        if (context != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                try {
                    for (String declaredField : new String[]{"mCurRootView", "mServedView", "mNextServedView", "mLastSrvView"}) {
                        Field declaredField2 = inputMethodManager.getClass().getDeclaredField(declaredField);
                        if (!declaredField2.isAccessible()) {
                            declaredField2.setAccessible(true);
                        }
                        Object obj = declaredField2.get(inputMethodManager);
                        if (obj != null && (obj instanceof View)) {
                            if (((View) obj).getContext() == context) {
                                declaredField2.set(inputMethodManager, null);
                            } else {
                                return;
                            }
                        }
                    }
                } catch (Throwable th) {
                    Log.w("InputMethodUtil", th.getMessage());
                }
            }
        }
    }
}
