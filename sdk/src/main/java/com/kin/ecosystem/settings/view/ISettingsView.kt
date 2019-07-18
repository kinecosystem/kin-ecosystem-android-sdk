package com.kin.ecosystem.settings.view

import com.kin.ecosystem.base.IBaseView

interface ISettingsView : IBaseView {

    enum class Item {
        ITEM_BACKUP,
        ITEM_RESTORE
    }

    enum class IconColor {
        PRIMARY
    }

    fun setIconColor(item: Item, color: IconColor)

    fun changeTouchIndicatorVisibility(item: Item, isVisible: Boolean)

    fun showCouldNotImportAccount()
}
