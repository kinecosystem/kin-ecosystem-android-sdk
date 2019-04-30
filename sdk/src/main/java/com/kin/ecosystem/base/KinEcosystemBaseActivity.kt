package com.kin.ecosystem.base

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StyleRes
import android.support.v7.app.AppCompatActivity
import com.kin.ecosystem.R
import com.kin.ecosystem.common.KinTheme
import com.kin.ecosystem.core.data.internal.ConfigurationImpl

abstract class KinEcosystemBaseActivity : AppCompatActivity() {

    @get:LayoutRes
    protected abstract val layoutRes: Int

    protected abstract fun initViews()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KinEcosystemInitiator.init(applicationContext)
        setTheme(getKinTheme())
        setContentView(layoutRes)
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        initViews()
    }

    @StyleRes
    private fun getKinTheme(): Int {
        return ConfigurationImpl.getInstance().kinTheme?.let {
            when (it) {
                KinTheme.LIGHT -> R.style.KinecosysNoActionBar_Light
                KinTheme.DARK -> R.style.KinecosysNoActionBar_Dark
            }
        } ?: R.style.KinecosysNoActionBar_Light
    }
}
