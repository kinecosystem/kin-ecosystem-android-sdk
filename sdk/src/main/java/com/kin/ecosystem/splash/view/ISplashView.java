package com.kin.ecosystem.splash.view;

import com.kin.ecosystem.EcosystemExperience;
import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.splash.presenter.ISplashPresenter.Message;
import com.kin.ecosystem.splash.presenter.SplashPresenter;

public interface ISplashView extends IBaseView<SplashPresenter> {

	void navigateToEcosystemActivity(@EcosystemExperience final int experience);

	void navigateBack();

	void animateLoading();

	void stopLoading(boolean reset);

	void showToast(@Message final int message);
}
