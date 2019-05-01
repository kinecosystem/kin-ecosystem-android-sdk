package com.kin.ecosystem.base

import com.kin.ecosystem.main.INavigator

open class BaseFragmentPresenter<V : IBaseView>(override var navigator: INavigator?) : BasePresenter<V>(), IBaseFragmentPresenter<V>