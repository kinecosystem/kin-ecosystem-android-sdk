package com.kin.ecosystem.base;

import android.support.annotation.CallSuper;

public class BasePresenter<T extends IBaseView> implements IBasePresenter<T> {

    protected T view;

    @CallSuper
    @Override
    public void onAttach(T view) {
        this.view = view;

    }

    @CallSuper
    @Override
    public void onDetach() {
        view = null;
    }
}
