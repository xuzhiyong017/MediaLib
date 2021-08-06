package com.sky.medialib.util;

import android.app.Dialog;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sky.media.image.core.util.LogUtils;
import com.sky.media.kit.BaseMediaApplication;
import com.sky.media.kit.base.BaseActivity;

import java.lang.reflect.Field;

public class DialogUtil {

    public static class GlobalDialogFragment extends DialogFragment {

        public Dialog dialog;

        public Dialog onCreateDialog(Bundle bundle) {
            return this.dialog;
        }

        public void show(FragmentManager fragmentManager, String str) {
            try {
                Field declaredField = DialogFragment.class.getDeclaredField("mDismissed");
                declaredField.setAccessible(true);
                declaredField.set(this, Boolean.valueOf(false));
                declaredField = DialogFragment.class.getDeclaredField("mShownByMe");
                declaredField.setAccessible(true);
                declaredField.set(this, Boolean.valueOf(true));
            } catch (Exception e) {
                LogUtils.loge("dialog",e.getMessage());
            }
            FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
            beginTransaction.add(this, str);
            beginTransaction.commitAllowingStateLoss();
        }
    }

    public static boolean showDialog(Dialog dialog) {
        BaseActivity b = BaseMediaApplication.Companion.getTopActivity();
        if (b != null) {
            try {
                if (!(b.isFinishing() || b.isDestroyed())) {
                    showDialog(b, dialog);
                    return true;
                }
            } catch (Throwable th) {
                LogUtils.loge("dialog",th.getMessage());
                return false;
            }
        }
        if (VERSION.SDK_INT >= 19) {
            showToast(dialog);
        } else {
            showAlert(dialog);
        }
        return true;
    }

    private static void showDialog(BaseActivity activity, Dialog dialog) {
        GlobalDialogFragment globalDialogFragment = new GlobalDialogFragment();
        globalDialogFragment.dialog = dialog;
        globalDialogFragment.show(activity.getSupportFragmentManager(), "GlobalDialog");
    }

    private static void showToast(Dialog dialog) {
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        dialog.show();
    }

    private static void showAlert(Dialog dialog) {
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }
}
