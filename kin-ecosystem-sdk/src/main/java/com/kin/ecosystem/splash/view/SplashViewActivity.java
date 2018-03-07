package com.kin.ecosystem.splash.view;

import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.kin.ecosystem.R;
import com.kin.ecosystem.data.auth.AuthRepository;
import com.kin.ecosystem.data.user.UserInfoRepository;
import com.kin.ecosystem.marketplace.view.MarketplaceActivity;
import com.kin.ecosystem.splash.presenter.ISplashPresenter;
import com.kin.ecosystem.splash.presenter.SplashPresenter;

public class SplashViewActivity extends AppCompatActivity implements ISplashView {

    private ISplashPresenter splashPresenter;

    private Button letsGetStartedBtn;
    private ProgressBar loader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        attachPresenter(new SplashPresenter(UserInfoRepository.getInstance(), AuthRepository.getInstance()));
        initViews();
    }

    private void initViews() {
        letsGetStartedBtn = findViewById(R.id.lets_get_started);
        setUpLoader();
        setUpTosText();
        setUpBackButton();
        setUpLetsGetStartedButton();
    }

    private void setUpLoader() {
        loader = findViewById(R.id.loader);
        int color = ContextCompat.getColor(this, R.color.cyan);
        loader.getIndeterminateDrawable().setColorFilter(color, Mode.SRC_IN);
    }

    private void setUpLetsGetStartedButton() {
        final Button backButton = findViewById(R.id.lets_get_started);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                splashPresenter.getStartedClicked();
            }
        });
    }

    private void setUpBackButton() {
        final ImageView backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                splashPresenter.backButtonPressed();
            }
        });
    }

    private void setUpTosText() {
        final TextView tosText = findViewById(R.id.tos_text);
        tosText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void attachPresenter(SplashPresenter presenter) {
        splashPresenter = presenter;
        splashPresenter.onAttach(this);
    }

    @Override
    public void navigateToMarketPlace() {
        Intent marketplaceIntent = new Intent(this, MarketplaceActivity.class);
        startActivity(marketplaceIntent);
        finish();
    }

    @Override
    public void navigateBack() {
        onBackPressed();
    }

    @Override
    public void setLetsGetStartedVisibility(boolean isVisible) {
        letsGetStartedBtn.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setLoaderVisibility(boolean isVisible) {
        loader.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SplashViewActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        splashPresenter.onDetach();
    }
}
