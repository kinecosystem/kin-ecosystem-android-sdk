package com.kin.ecosystem.recovery.base;


public interface BasePresenter<T extends BaseView> {

	void onAttach(T view);

	void onDetach();

	T getView();

	void onBackClicked();
}
