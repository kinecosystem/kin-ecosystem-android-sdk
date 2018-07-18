package com.kin.ecosystem.web;

import android.webkit.JavascriptInterface;
import com.kin.ecosystem.Log;
import com.kin.ecosystem.Logger;

class EcosystemNativeApi {

    private static final String TAG = EcosystemNativeApi.class.getSimpleName();

    private EcosystemWebPageListener listener;

    @JavascriptInterface
    public void loaded() {
        Logger.log(new Log().withTag(TAG).text("loaded()"));
        if (listener != null) {
            listener.onPageLoaded();
        }
    }

    @JavascriptInterface
    public void handleCancel() {
        Logger.log(new Log().withTag(TAG).text("handleCancel()"));
        if (listener != null) {
            listener.onPageCancel();
        }
    }

    @JavascriptInterface
    public void handleResult(final String result) {
        Logger.log(new Log().withTag(TAG).text("handleResult(\"" + result + "\")"));
        if (listener != null) {
            listener.onPageResult(result);
        }
    }

    @JavascriptInterface
    public void displayTopBar(boolean shouldDisplay) {
        Logger.log(new Log().withTag(TAG).text("displayTopBar(\"" + shouldDisplay + "\")"));
        if (listener != null) {
            if (shouldDisplay) {
                listener.showToolbar();
            } else {
                listener.hideToolbar();
            }
        }
    }

    @JavascriptInterface
    public void handleClose() {
        Logger.log(new Log().withTag(TAG).text("handleClose()"));
        if (listener != null) {
            listener.onPageClosed();
        }
    }

    void setListener(final EcosystemWebPageListener listener) {
        this.listener = listener;
    }
}
