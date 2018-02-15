package com.kin.ecosystem.web;

import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class EcosystemWebViewClient extends WebViewClient {
	EcosystemWebViewClient() {
		super();
	}

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.d("WebViewClient", "onPageFinished(WEB_VIEW, \"" + url + "\")");
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        Log.d("WebViewClient", "onReceivedError(WEB_VIEW, REQUEST, \"" + error.toString() + "\")");
    }
}
