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

    public SplashPresenter(@NonNull final UserInfoDataSource userInfoRepository,
        @NonNull final AuthDataSource authRepository) {
        this.userInfoRepository = userInfoRepository;
        this.authRepository = authRepository;
    }

    @Override
    public void getStartedClicked() {
        setLetsGetStartedVisibility(false);
        setLoaderVisibility(true);
        activateAccount();
    }

    private void activateAccount() {
        authRepository.activateAccount(new Callback<Void>() {
            @Override
            public void onResponse(Void response) {
                userInfoRepository.setConfirmedTOS(true);
                setLoaderVisibility(false);
                navigateToMarketplace();
            }

            @Override
            public void onFailure(Throwable t) {
                setLoaderVisibility(false);
                setLetsGetStartedVisibility(true);
                showToast("Oops something went wrong...");
            }
        });
    }

    private void setLetsGetStartedVisibility(boolean isVisible) {
        if (view != null) {
            view.setLetsGetStartedVisibility(isVisible);
        }
    }

    private void setLoaderVisibility(boolean isVisible) {
        if (view != null) {
            view.setLoaderVisibility(isVisible);
        }
    }

    private void showToast(String msg) {
        if (view != null) {
            view.showToast(msg);
        }
    }

    private void navigateToMarketplace() {
        if (view != null) {
            view.navigateToMarketPlace();
        }
    }

    @Override
    public void backButtonPressed() {
        if (view != null) {
            view.navigateBack();
        }
    }
}
