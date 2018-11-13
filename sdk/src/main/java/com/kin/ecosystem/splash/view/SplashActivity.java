package com.kin.ecosystem.splash.view;

import static com.kin.ecosystem.splash.presenter.ISplashPresenter.SOMETHING_WENT_WRONG;
import static com.kin.ecosystem.splash.presenter.ISplashPresenter.TRY_AGAIN;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.kin.ecosystem.R;
import com.kin.ecosystem.core.accountmanager.AccountManagerImpl;
import com.kin.ecosystem.core.bi.EventLoggerImpl;
import com.kin.ecosystem.main.view.EcosystemActivity;
import com.kin.ecosystem.splash.presenter.ISplashPresenter;
import com.kin.ecosystem.splash.presenter.ISplashPresenter.Message;
import com.kin.ecosystem.splash.presenter.SplashPresenter;
import com.kin.ecosystem.splash.view.SplashScreenButton.LoadAnimationListener;
import java.util.Timer;

public class SplashActivity extends AppCompatActivity implements ISplashView {

	private ISplashPresenter splashPresenter;

	private SplashScreenButton letsGetStartedBtn;
	private TextView loadingText;

	private static final int FADE_DURATION = 250;
	private static final float ZERO_FLOAT = 0f;
	private static final float ONE_FLOAT = 1f;
	private Animation fadeIn = new AlphaAnimation(ZERO_FLOAT, ONE_FLOAT);
	private Animation fadeOutLoading = new AlphaAnimation(ONE_FLOAT, ZERO_FLOAT);

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kinecosystem_activity_splash);
		attachPresenter(new SplashPresenter(AccountManagerImpl.getInstance(),
			EventLoggerImpl.getInstance(), new Timer()));
		initViews();
		initAnimations();
	}

	private void initAnimations() {
		fadeIn.setDuration(FADE_DURATION);
		fadeOutLoading.setDuration(FADE_DURATION);
		fadeOutLoading.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				loadingText.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
	}

	private void initViews() {
		letsGetStartedBtn = findViewById(R.id.lets_get_started);
		loadingText = findViewById(R.id.loading_text);
		setUpBackButton();
		setUpLetsGetStartedButton();
	}

	private void setUpLetsGetStartedButton() {
		letsGetStartedBtn = findViewById(R.id.lets_get_started);
		letsGetStartedBtn.setButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				splashPresenter.getStartedClicked();
			}
		});
		letsGetStartedBtn.setLoadAnimationListener(new LoadAnimationListener() {
			@Override
			public void onAnimationEnd() {
				splashPresenter.onAnimationEnded();
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

	@Override
	public void attachPresenter(SplashPresenter presenter) {
		splashPresenter = presenter;
		splashPresenter.onAttach(this);
	}

	@Override
	public void navigateToMarketPlace() {
		Intent marketplaceIntent = new Intent(this, EcosystemActivity.class);
		startActivity(marketplaceIntent);
		finish();
	}

	@Override
	public void onBackPressed() {
		splashPresenter.backButtonPressed();
	}

	@Override
	public void navigateBack() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.kinecosystem_slide_out_right);
	}

	@Override
	public void animateLoading() {
		letsGetStartedBtn.animateLoading();
		fadeInView(loadingText);
	}

	@Override
	public void stopLoading(final boolean reset) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				letsGetStartedBtn.stopLoading(reset);
				fadeOutLoading();
			}
		});
	}

	private void fadeInView(View view) {
		view.setVisibility(View.VISIBLE);
		view.startAnimation(fadeIn);
	}

	private void fadeOutLoading() {
		loadingText.startAnimation(fadeOutLoading);
	}

	@Override
	public void showToast(@Message final int msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(SplashActivity.this, getMessageResId(msg), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@NonNull
	private @StringRes int getMessageResId(@Message final int msg) {
		switch (msg) {
			case TRY_AGAIN:
				return R.string.kinecosystem_try_again_later;
			default:
			case SOMETHING_WENT_WRONG:
				return R.string.kinecosystem_something_went_wrong;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		splashPresenter.onDetach();
	}
}
