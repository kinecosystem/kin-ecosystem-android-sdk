package com.kin.ecosystem.balance.view

import com.kin.ecosystem.base.IBaseView

interface IBalanceView : IBaseView {

    fun updateBalance(balance: Int)

    fun startLoadingAnimation()

    fun stopLoadingAnimation()
}