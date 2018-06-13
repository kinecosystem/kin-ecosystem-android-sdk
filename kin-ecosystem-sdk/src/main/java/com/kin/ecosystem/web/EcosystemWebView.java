package com.kin.ecosystem.web;

import static com.kin.ecosystem.BuildConfig.DEBUG;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.kin.ecosystem.Configuration;

public class EcosystemWebView extends WebView {

    private static final String HTML_URL = Configuration.getEnvironment().getEcosystemWebFront();
    private static final String JS_INTERFACE_OBJECT_NAME = "KinNative";

    private final Handler mainThreadHandler;
    private final EcosystemNativeApi nativeApi;
    private final EcosystemWebViewClient webViewClient;
    private final EcosystemWebChromeClient webChromeClient;

    public EcosystemWebView(Context context) {
        this(context, null);
    }

    public EcosystemWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EcosystemWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mainThreadHandler = new Handler(Looper.getMainLooper());

        webViewClient = new EcosystemWebViewClient();
        setWebViewClient(this.webViewClient);

        webChromeClient = new EcosystemWebChromeClient(context);
        setWebChromeClient(this.webChromeClient);

        final WebSettings settings = this.getSettings();
        settings.setJavaScriptEnabled(true);

        nativeApi = new EcosystemNativeApi();
        addJavascriptInterface(nativeApi, JS_INTERFACE_OBJECT_NAME);
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(DEBUG);
        }
    }

    public void load() {
        loadUrl(HTML_URL);
    }

    public void setListener(final EcosystemWebPageListener listener) {
        nativeApi.setListener(listener);
    }

    public void render(final String pollJsonData) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    render(pollJsonData);
                }
            });

            return;
        }

        final StringBuilder js = new StringBuilder("kin.renderPoll(");
        js.append(pollJsonData).append(")");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(js.toString(), null);
        } else {
            loadUrl(js.toString());
        }
    }

    public void release() {
        mainThreadHandler.removeCallbacksAndMessages(null);
        onPause();
        destroy();
    }
}
