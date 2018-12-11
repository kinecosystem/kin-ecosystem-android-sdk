package com.kin.ecosystem.main.presenter;

import android.os.Bundle;
import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.main.ScreenId;
import com.kin.ecosystem.main.view.IEcosystemView;

public interface IEcosystemPresenter extends IBasePresenter<IEcosystemView> {

	void onStart();

	void onStop();

	void balanceItemClicked();

	void backButtonPressed();

	void visibleScreen(@ScreenId final int id);

	void settingsMenuClicked();

	void onMenuInitialized();


	void onSaveInstanceState(Bundle outState);
}
