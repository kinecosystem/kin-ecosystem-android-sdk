package com.kin.ecosystem.poll.view;

import static com.kin.ecosystem.exception.ClientException.INTERNAL_INCONSISTENCY;

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
import com.kin.ecosystem.exception.ClientException;
import com.kin.ecosystem.poll.presenter.IPollWebViewPresenter;
import com.kin.ecosystem.poll.presenter.PollWebViewPresenter;
import com.kin.ecosystem.util.ErrorUtil;
import com.kin.ecosystem.web.EcosystemWebView;

public class PollWebViewActivity extends BaseToolbarActivity implements IPollWebView {

    public static Intent createIntent(final Context context, @NonNull PollBundle bundle) throws ClientException {
        final Intent intent = new Intent(context, PollWebViewActivity.class);
        intent.putExtras(bundle.build());
        return intent;
    }

    private IPollWebViewPresenter pollWebViewPresenter;
    private EcosystemWebView webView;
    private LinearLayout webViewContainer;

    @Override
    protected int getLayoutRes() {
        return R.layout.kinecosystem_activity_poll;
    }

    @Override
    protected int getTitleRes() {
        return EMPTY_TITLE;
    }

    @Override
    protected int getNavigationIcon() {
        return R.drawable.kinecosystem_ic_close_white_24dp;
    }

    @Override
    protected View.OnClickListener getNavigationClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pollWebViewPresenter.closeClicked();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PollBundle pollBundle = new PollBundle(getIntent().getExtras());
        attachPresenter(
            new PollWebViewPresenter(pollBundle.getJsonData(), pollBundle.getOfferID(), pollBundle.getTitle(),
                OrderRepository.getInstance()));
    }

    @Override
    public void onBackPressed() {
        pollWebViewPresenter.closeClicked();
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getToolbar().setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void hideToolbar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getToolbar().setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void setTitle(String title) {
        getToolbar().setTitle(title);
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

    public static class PollBundle {

        private Bundle bundle;

        private static final int FIELD_COUNT = 3;

        private static final String EXTRA_JSON_DATA_KEY = "jsondata";
        private static final String EXTRA_OFFER_ID_KEY = "offer_id";
        private static final String EXTRA_TITLE_KEY = "title";

        public PollBundle() {
            this.bundle = new Bundle();
        }

        public PollBundle(Bundle bundle) {
            this.bundle = bundle;
        }

        public PollBundle setJsonData(String jsonData) {
            this.bundle.putString(EXTRA_JSON_DATA_KEY, jsonData);
            return this;
        }

        public String getJsonData() {
            return bundle.getString(EXTRA_JSON_DATA_KEY);
        }

        public PollBundle setOfferID(String offerID) {
            this.bundle.putString(EXTRA_OFFER_ID_KEY, offerID);
            return this;
        }

        public String getOfferID() {
            return bundle.getString(EXTRA_OFFER_ID_KEY);
        }

        public PollBundle setTitle(String title) {
            this.bundle.putString(EXTRA_TITLE_KEY, title);
            return this;
        }

        public String getTitle() {
            return bundle.getString(EXTRA_TITLE_KEY);
        }

        public Bundle build() throws ClientException {
            if (bundle.size() < FIELD_COUNT) {
                throw ErrorUtil.getClientException(INTERNAL_INCONSISTENCY, new IllegalArgumentException("You must specified all the fields."));
            }
            return bundle;
        }

    }
}
