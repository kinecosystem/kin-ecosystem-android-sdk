package com.kin.ecosystem.splash.view;

import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.splash.presenter.SplashPresenter;

public interface ISplashView extends IBaseView<SplashPresenter> {

    void navigateToMarketPlace();

    void navigateBack();

    void setLetsGetStartedVisibility(boolean isVisible);

    void setLoaderVisibility(boolean isVisible);

    void showToast(String msg);
}
