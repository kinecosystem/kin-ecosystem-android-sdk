package com.kin.ecosystem.web;


import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.kin.ecosystem.Log;
import com.kin.ecosystem.Logger;

public class EcosystemWebViewClient extends WebViewClient {

	private static final String TAG = EcosystemWebViewClient.class.getSimpleName();

	EcosystemWebViewClient() {
		super();
	}

    @Override
    public void onPageFinished(WebView view, String url) {
		Logger.log(new Log().withTag(TAG).text("onPageFinished(WEB_VIEW, \"" + url + "\")"));
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
		Logger.log(new Log().withTag(TAG).text("onReceivedError(WEB_VIEW, REQUEST, \"" + error.toString() + "\")"));
    }
}
