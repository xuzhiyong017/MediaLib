package com.sky.media.image.core.extra;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FpsTest {
    private static FpsTest mInstance = new FpsTest();
    private int mFps;
    private List<FpsGetListener> mFpsListeners = new ArrayList();
    private final Object mLock = new Object();
    private long mTime;

    public interface FpsGetListener {
        void onFpsGet(int i);
    }

    private FpsTest() {
    }

    public void printFpsGetListenersCount() {
        Log.e("FpsTest", "FPS Listener Count:" + this.mFpsListeners.size());
    }

    public void countFps() {
        synchronized (this.mLock) {
            if (this.mFpsListeners.size() > 0) {
                long currentTimeMillis = System.currentTimeMillis();
                if (this.mTime == 0) {
                    this.mTime = currentTimeMillis;
                }
                this.mFps++;
                if (currentTimeMillis - this.mTime >= 1000) {
                    for (FpsGetListener onFpsGet : this.mFpsListeners) {
                        onFpsGet.onFpsGet(this.mFps);
                    }
                    this.mTime = currentTimeMillis;
                    this.mFps = 0;
                }
            }
        }
    }

    public int getFps() {
        return this.mFps;
    }

    public static FpsTest getInstance() {
        return mInstance;
    }

    public void addFpsListener(FpsGetListener fpsGetListener) {
        synchronized (this.mLock) {
            if (!this.mFpsListeners.contains(fpsGetListener)) {
                this.mFpsListeners.add(fpsGetListener);
            }
        }
    }

    public void removeFpsListener(FpsGetListener fpsGetListener) {
        synchronized (this.mLock) {
            if (this.mFpsListeners.contains(fpsGetListener)) {
                this.mFpsListeners.remove(fpsGetListener);
            }
        }
    }

    public void clearFpsListener() {
        synchronized (this.mLock) {
            this.mFpsListeners.clear();
        }
    }
}
