package com.kin.ecosystem.main.view;

import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.main.Title;
import com.kin.ecosystem.main.presenter.IEcosystemPresenter;

public interface IEcosystemView extends IBaseView<IEcosystemPresenter> {

	void updateTitle(@Title final int title);

	void navigateBack();
}
