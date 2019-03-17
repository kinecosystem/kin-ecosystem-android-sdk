package com.kin.ecosystem.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView
import com.kin.ecosystem.R
import com.kin.ecosystem.obtainAttrs
import com.kin.ecosystem.widget.util.FontUtil

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
        return when(fontStyle) {
            REGULAR -> FontUtil.SAILEC
            BOLD -> FontUtil.SAILEC_MEDIUM
            else -> FontUtil.SAILEC
        }
    }

    companion object {
        private const val REGULAR = 0
        private const val BOLD = 1
    }
}