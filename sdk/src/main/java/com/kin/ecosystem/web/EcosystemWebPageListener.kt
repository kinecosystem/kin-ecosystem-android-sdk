package com.kin.ecosystem.web

interface EcosystemWebPageListener {

    fun onPageLoaded()

    fun onPageCancel()

    fun onPageResult(result: String)

    fun onPageClosed()
}
