package com.kin.ecosystem.base

import android.support.annotation.CallSuper

open class BasePresenter<T : IBaseView> : IBasePresenter<T> {

    var view: T? = null
        protected set

    @CallSuper
    override fun onAttach(view: T) {
        this.view = view

    }

    @CallSuper
    override fun onDetach() {
        view = null
    }
}
