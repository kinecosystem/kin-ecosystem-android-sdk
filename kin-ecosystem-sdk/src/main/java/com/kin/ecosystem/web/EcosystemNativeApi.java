package com.kin.ecosystem.web;

import android.webkit.JavascriptInterface;
import com.kin.ecosystem.Log;

class EcosystemNativeApi {

    private static final String TAG = EcosystemNativeApi.class.getSimpleName();

    private EcosystemWebPageListener listener;

    @JavascriptInterface
    public void loaded() {
        new Log().withTag(TAG).text("loaded()").log();
        if (listener != null) {
            listener.onPageLoaded();
        }
    }

    @JavascriptInterface
    public void handleCancel() {
        new Log().withTag(TAG).text("handleCancel()").log();
        if (listener != null) {
            listener.onPageCancel();
        }
    }

    @JavascriptInterface
    public void handleResult(final String result) {
        new Log().withTag(TAG).text("handleResult(\"" + result + "\")").log();
        if (listener != null) {
            listener.onPageResult(result);
        }
    }

    @JavascriptInterface
    public void displayTopBar(boolean shouldDisplay) {
        new Log().withTag(TAG).text("displayTopBar(\"" + shouldDisplay + "\")").log();
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
        new Log().withTag(TAG).text("handleClose()").log();
        if (listener != null) {
            listener.onPageClosed();
        }
    }

    void setListener(final EcosystemWebPageListener listener) {
        this.listener = listener;
    }
}
