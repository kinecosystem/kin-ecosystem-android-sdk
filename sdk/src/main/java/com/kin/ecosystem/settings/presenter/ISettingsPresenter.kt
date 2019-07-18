package com.kin.ecosystem.settings.presenter

import android.content.Intent
import com.kin.ecosystem.base.IBaseFragmentPresenter
import com.kin.ecosystem.base.IBasePresenter
import com.kin.ecosystem.settings.view.ISettingsView

interface ISettingsPresenter : IBaseFragmentPresenter<ISettingsView> {

    fun onResume()

    fun onPause()

    fun backupClicked()

    fun restoreClicked()

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    fun backClicked()
}
