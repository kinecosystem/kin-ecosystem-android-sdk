package com.kin.ecosystem.base

interface IBasePresenter<T : IBaseView> {

    fun onAttach(view: T)

    fun onDetach()
}
