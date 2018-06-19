package com.kin.ecosystem.splash.presenter;

import android.support.annotation.NonNull;
import com.kin.ecosystem.KinCallback;
import com.kin.ecosystem.bi.EventLogger;
import com.kin.ecosystem.bi.events.BackButtonOnWelcomeScreenPageTapped;
import com.kin.ecosystem.data.Callback;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.bi.events.WelcomeScreenButtonTapped;
import com.kin.ecosystem.bi.events.WelcomeScreenPageViewed;
import com.kin.ecosystem.data.auth.AuthDataSource;
import com.kin.ecosystem.exception.KinEcosystemException;
import com.kin.ecosystem.splash.view.ISplashView;

public class SplashPresenter extends BasePresenter<ISplashView> implements ISplashPresenter {

    private final AuthDataSource authRepository;
    private final EventLogger eventLogger;

    private boolean animationEnded = false;
    private boolean confirmedSucceed = false;

    public SplashPresenter(@NonNull final AuthDataSource authRepository, @NonNull EventLogger eventLogger) {
        this.authRepository = authRepository;
        this.eventLogger = eventLogger;
        this.eventLogger.send(WelcomeScreenPageViewed.create());

    }

    @Override
    public void getStartedClicked() {
    	eventLogger.send(WelcomeScreenButtonTapped.create());
        animateLoading();
        activateAccount();
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
                confirmedSucceed = true;
                navigateToMarketplace();
            }

            @Override
            public void onFailure(KinEcosystemException exception) {
                showToast("Oops something went wrong...");
                stopLoading(true);
            }
        });
    }

    private void stopLoading(boolean reset) {
        if (view != null) {
            view.stopLoading(reset);
        }
    }

    private void showToast(String msg) {
        if (view != null) {
            view.showToast(msg);
        }
    }

    private void navigateToMarketplace() {
        if (animationEnded && confirmedSucceed) {
            if (view != null) {
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
