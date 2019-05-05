package com.kin.ecosystem.base

import android.support.v4.app.Fragment
import com.kin.ecosystem.main.INavigator

open class KinEcosystemBaseFragment<P, V> : Fragment() where P : IBaseFragmentPresenter<V>, V : IBaseView {


    protected var presenter: P? = null
    var navigator: INavigator? = null
        set(value) {
            field = value
            presenter?.navigator = value
        }
}