package com.kin.ecosystem.web

import android.webkit.JavascriptInterface
import com.kin.ecosystem.core.Log
import com.kin.ecosystem.core.Logger

internal class EcosystemNativeApi {

    private var listener: EcosystemWebPageListener? = null

    @JavascriptInterface
    fun loaded() {
        Logger.log(Log().withTag(TAG).text("loaded()"))
        if (listener != null) {
            listener!!.onPageLoaded()
        }
    }

    @JavascriptInterface
    fun handleCancel() {
        Logger.log(Log().withTag(TAG).text("handleCancel()"))
        if (listener != null) {
            listener!!.onPageCancel()
        }
    }

    @JavascriptInterface
    fun handleResult(result: String) {
        Logger.log(Log().withTag(TAG).text("handleResult(\"$result\")"))
        if (listener != null) {
            listener!!.onPageResult(result)
        }
    }

    @JavascriptInterface
    fun handleClose() {
        Logger.log(Log().withTag(TAG).text("handleClose()"))
        if (listener != null) {
            listener!!.onPageClosed()
        }
    }

    fun setListener(listener: EcosystemWebPageListener) {
        this.listener = listener
    }

    companion object {

        private val TAG = EcosystemNativeApi::class.java.simpleName
    }
}
