package com.kin.ecosystem.base.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView
import com.kin.ecosystem.base.FontUtil
import com.kin.ecosystem.base.R

class KinEcosystemTextView @JvmOverloads constructor(context: Context,
                                                     attrs: AttributeSet? = null,
                                                     defStyleAttr: Int = 0) : TextView(context, attrs, defStyleAttr) {

    init {
        val attributes = obtainAttrs(attrs, R.styleable.KinEcosystemTextView)

        try {
            attributes?.let {
                val fontExtra = it.getInt(R.styleable.KinEcosystemTextView_fontExtra, REGULAR)
                typeface = getTypeFace(fontExtra)
            }
        } finally {
            attributes?.recycle()
        }
    }

    private fun getTypeFace(fontStyle: Int): Typeface {
        return when (fontStyle) {
            MEDIUM -> FontUtil.SAILEC_MEDIUM
            else -> FontUtil.SAILEC
        }
    }

    companion object {
        private const val REGULAR = 0
        private const val MEDIUM = 1
    }
}