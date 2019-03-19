package com.kin.ecosystem

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.res.TypedArray
import android.support.annotation.StyleableRes
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver

fun <T : View> T.obtainAttrs(set: AttributeSet?, @StyleableRes attrs: IntArray): TypedArray? = context.theme.obtainStyledAttributes(set, attrs, 0, 0)

inline fun <T : View> T.onPreDraw(crossinline action: T.() -> Unit) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            viewTreeObserver.removeOnPreDrawListener(this)
            action()
            return true
        }
    })
}

inline fun <T : ValueAnimator> T.withEndAction(crossinline action: T.() -> Unit) {
    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            action()
        }
    })
}