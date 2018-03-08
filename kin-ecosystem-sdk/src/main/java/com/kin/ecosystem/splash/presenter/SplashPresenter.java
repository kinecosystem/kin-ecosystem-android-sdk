package com.kin.ecosystem.splash.presenter;

import android.support.annotation.NonNull;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.data.auth.AuthDataSource;
import com.kin.ecosystem.data.user.UserInfoDataSource;
import com.kin.ecosystem.splash.view.ISplashView;

public class SplashPresenter extends BasePresenter<ISplashView> implements ISplashPresenter {

    private final UserInfoDataSource userInfoRepository;
    private final AuthDataSource authRepository;

    private boolean animationEnded = false;
    private boolean confirmedSucceed = false;

    public SplashPresenter(@NonNull final UserInfoDataSource userInfoRepository,
        @NonNull final AuthDataSource authRepository) {
        this.userInfoRepository = userInfoRepository;
        this.authRepository = authRepository;
    }

    @Override
    public void getStartedClicked() {
        animateLoading();
        activateAccount();
    }

    private void animateLoading() {
        if(view != null) {
            view.animateLoading();
        }
    }

    private void activateAccount() {
        authRepository.activateAccount(new Callback<Void>() {
            @Override
            public void onResponse(Void response) {
                userInfoRepository.setConfirmedTOS(true);
                confirmedSucceed = true;
                navigateToMarketplace();
            }

            @Override
            public void onFailure(Throwable t) {
                showToast("Oops something went wrong...");
                stopLoading(true);
            }
        });
    }

    private void stopLoading(boolean reset) {
        if(view != null) {
            view.stopLoading(reset);
        }
    }

    private void showToast(String msg) {
        if (view != null) {
            view.showToast(msg);
        }
    }

    private void navigateToMarketplace() {
        if(animationEnded && confirmedSucceed) {
            if (view != null) {
                view.navigateToMarketPlace();
            }
        }
    }

    @Override
    public void backButtonPressed() {
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
