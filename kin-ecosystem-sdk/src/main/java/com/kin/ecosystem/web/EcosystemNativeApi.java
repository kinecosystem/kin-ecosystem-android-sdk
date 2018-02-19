package com.kin.ecosystem.web;

import android.util.Log;
import android.webkit.JavascriptInterface;

class EcosystemNativeApi {
    private EcosystemWebPageListener listener;

    @JavascriptInterface
    public void loaded() {
        Log.d("NativeApi", "loaded()");
        if (listener != null) {
            listener.onPageLoaded();
        }
    }

    @JavascriptInterface
    public void handleCancel() {
        Log.d("NativeApi", "handleCancel()");
        if (listener != null) {
            listener.onPageCancel();
        }
    }

    @JavascriptInterface
    public void handleResult(final String result) {
        Log.d("NativeApi", "handleResult(\"" + result + "\")");
        if (listener != null) {
            listener.onPageResult(result);
        }
    }

    void setListener(final EcosystemWebPageListener listener) {
        this.listener = listener;
    }
}
