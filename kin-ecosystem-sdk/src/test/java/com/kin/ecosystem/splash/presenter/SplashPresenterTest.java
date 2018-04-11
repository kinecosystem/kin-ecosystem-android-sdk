package com.kin.ecosystem.splash.presenter;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.kin.ecosystem.Callback;
import com.kin.ecosystem.data.auth.AuthRepository;
import com.kin.ecosystem.splash.view.ISplashView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class SplashPresenterTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private ISplashView splashView;

    private SplashPresenter splashPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        splashPresenter = new SplashPresenter(authRepository);
        splashPresenter.onAttach(splashView);
        assertNotNull(splashPresenter.getView());
    }

    @After
    public void tearDown() throws Exception {
        splashPresenter.onDetach();
        assertNull(splashPresenter.getView());
    }

    @Test
    public void getStartedClicked_AnimationEndedNavigateMP() throws Exception {
        Callback<Void> activateCallback = splashPresenter.getActivateAccountCallback();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<Void> callback = invocation.getArgument(0);
                callback.onResponse(null);
                return null;
            }
        }).when(authRepository).activateAccount(activateCallback);

        splashPresenter.getStartedClicked();
        verify(splashView, times(0)).navigateToMarketPlace();

        splashPresenter.onAnimationEnded();
        verify(splashView, times(1)).animateLoading();
        verify(splashView, times(1)).navigateToMarketPlace();
    }

    @Test
    public void getStartedClicked_CallbackSuccessNavigateMP() throws Exception {
        Callback<Void> activateCallback = splashPresenter.getActivateAccountCallback();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<Void> callback = invocation.getArgument(0);
                callback.onResponse(null);
                return null;
            }
        }).when(authRepository).activateAccount(activateCallback);

        splashPresenter.onAnimationEnded();
        verify(splashView, times(0)).navigateToMarketPlace();

        splashPresenter.getStartedClicked();
        verify(splashView, times(1)).animateLoading();
        verify(splashView, times(1)).navigateToMarketPlace();
    }

    @Test
    public void getStartedClicked_CallbackFailed_Reset() throws Exception {
        Callback<Void> activateCallback = splashPresenter.getActivateAccountCallback();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<Void> callback = invocation.getArgument(0);
                callback.onFailure(null);
                return null;
            }
        }).when(authRepository).activateAccount(activateCallback);

        splashPresenter.getStartedClicked();
        splashPresenter.onAnimationEnded();

        verify(splashView, times(1)).animateLoading();
        verify(splashView, times(0)).navigateToMarketPlace();

        verify(splashView, times(1)).showToast("Oops something went wrong...");
        verify(splashView, times(1)).stopLoading(true);
    }

    @Test
    public void backButtonPressed_NavigateBack() throws Exception {
        splashPresenter.backButtonPressed();
        verify(splashView, times(1)).navigateBack();
    }
}