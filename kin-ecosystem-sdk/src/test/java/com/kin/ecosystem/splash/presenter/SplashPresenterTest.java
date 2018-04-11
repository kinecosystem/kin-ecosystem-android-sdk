package com.kin.ecosystem.splash.presenter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kin.ecosystem.Callback;
import com.kin.ecosystem.data.auth.AuthRepository;
import com.kin.ecosystem.splash.view.ISplashView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class SplashPresenterTest {

    @Captor
    private ArgumentCaptor<Callback<Void>> activateCallback;

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
    }

    @Test
    public void getStartedClicked() throws Exception {
        splashPresenter.getStartedClicked();
        
        verify(splashView, times(1)).animateLoading();

    }

    @Test
    public void testBackButtonPressed_NavigateBack() throws Exception {
        splashPresenter.backButtonPressed();
        verify(splashView, times(1)).navigateBack();
    }

    @Test
    public void onAnimationEnded() throws Exception {
    }

}