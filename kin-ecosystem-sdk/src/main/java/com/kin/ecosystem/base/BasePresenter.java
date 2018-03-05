package com.kin.ecosystem.base;

import android.support.annotation.CallSuper;

public class BasePresenter<T extends IBaseView> {

    protected T view;

    @CallSuper
    public void onAttach(T view) {
        this.view = view;

    }

    @CallSuper
    public void onDetach() {
        view = null;
    }
}
