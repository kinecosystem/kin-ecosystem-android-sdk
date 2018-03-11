package com.kin.ecosystem.splash.presenter;

import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.splash.view.ISplashView;

public interface ISplashPresenter extends IBasePresenter<ISplashView> {

    void getStartedClicked();

    void backButtonPressed();

    void onAnimationEnded();
}
