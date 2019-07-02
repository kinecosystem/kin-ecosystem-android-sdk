package com.kin.ecosystem.base

import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator

object AnimConsts {

    object Property {
        internal const val ALPHA = "alpha"
    }

    object Duration {
        internal const val FADE_ANIM = 300L
        internal const val CLOSE_ANIM = 350L
        internal const val SLIDE_ANIM = 300L
    }

    object Value {
        internal const val ALPHA_1 = 1F
        internal const val ALPHA_0 = 0F

        internal const val BG_COLOR_ALPHA_255 = 255
        internal const val BG_COLOR_ALPHA_0 = 0
    }

    object Interpolator {
        @JvmField internal val DECELERATE = DecelerateInterpolator()
        @JvmField internal val ACCELERATE_DECELERATE = AccelerateDecelerateInterpolator()
    }
}