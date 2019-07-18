package com.kin.ecosystem.base

import com.kin.ecosystem.main.INavigator

interface IBaseFragmentPresenter<V : IBaseView>: IBasePresenter<V> {

    var navigator: INavigator?
}