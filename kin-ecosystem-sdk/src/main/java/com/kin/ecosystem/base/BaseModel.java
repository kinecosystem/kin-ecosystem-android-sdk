package com.kin.ecosystem.base;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CallSuper;

import com.kin.ecosystem.network.ApiClient;

public class BaseModel {

    protected ApiClient apiClient = new ApiClient();
    private Handler handler = new Handler(Looper.getMainLooper());

    @CallSuper
    protected void release() {
        apiClient = null;
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }

    protected void runOnMainThread(Runnable runnable) {
        if (handler != null) {
            handler.post(runnable);
        }
    }


}
