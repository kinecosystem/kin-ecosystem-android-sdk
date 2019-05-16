package com.kin.ecosystem.poll.presenter

import com.kin.ecosystem.base.IBasePresenter
import com.kin.ecosystem.poll.view.IPollWebView
import com.kin.ecosystem.web.EcosystemWebPageListener

interface IPollWebViewPresenter : IBasePresenter<IPollWebView>, EcosystemWebPageListener {

    fun closeClicked()
}
