package com.kin.ecosystem.base;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CallSuper;
import com.kin.ecosystem.network.ApiClient;

public class BaseModel {

    protected static ApiClient apiClient = new ApiClient();
    private static Handler handler = new Handler(Looper.getMainLooper());

    @CallSuper
    protected void release() {
        handler.removeCallbacksAndMessages(null);
    }

    protected void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            handler.post(runnable);
        } else {
            runnable.run();
        }
    }
}
