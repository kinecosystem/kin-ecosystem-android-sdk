package com.kin.ecosystem.web;

import android.content.Context;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

public class EcosystemWebChromeClient extends WebChromeClient {
    private final Context context;

    EcosystemWebChromeClient(final Context context) {
        this.context = context;
    }

    @Override
    public boolean onConsoleMessage(final ConsoleMessage consoleMessage) {
        Log.d("WebChromeClient", "onConsoleMessage(\"" + consoleMessage.message() + "\")");
        return true;
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        Log.d("WebChromeClient", "onJsAlert(WEB_VIEW, \"" + url + "\"), \"" + message + "\", RESULT)");
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        return true;
    }
}
