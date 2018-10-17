package com.kin.ecosystem.backup.base;


public interface BasePresenter<T extends BaseView> {

	void onAttach(T view);

	void onDetach();

	T getView();

	void onBackClicked();
}
