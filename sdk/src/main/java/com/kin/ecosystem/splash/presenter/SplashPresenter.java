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
	private final AuthDataSource authRepository;
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
					isAccountCreated = true;
					navigateToMarketplace();
				} else {
					Logger.log(new Log().withTag(TAG).text("accountStateObserver -> showTryAgainLater"));
					showTryAgainLater();
					stopLoading(true);
					shouldShowError = false;
				}
			}
		}
	};

	private TimerTask timeOutTask;

	private boolean animationEnded = false;
	private boolean isAccountActivated = false;
	private boolean isAccountCreated = false;
	private boolean shouldShowError = true;

	public SplashPresenter(@NonNull AccountManager accountManager,
		@NonNull final AuthDataSource authRepository,
		@NonNull EventLogger eventLogger,
		@NonNull Timer timer) {
		this.accountManager = accountManager;
		this.authRepository = authRepository;
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
		shouldShowError = true;
		isAccountActivated = authRepository.isActivated();
		animateLoading();
		Logger.log(new Log().withTag(TAG).text("getStartedClicked")
			.put("isActivate", isAccountCreated)
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
			isAccountCreated = true;
		}

		if (!isAccountActivated) {
			activateAccount();
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
				shouldShowError = false;
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
		if(shouldShowError) {
			showToast(TRY_AGAIN);
		}
	}

	private void showSomethingWentWrong() {
		if(shouldShowError) {
			showToast(SOMETHING_WENT_WRONG);
		}
	}

	private void animateLoading() {
		if (view != null) {
			view.animateLoading();
		}
	}

	private void activateAccount() {
		authRepository.activateAccount(new KinCallback<Void>() {
			@Override
			public void onResponse(Void response) {
				Logger.log(new Log().withTag(TAG).text("Activate account response"));
				isAccountActivated = true;
				navigateToMarketplace();
			}

			@Override
			public void onFailure(KinEcosystemException exception) {
				Logger.log(new Log().withTag(TAG).put("Activate account fail", exception));
				cancelTimeoutTask();
				showSomethingWentWrong();
				stopLoading(true);
				shouldShowError = false;
			}
		});
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
		if (animationEnded && isAccountActivated && isAccountCreated) {
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
