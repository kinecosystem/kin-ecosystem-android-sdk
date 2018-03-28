package com.kin.ecosystem.poll.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.Toast;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.BaseToolbarActivity;
import com.kin.ecosystem.data.order.OrderRepository;
import com.kin.ecosystem.poll.presenter.IPollWebViewPresenter;
import com.kin.ecosystem.poll.presenter.PollWebViewPresenter;
import com.kin.ecosystem.web.EcosystemWebView;

public class PollWebViewActivity extends BaseToolbarActivity implements IPollWebView {

    private static final String EXTRA_JSON_DATA_KEY = "jsondata";
    private static final String EXTRA_OFFER_ID_KEY = "offer_id";

    public static Intent createIntent(final Context context, @NonNull final String jsonData,
        @NonNull final String offerID) {
        final Intent intent = new Intent(context, PollWebViewActivity.class);
        intent.putExtra(EXTRA_JSON_DATA_KEY, jsonData);
        intent.putExtra(EXTRA_OFFER_ID_KEY, offerID);
        return intent;
    }

    private IPollWebViewPresenter pollWebViewPresenter;
    private EcosystemWebView webView;
    private LinearLayout webViewContainer;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_poll;
    }

    @Override
    protected int getTitleRes() {
        return R.string.answer_a_poll;
    }

    @Override
    protected int getNavigationIcon() {
        return R.drawable.ic_close_white_24dp;
    }

    @Override
    protected View.OnClickListener getNavigationClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String pollJsonString = intent.getStringExtra(EXTRA_JSON_DATA_KEY);
        String offerID = intent.getStringExtra(EXTRA_OFFER_ID_KEY);
        attachPresenter(new PollWebViewPresenter(pollJsonString, offerID, OrderRepository.getInstance()));
    }

    @Override
    public void attachPresenter(PollWebViewPresenter presenter) {
        pollWebViewPresenter = presenter;
        pollWebViewPresenter.onAttach(this);
    }

    @Override
    protected void initViews() {
        webView = findViewById(R.id.webview);
        webViewContainer = findViewById(R.id.webview_container);
    }

    @Override
    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PollWebViewActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void loadUrl() {
        webView.setListener(pollWebViewPresenter);
        webView.load();
    }

    @Override
    public void renderJson(@NonNull final String pollJsonString) {
        webView.render(pollJsonString);
    }

    @Override
    public void showToolbar() {
        getToolbar().setVisibility(View.VISIBLE);
    }

    @Override
    public void hideToolbar() {
        getToolbar().setVisibility(View.GONE);
    }

    @Override
    public void close() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webViewContainer.removeView(webView);
                webView.release();
            }
        });
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        close();
        pollWebViewPresenter.onDetach();
    }
}
