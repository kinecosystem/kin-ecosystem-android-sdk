package com.kin.ecosystem.widget.util

import android.content.res.AssetManager
import android.graphics.Typeface

class FontUtil private constructor(val assetsManager: AssetManager) {

    companion object {
        private var INSTANCE: FontUtil? = null

        /**
         * Should be called from Main Thread
         */
        fun init(assetsManager: AssetManager) {
            INSTANCE ?: FontUtil(assetsManager).also { INSTANCE = it }
        }

        private const val NAME_SAILEC_REGULAR  = "sailec.otf"
        private const val NAME_SAILEC_MEDIUM  = "sailec_medium.otf"


        val SAILEC: Typeface by lazy(LazyThreadSafetyMode.PUBLICATION) { loadTypeface(NAME_SAILEC_REGULAR) }
        val SAILEC_MEDIUM: Typeface by lazy(LazyThreadSafetyMode.PUBLICATION) { loadTypeface(NAME_SAILEC_MEDIUM) }

        private fun loadTypeface(fontName: String) = INSTANCE?.let {
            Typeface.createFromAsset(it.assetsManager, "fonts/$fontName")
        } ?: kotlin.run { throw NullPointerException("You must call init before accessing any font") }
    }
}