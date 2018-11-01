package com.kin.ecosystem.recovery.restore.presenter;


import com.kin.ecosystem.recovery.base.BasePresenter;
import com.kin.ecosystem.recovery.base.BaseView;

interface BaseChildPresenter<T extends BaseView> extends BasePresenter<T> {

	void onAttach(T view, RestorePresenter restorePresenter);
}
