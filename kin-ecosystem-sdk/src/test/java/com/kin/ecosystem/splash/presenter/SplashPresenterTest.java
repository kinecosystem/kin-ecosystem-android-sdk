package com.kin.ecosystem.splash.presenter;

import static kin.ecosystem.core.accountmanager.AccountManager.CREATION_COMPLETED;
import static kin.ecosystem.core.accountmanager.AccountManager.ERROR;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import kin.ecosystem.core.accountmanager.AccountManager;
import kin.ecosystem.core.BaseTestClass;
import kin.ecosystem.common.KinCallback;
import kin.ecosystem.common.Observer;
import kin.ecosystem.core.bi.EventLogger;
import kin.ecosystem.core.bi.events.BackButtonOnWelcomeScreenPageTapped;
import kin.ecosystem.core.bi.events.WelcomeScreenButtonTapped;
import kin.ecosystem.core.bi.events.WelcomeScreenPageViewed;
import kin.ecosystem.core.data.auth.AuthDataSource;
import com.kin.ecosystem.splash.view.ISplashView;
import java.util.Timer;
import java.util.TimerTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@RunWith(JUnit4.class)
public class SplashPresenterTest extends BaseTestClass {

	@Mock
	private AccountManager accountManager;

	@Mock
	private AuthDataSource authRepository;

	@Mock
	private EventLogger eventLogger;

	@Mock
	private Timer timer;

	@Mock
	private ISplashView splashView;

	@Captor
	private ArgumentCaptor<KinCallback<Void>> activateCapture;

	private SplashPresenter splashPresenter;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);
		splashPresenter = new SplashPresenter(accountManager, authRepository, eventLogger, timer);
		splashPresenter.onAttach(splashView);
		assertNotNull(splashPresenter.getView());
		verify(eventLogger).send(any(WelcomeScreenPageViewed.class));
	}

	@After
	public void tearDown() throws Exception {
		splashPresenter.onDetach();
		assertNull(splashPresenter.getView());
	}

	@Test
	public void getStartedClicked_AccountCreated_AnimationEndedNavigateMP() {
		when(accountManager.isAccountCreated()).thenReturn(true);
		splashPresenter.getStartedClicked();
		verify(eventLogger).send(any(WelcomeScreenButtonTapped.class));
		verify(authRepository).activateAccount(activateCapture.capture());

		activateCapture.getValue().onResponse(null);
		verify(splashView, times(0)).navigateToMarketPlace();
		verify(splashView, times(1)).animateLoading();

		splashPresenter.onAnimationEnded();
		verify(splashView, times(1)).navigateToMarketPlace();
	}

	@Test
	public void getStartedClicked_AccountCreated_CallbackSuccessNavigateMP() {
		when(accountManager.isAccountCreated()).thenReturn(true);
		splashPresenter.getStartedClicked();
		verify(authRepository).activateAccount(activateCapture.capture());
		verify(splashView, times(1)).animateLoading();

		splashPresenter.onAnimationEnded();
		verify(splashView, times(0)).navigateToMarketPlace();

		activateCapture.getValue().onResponse(null);
		verify(splashView, times(1)).navigateToMarketPlace();
	}

	@Test
	public void getStartedClicked_AccountCreated_CallbackFailed_Reset() {
		when(accountManager.isAccountCreated()).thenReturn(true);
		splashPresenter.getStartedClicked();
		verify(authRepository).activateAccount(activateCapture.capture());

		splashPresenter.onAnimationEnded();
		activateCapture.getValue().onFailure(null);

		verify(splashView, times(1)).animateLoading();
		verify(splashView, times(0)).navigateToMarketPlace();

		verify(splashView, times(1)).showToast(ISplashPresenter.SOMETHING_WENT_WRONG);
		verify(splashView, times(1)).stopLoading(true);
	}

	@Test
	public void getStartedClicked_AccountNotCreated_NotNavigateToMP() {
		when(accountManager.isAccountCreated()).thenReturn(false);
		splashPresenter.getStartedClicked();

		verify(authRepository).activateAccount(activateCapture.capture());
		splashPresenter.onAnimationEnded();
		activateCapture.getValue().onResponse(null);

		verify(splashView, times(0)).navigateToMarketPlace();
	}

	@Test
	public void getStartedClicked_AccountNotCreated_Timeout_NotNavigateToMP() {
		ArgumentCaptor<TimerTask> timeoutTask = ArgumentCaptor.forClass(TimerTask.class);
		ArgumentCaptor<Observer<Integer>> accountStateObserver = ArgumentCaptor.forClass(Observer.class);
		when(accountManager.isAccountCreated()).thenReturn(false);

		splashPresenter.getStartedClicked();
		verify(timer).schedule(timeoutTask.capture(), anyLong());
		verify(authRepository).activateAccount(activateCapture.capture());
		verify(accountManager).addAccountStateObserver(accountStateObserver.capture());
		verify(splashView, times(1)).animateLoading();

		splashPresenter.onAnimationEnded();
		activateCapture.getValue().onResponse(null);

		timeoutTask.getValue().run();
		verify(splashView).stopLoading(true);
		verify(splashView).showToast(ISplashPresenter.TRY_AGAIN);
		verify(splashView, times(0)).navigateToMarketPlace();
		verify(accountManager).removeAccountStateObserver(accountStateObserver.getValue());
	}

	@Test
	public void getStartedClicked_AccountNotCreated_ObserverOnChange_AccountCreated_NavigateToMP() {
		ArgumentCaptor<TimerTask> timeoutTask = ArgumentCaptor.forClass(TimerTask.class);
		ArgumentCaptor<Observer<Integer>> accountStateObserver = ArgumentCaptor.forClass(Observer.class);
		when(accountManager.isAccountCreated()).thenReturn(false);

		splashPresenter.getStartedClicked();
		verify(timer).schedule(timeoutTask.capture(), anyLong());
		verify(authRepository).activateAccount(activateCapture.capture());
		verify(accountManager).addAccountStateObserver(accountStateObserver.capture());
		verify(splashView, times(1)).animateLoading();

		splashPresenter.onAnimationEnded();
		activateCapture.getValue().onResponse(null);
		verify(splashView, times(0)).navigateToMarketPlace();

		accountStateObserver.getValue().onChanged(CREATION_COMPLETED);
		verify(accountManager).removeAccountStateObserver(accountStateObserver.getValue());
		verify(timer, times(2)).purge();
		verify(splashView).navigateToMarketPlace();
	}

	@Test
	public void getStartedClicked_AccountStateERROR_RetryCreation() {
		when(accountManager.isAccountCreated()).thenReturn(false);
		when(accountManager.getAccountState()).thenReturn(ERROR);

		splashPresenter.getStartedClicked();

		verify(accountManager).addAccountStateObserver(any(Observer.class));
		verify(accountManager).retry();

		verify(authRepository).activateAccount(activateCapture.capture());
		verify(splashView, times(1)).animateLoading();
	}

	@Test
	public void getStartedClicked_AccountNotCreated_Retry_ObserverOnChange_ERROR_CancelAndShowTryAgain() {
		ArgumentCaptor<Observer<Integer>> accountStateObserver = ArgumentCaptor.forClass(Observer.class);
		when(accountManager.isAccountCreated()).thenReturn(false);

		splashPresenter.getStartedClicked();
		verify(timer).schedule(any(TimerTask.class), anyLong());
		verify(authRepository).activateAccount(activateCapture.capture());
		verify(accountManager).addAccountStateObserver(accountStateObserver.capture());
		verify(splashView, times(1)).animateLoading();

		splashPresenter.onAnimationEnded();
		activateCapture.getValue().onResponse(null);
		verify(splashView, times(0)).navigateToMarketPlace();

		accountStateObserver.getValue().onChanged(ERROR);
		verify(accountManager).removeAccountStateObserver(accountStateObserver.getValue());
		verify(timer, times(2)).purge();
		verify(splashView).showToast(ISplashPresenter.TRY_AGAIN);
		verify(splashView, times(0)).navigateToMarketPlace();
	}

	@Test
	public void backButtonPressed_NavigateBack() {
		splashPresenter.backButtonPressed();
		verify(eventLogger).send(any(BackButtonOnWelcomeScreenPageTapped.class));
		verify(splashView, times(1)).navigateBack();
	}
}