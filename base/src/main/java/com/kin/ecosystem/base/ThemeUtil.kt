package com.kin.ecosystem.base

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
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

        fun themeAttributeToDrawable(context: Context, themeAttributeId: Int, @DrawableRes defaultDrawableRes: Int): Drawable {
            val outValue = TypedValue()
            val theme = context.theme
            val wasResolved = theme.resolveAttribute(themeAttributeId, outValue, true)
            return if (wasResolved) {
                ContextCompat.getDrawable(context, outValue.resourceId)
            } else {
                // fallback drawable
                ContextCompat.getDrawable(context, defaultDrawableRes)
            }
        }
    }

}