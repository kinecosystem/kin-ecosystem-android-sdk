package com.kin.ecosystem.poll.view

import com.kin.ecosystem.base.IBaseView

interface IPollWebView : IBaseView {

    enum class Message {
        ORDER_SUBMISSION_FAILED,
        SOMETHING_WENT_WRONG
    }

    fun showToast(msg: Message)

    fun initWebView()

    fun renderJson(pollJsonString: String, kinTheme: String)

    fun close()
}
