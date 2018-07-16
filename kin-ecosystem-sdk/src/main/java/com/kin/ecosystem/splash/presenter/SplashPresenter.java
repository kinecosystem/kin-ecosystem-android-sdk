package com.kin.ecosystem.splash.presenter;

import static com.kin.ecosystem.AccountManager.CREATION_COMPLETED;

import android.support.annotation.NonNull;
import com.kin.ecosystem.AccountManager;
import com.kin.ecosystem.KinCallback;
import com.kin.ecosystem.Log;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.bi.EventLogger;
import com.kin.ecosystem.bi.events.BackButtonOnWelcomeScreenPageTapped;
import com.kin.ecosystem.bi.events.WelcomeScreenButtonTapped;
import com.kin.ecosystem.bi.events.WelcomeScreenPageViewed;
import com.kin.ecosystem.data.auth.AuthDataSource;
import com.kin.ecosystem.exception.KinEcosystemException;
import com.kin.ecosystem.splash.view.ISplashView;
import java.util.Timer;
import java.util.TimerTask;
import kin.core.AccountStatus;

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
		public void onChanged(@AccountStatus Integer value) {
			new Log().withTag(TAG).put("accountStateObserver", value);
			if (value == CREATION_COMPLETED) {
				isAccountCreated = true;
				removeAccountStateObserver();
				cancelTimeoutTask();
				navigateToMarketplace();
			}
		}
	};

	private TimerTask timeOutTask = new TimerTask() {
		@Override
		public void run() {
			new Log().withTag(TAG).text("Account creation time out").log();
			stopLoading(true);
			showTryAgainLater();
			removeAccountStateObserver();
		}
	};

	private boolean animationEnded = false;
	private boolean isAccountActivated = false;
	private boolean isAccountCreated = false;

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
		new Log().withTag(TAG).text("removeAccountStateObserver").log();
		accountManager.removeAccountStateObserver(accountStateObserver);
	}

	@Override
	public void getStartedClicked() {
		eventLogger.send(WelcomeScreenButtonTapped.create());
		isAccountActivated = authRepository.isActivated();
		animateLoading();
		new Log().withTag(TAG).text("getStartedClicked")
			.put("isActivate", isAccountCreated)
			.put("accountState", accountManager.getAccountState())
			.log();

		if (accountManager.getAccountState() != CREATION_COMPLETED) {
			new Log().withTag(TAG).text("addAccountStateObserver").log();
			accountManager.addAccountStateObserver(accountStateObserver);
			startCreationTimeout(TIME_OUT_DURATION);
		} else {
			isAccountCreated = true;
		}

		if (!isAccountActivated) {
			activateAccount();
		}
	}

	private void cancelTimeoutTask() {
		timeOutTask.cancel();
		timer.purge();
	}

	private void startCreationTimeout(final int sec) {
		timer.schedule(timeOutTask, sec * SEC_IN_MILI);
	}

	private void showTryAgainLater() {
		showToast(TRY_AGAIN);
	}

	private void showSomethingWentWrong() {
		showToast(SOMETHING_WENT_WRONG);
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
				new Log().withTag(TAG).text("Activate account response").log();
				isAccountActivated = true;
				navigateToMarketplace();
			}

			@Override
			public void onFailure(KinEcosystemException exception) {
				new Log().withTag(TAG).put("Activate account fail", exception).log();
				showSomethingWentWrong();
				stopLoading(true);
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

	private synchronized void navigateToMarketplace() {
		if (animationEnded && isAccountActivated && isAccountCreated) {
			if (view != null) {
				new Log().withTag(TAG).text("navigateToMarketPlace").log();
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
