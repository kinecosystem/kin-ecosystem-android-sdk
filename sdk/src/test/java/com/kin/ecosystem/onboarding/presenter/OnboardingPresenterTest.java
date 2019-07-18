package com.kin.ecosystem.onboarding.presenter;

import static com.kin.ecosystem.core.accountmanager.AccountManager.CREATION_COMPLETED;
import static com.kin.ecosystem.core.accountmanager.AccountManager.ERROR;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Bundle;
import com.kin.ecosystem.EcosystemExperience;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.base.CustomAnimation;
import com.kin.ecosystem.core.accountmanager.AccountManager;

import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.data.auth.AuthDataSource;
import com.kin.ecosystem.core.data.settings.SettingsDataSource;
import com.kin.ecosystem.main.INavigator;
import com.kin.ecosystem.onboarding.view.IOnboardingView;
import java.util.Timer;
import java.util.TimerTask;
import kin.ecosystem.test.base.BaseTestClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class OnboardingPresenterTest extends BaseTestClass {

	@Mock
	private AccountManager accountManager;

	@Mock
	private AuthDataSource authDataSource;

	@Mock
	private SettingsDataSource settingsDataSource;

	@Mock
	private INavigator navigator;

	@Mock
	private EventLogger eventLogger;

	@Mock
	private Timer timer;

	@Mock
	private IOnboardingView splashView;

	@Mock
	private Bundle extras;

	private OnboardingPresenterImpl splashPresenter;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);
		when(authDataSource.getEcosystemUserID()).thenReturn("some_id");
		when(extras.getInt(Kin.KEY_ECOSYSTEM_EXPERIENCE, EcosystemExperience.MARKETPLACE)).thenReturn(EcosystemExperience.MARKETPLACE);
		splashPresenter = new OnboardingPresenterImpl(accountManager, authDataSource, settingsDataSource, navigator, eventLogger, timer, extras);
		splashPresenter.onAttach(splashView);
		assertNotNull(splashPresenter.getView());
	}

	@After
	public void tearDown() throws Exception {
		splashPresenter.onDetach();
		assertNull(splashPresenter.getView());
	}

	@Test
	public void getStartedClicked_AccountCreated_NavigateMP() {
		when(accountManager.isAccountCreated()).thenReturn(true);
		splashPresenter.getStartedClicked();
		verify(navigator).navigateToMarketplace(any(CustomAnimation.class));
	}


	@Test
	public void getStartedClicked_AccountNotCreated_ShouldNOT_NavigateToMP() {
		when(accountManager.isAccountCreated()).thenReturn(false);
		splashPresenter.getStartedClicked();
		verify(navigator, times(0)).navigateToMarketplace(any(CustomAnimation.class));

	}

	@Test
	public void getStartedClicked_AccountNotCreated_Timeout_ShouldNOT_NavigateToMP() {
		ArgumentCaptor<TimerTask> timeoutTask = ArgumentCaptor.forClass(TimerTask.class);
		ArgumentCaptor<Observer<Integer>> accountStateObserver = ArgumentCaptor.forClass(Observer.class);
		when(accountManager.isAccountCreated()).thenReturn(false);

		splashPresenter.getStartedClicked();
		verify(timer).schedule(timeoutTask.capture(), anyLong());
		verify(accountManager).addAccountStateObserver(accountStateObserver.capture());
		verify(splashView).animateLoading();

		timeoutTask.getValue().run();
		verify(splashView).stopLoading(true);
		verify(splashView).showToast(OnboardingPresenter.Message.TRY_AGAIN);
		verify(navigator, times(0)).navigateToMarketplace(any(CustomAnimation.class));
		verify(accountManager).removeAccountStateObserver(accountStateObserver.getValue());
	}

	@Test
	public void getStartedClicked_AccountNotCreated_ObserverOnChange_AccountCreated_NavigateToMP() {
		ArgumentCaptor<TimerTask> timeoutTask = ArgumentCaptor.forClass(TimerTask.class);
		ArgumentCaptor<Observer<Integer>> accountStateObserver = ArgumentCaptor.forClass(Observer.class);
		when(accountManager.isAccountCreated()).thenReturn(false);

		splashPresenter.getStartedClicked();
		verify(timer).schedule(timeoutTask.capture(), anyLong());
		verify(accountManager).addAccountStateObserver(accountStateObserver.capture());
		verify(splashView).animateLoading();


		verify(navigator, times(0)).navigateToMarketplace(any(CustomAnimation.class));

		when(accountManager.isAccountCreated()).thenReturn(true);
		accountStateObserver.getValue().onChanged(CREATION_COMPLETED);

		verify(accountManager).removeAccountStateObserver(accountStateObserver.getValue());
		verify(timer, times(2)).purge();
		verify(navigator).navigateToMarketplace(any(CustomAnimation.class));
	}

	@Test
	public void getStartedClicked_AccountStateERROR_RetryCreation() {
		when(accountManager.isAccountCreated()).thenReturn(false);
		when(accountManager.getAccountState()).thenReturn(ERROR);

		splashPresenter.getStartedClicked();

		verify(accountManager).addAccountStateObserver(any(Observer.class));
		verify(accountManager).retry();
		verify(splashView).animateLoading();
	}

	@Test
	public void getStartedClicked_AccountNotCreated_Retry_ObserverOnChange_ERROR_CancelAndShowTryAgain() {
		ArgumentCaptor<Observer<Integer>> accountStateObserver = ArgumentCaptor.forClass(Observer.class);
		when(accountManager.isAccountCreated()).thenReturn(false);

		splashPresenter.getStartedClicked();
		verify(timer).schedule(any(TimerTask.class), anyLong());
		verify(accountManager).addAccountStateObserver(accountStateObserver.capture());
		verify(splashView).animateLoading();
		verify(navigator, times(0)).navigateToMarketplace(any(CustomAnimation.class));

		accountStateObserver.getValue().onChanged(ERROR);
		verify(accountManager).removeAccountStateObserver(accountStateObserver.getValue());
		verify(timer, times(2)).purge();
		verify(splashView).showToast(OnboardingPresenter.Message.TRY_AGAIN);
		verify(navigator, times(0)).navigateToMarketplace(any(CustomAnimation.class));
	}

	@Test
	public void closeButtonPressed_NavigateBack() {
		splashPresenter.closeButtonPressed();
		verify(navigator).close();
	}
}