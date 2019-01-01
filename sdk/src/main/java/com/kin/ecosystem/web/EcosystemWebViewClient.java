package com.kin.ecosystem.web;

import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;

public class EcosystemWebViewClient extends WebViewClient {

	private static final String TAG = EcosystemWebViewClient.class.getSimpleName();

    private final Context context;

	EcosystemWebViewClient(final Context context) {
		super();
        this.context = context;
	}

    @Override
    public void onPageFinished(WebView view, String url) {
		Logger.log(new Log().withTag(TAG).text("onPageFinished(WEB_VIEW, \"" + url + "\")"));
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
		Logger.log(new Log().withTag(TAG).text("onReceivedError(WEB_VIEW, REQUEST, \"" + error.toString() + "\")"));
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url != null && url.startsWith("mailto:")) {
            MailTo mt = MailTo.parse(url);
            Intent intent = newEmailIntent(mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
            context.startActivity(intent);

            return true;
        }

        return false;
    }

    private Intent newEmailIntent(String address, String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }
}
