package com.kin.ecosystem.recovery.restore.presenter;


import com.kin.ecosystem.recovery.base.BasePresenterImpl;
import com.kin.ecosystem.recovery.base.BaseView;

abstract class BaseChildPresenterImpl<T extends BaseView> extends BasePresenterImpl<T> implements
	BaseChildPresenter<T> {

	private RestorePresenter parentPresenter;

	@Override
	public void onAttach(T view, RestorePresenter restorePresenter) {
		super.onAttach(view);
		this.parentPresenter = restorePresenter;
		onAttach(view);
	}

	RestorePresenter getParentPresenter() {
		return parentPresenter;
	}
}
