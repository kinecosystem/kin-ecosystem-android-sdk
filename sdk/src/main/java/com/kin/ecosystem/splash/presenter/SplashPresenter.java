package com.kin.ecosystem.splash.presenter;

import static com.kin.ecosystem.core.accountmanager.AccountManager.CREATION_COMPLETED;
import static com.kin.ecosystem.core.accountmanager.AccountManager.ERROR;

import android.support.annotation.NonNull;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;
import com.kin.ecosystem.core.accountmanager.AccountManager;
import com.kin.ecosystem.core.accountmanager.AccountManager.AccountState;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.bi.events.BackButtonOnWelcomeScreenPageTapped;
import com.kin.ecosystem.core.bi.events.WelcomeScreenButtonTapped;
import com.kin.ecosystem.core.bi.events.WelcomeScreenPageViewed;
import com.kin.ecosystem.core.data.auth.AuthDataSource;
import com.kin.ecosystem.splash.view.ISplashView;
import java.util.Timer;
import java.util.TimerTask;


public class SplashPresenter extends BasePresenter<ISplashView> implements ISplashPresenter {

	private static final String TAG = SplashPresenter.class.getSimpleName();

	private static final int SEC_IN_MILI = 1000;
	private static final int TIME_OUT_DURATION = 20;

	private final AccountManager accountManager;
	private final EventLogger eventLogger;
	private final Timer timer;

	private final Observer<Integer> accountStateObserver = new Observer<Integer>() {
		@Override
		public void onChanged(@AccountState Integer value) {
			Logger.log(new Log().withTag(TAG).put("accountStateObserver", value));
			if (value == CREATION_COMPLETED || value == ERROR) {
				removeAccountStateObserver();
				cancelTimeoutTask();

				if (value == CREATION_COMPLETED) {
					navigateToMarketplace();
				} else {
					Logger.log(new Log().withTag(TAG).text("accountStateObserver -> showTryAgainLater"));
					showTryAgainLater();
					stopLoading(true);
				}
			}
		}
	};

	private TimerTask timeOutTask;

	private boolean animationEnded = false;

	public SplashPresenter(@NonNull AccountManager accountManager,
		@NonNull EventLogger eventLogger,
		@NonNull Timer timer) {
		this.accountManager = accountManager;
		this.eventLogger = eventLogger;
		this.timer = timer;
	}

	@Override
	public void onAttach(ISplashView view) {
		super.onAttach(view);
		this.eventLogger.send(WelcomeScreenPageViewed.create());
	}

	@Override
	public void onDetach() {
		super.onDetach();
		removeAccountStateObserver();
		cancelTimeoutTask();
	}

	private void removeAccountStateObserver() {
		Logger.log(new Log().withTag(TAG).text("removeAccountStateObserver"));
		accountManager.removeAccountStateObserver(accountStateObserver);
	}

	@Override
	public void getStartedClicked() {
		eventLogger.send(WelcomeScreenButtonTapped.create());
		animateLoading();
		Logger.log(new Log().withTag(TAG).text("getStartedClicked")
			.put("accountState", accountManager.getAccountState()));

		if (!accountManager.isAccountCreated()) {
			Logger.log(new Log().withTag(TAG).text("addAccountStateObserver"));
			startCreationTimeout(TIME_OUT_DURATION);
			accountManager.addAccountStateObserver(accountStateObserver);

			if (accountManager.getAccountState() == AccountManager.ERROR) {
				Logger.log(new Log().withTag(TAG).text("accountManager -> retry"));
				accountManager.retry();
			}
		} else {
			navigateToMarketplace();
		}
	}

	private TimerTask createTimeOutTimerTask() {
		return new TimerTask() {
			@Override
			public void run() {
				Logger.log(new Log().withTag(TAG).text("Account creation time out"));
				stopLoading(true);
				showTryAgainLater();
				removeAccountStateObserver();
			}
		};
	}

	private void cancelTimeoutTask() {
		if (timeOutTask != null) {
			timeOutTask.cancel();
			timeOutTask = null;
		}
		timer.purge();
	}

	private void startCreationTimeout(final int sec) {
		cancelTimeoutTask();
		timeOutTask = createTimeOutTimerTask();
		timer.schedule(timeOutTask, sec * SEC_IN_MILI);
	}

	private void showTryAgainLater() {
			showToast(TRY_AGAIN);
	}

	private void animateLoading() {
		if (view != null) {
			view.animateLoading();
		}
	}


	private void stopLoading(boolean reset) {
		if (view != null) {
			view.stopLoading(reset);
		}
	}

	private void showToast(@Message final int msg) {
		if (view != null) {
			view.showToast(msg);
		}
	}

	private void navigateToMarketplace() {
		if (accountManager.isAccountCreated() && animationEnded) {
			if (view != null) {
				Logger.log(new Log().withTag(TAG).text("navigateToMarketPlace"));
				view.navigateToMarketPlace();
			}
		}
	}

	@Override
	public void backButtonPressed() {
		eventLogger.send(BackButtonOnWelcomeScreenPageTapped.create());
		if (view != null) {
			view.navigateBack();
		}
	}

	@Override
	public void onAnimationEnded() {
		animationEnded = true;
		navigateToMarketplace();
	}
}
