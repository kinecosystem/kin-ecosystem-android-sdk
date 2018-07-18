package com.kin.ecosystem.web;

import android.content.Context;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;
import com.kin.ecosystem.Log;

public class EcosystemWebChromeClient extends WebChromeClient {

    private static final String TAG = EcosystemWebChromeClient.class.getSimpleName();

    private final Context context;

    EcosystemWebChromeClient(final Context context) {
        this.context = context;
    }

    @Override
    public boolean onConsoleMessage(final ConsoleMessage consoleMessage) {
        new Log().withTag(TAG).text("onConsoleMessage(\"" + consoleMessage.message() + "\")").log();
        return true;
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        new Log().withTag(TAG).text("onJsAlert(WEB_VIEW, \"" + url + "\"), \"" + message + "\", RESULT)").log();
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        return true;
    }
}
