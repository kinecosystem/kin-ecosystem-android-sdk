package com.kin.ecosystem.poll.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.kin.ecosystem.R;
import com.kin.ecosystem.base.BaseToolbarActivity;
import com.kin.ecosystem.marketplace.view.MarketplaceActivity;
import com.kin.ecosystem.web.EcosystemWebPageListener;
import com.kin.ecosystem.web.EcosystemWebView;

public class PollActivity extends BaseToolbarActivity implements EcosystemWebPageListener {
    private EcosystemWebView webView;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_poll;
    }

    @Override
    protected int getTitleRes() {
        return R.string.poll;
    }

    @Override
    protected int getNavigationIcon() {
        return R.drawable.ic_back;
    }

    @Override
    protected View.OnClickListener getNavigationClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    @Override
    protected void initViews() {
        this.webView = findViewById(R.id.webview);

        this.webView.setListener(this);
        this.webView.load();
    }

    @Override
    public void onPageLoaded() {
        String pollJsonString = getIntent().getStringExtra("jsondata");
        webView.render(pollJsonString);
    }

    @Override
    public void onPageCancel() {
        // not supported yet
    }

    @Override
    public void onPageResult(String result) {
        Log.d("PollActivity", "received result from webview: " + result);
        Intent transactionHistory = new Intent(this, MarketplaceActivity.class);
        navigateToActivity(transactionHistory);
    }
}
