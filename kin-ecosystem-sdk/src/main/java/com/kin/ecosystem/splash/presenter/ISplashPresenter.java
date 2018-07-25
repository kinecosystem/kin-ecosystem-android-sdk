package com.kin.ecosystem.splash.presenter;

import android.support.annotation.IntDef;
import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.splash.view.ISplashView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface ISplashPresenter extends IBasePresenter<ISplashView> {

	int TRY_AGAIN = 0x00000001;
	int SOMETHING_WENT_WRONG = 0x00000002;

	@IntDef({TRY_AGAIN, SOMETHING_WENT_WRONG})
	@Retention(RetentionPolicy.SOURCE)
	@interface Message {

	}

	void getStartedClicked();

	void backButtonPressed();

	void onAnimationEnded();
}
