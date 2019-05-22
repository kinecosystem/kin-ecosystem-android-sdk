package com.kin.ecosystem.settings.view

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.kin.ecosystem.R
import com.kin.ecosystem.base.ThemeUtil
import com.kin.ecosystem.widget.TouchIndicatorIcon


class SettingsItem @JvmOverloads constructor(context: Context,
                                            attrs: AttributeSet? = null,
                                            defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    private val touchIndicatorIcon: TouchIndicatorIcon
    private val title: TextView

    init {
        val height = resources.getDimensionPixelSize(R.dimen.kinecosystem_settings_item_height)
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER
        weightSum = 1f
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        LayoutInflater.from(context).inflate(R.layout.kinecosystem_settings_item_layout, this, true)
        touchIndicatorIcon = findViewById(R.id.settings_icon)
        title = findViewById(R.id.settings_text)

        val icon: Int
        val indicatorVisibility: Boolean
        val text: String?
        val styledAttributes = context.theme
                .obtainStyledAttributes(attrs, R.styleable.KinEcosystemSettingsItem, 0, 0)
        val topSeparator: Boolean
        val bottomSeparator: Boolean

        try {
            icon = styledAttributes.getResourceId(R.styleable.KinEcosystemSettingsItem_kinecosystem_src, -1)
            indicatorVisibility = styledAttributes.getBoolean(R.styleable.KinEcosystemSettingsItem_kinecosystem_indicatorVisibility, false)
            text = styledAttributes.getString(R.styleable.KinEcosystemSettingsItem_kinecosysem_text)
            topSeparator = styledAttributes.getBoolean(R.styleable.KinEcosystemSettingsItem_top_separator, false)
            bottomSeparator = styledAttributes.getBoolean(R.styleable.KinEcosystemSettingsItem_bottom_separator, false)
        } finally {
            styledAttributes.recycle()
        }

        if (icon != -1) {
            touchIndicatorIcon.setIcon(icon)
            touchIndicatorIcon.setTouchIndicatorVisibility(indicatorVisibility)
        }

        title.text = text
        val separatorColor = ThemeUtil.themeAttributeToColor(context, R.attr.separatorColor, R.color.settings_separator_light)
        var separator: Drawable? = null
        if (topSeparator && bottomSeparator) {
            separator = ContextCompat.getDrawable(getContext(), R.drawable.kinecosystem_top_bottom_stroke) as LayerDrawable
            separator.getDrawable(0).setColorFilter(separatorColor, PorterDuff.Mode.SRC_ATOP)
        } else {
            if (topSeparator) {
                separator = ContextCompat.getDrawable(getContext(), R.drawable.kinecosystem_top_stroke) as LayerDrawable
                separator.getDrawable(0).setColorFilter(separatorColor, PorterDuff.Mode.SRC_ATOP)
            }
            if(bottomSeparator) {
                separator = ContextCompat.getDrawable(getContext(), R.drawable.kinecosystem_bottom_stroke) as LayerDrawable
                separator.getDrawable(0).setColorFilter(separatorColor, PorterDuff.Mode.SRC_ATOP)
            }
        }

        separator?.let {
            background = it
        }
    }

    fun setIcon(@DrawableRes iconRes: Int) {
        touchIndicatorIcon.setIcon(iconRes)
    }

    fun changeIconColor(@ColorRes colorRes: Int) {
        touchIndicatorIcon.setIconColor(colorRes)
    }

    fun setTouchIndicatorVisibility(isVisible: Boolean) {
        touchIndicatorIcon.setTouchIndicatorVisibility(isVisible)
    }

    fun setText(@StringRes stringRes: Int) {
        title.setText(stringRes)
    }
}
