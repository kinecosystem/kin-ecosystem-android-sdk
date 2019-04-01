package com.kin.ecosystem.widget.util

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.util.TypedValue

class ThemeUtil {

    companion object {
        fun themeAttributeToColor(context: Context, themeAttributeId: Int, @ColorRes defaultColorRes: Int): Int {
            val outValue = TypedValue()
            val theme = context.theme
            val wasResolved = theme.resolveAttribute(themeAttributeId, outValue, true)
            return if (wasResolved) {
                ContextCompat.getColor(context, outValue.resourceId)
            } else {
                // fallback color
                ContextCompat.getColor(context, defaultColorRes)
            }
        }
    }

}