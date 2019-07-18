package com.kin.ecosystem.widget

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.kin.ecosystem.R
import com.kin.ecosystem.base.ThemeUtil
import com.kin.ecosystem.base.widget.obtainAttrs
import com.kin.ecosystem.base.widget.onPreDraw
import com.kin.ecosystem.base.widget.withEndAction


//  <declare-styleable name="KinEcosystemTabsView">
//      <attr format="string" name="leftTabText"/>
//      <attr format="string" name="rightTabText"/>
//      <attr format="color" name="leftColor"/>
//      <attr format="color" name="rightColor"/>
//      <attr format="enum" name="defaultSelected">
//          <enum name="left" value="0"/>
//          <enum name="right" value="1"/>
//      </attr>
//  </declare-styleable>
class KinEcosystemTabs @JvmOverloads constructor(context: Context,
                                                 attrs: AttributeSet? = null,
                                                 defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var defaultSelectedTab = Tab.LEFT
    private var currentSelectedTab: Tab? = null
    private val selectedBG: ImageView
    private val leftTab: TextView
    private val rightTab: TextView
    private var isAnimating = false
    private var onTabClickedListener: OnTabClickedListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.kinecosystem_tab_layout, this, true)
        layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        val attributes = obtainAttrs(attrs, R.styleable.KinEcosystemTabsView)
        var leftTabText = ""
        var rightTabText = ""
        try {
            attributes?.let {
                leftTabText = it.getText(R.styleable.KinEcosystemTabsView_leftTabText).toString()
                rightTabText = it.getText(R.styleable.KinEcosystemTabsView_rightTabText).toString()
                leftColor = it.getColor(R.styleable.KinEcosystemTabsView_leftColor, ContextCompat.getColor(context, R.color.kinecosystem_purple))
                rightColor = it.getColor(R.styleable.KinEcosystemTabsView_rightColor, ContextCompat.getColor(context, R.color.kinecosystem_green))
                defaultSelectedTab = Tab.fromInt(it.getInt(R.styleable.KinEcosystemTabsView_defaultSelected, Tab.LEFT.ordinal))
            }
        } finally {
            attributes?.recycle()
        }

        findViewById<LinearLayout>(R.id.tabs_bg).apply {
            background.setColorFilter(ThemeUtil.themeAttributeToColor(getContext(), R.attr.tabDeselectedBackgroundColor,
                    R.color.kinecosystem_subtitle), PorterDuff.Mode.SRC_ATOP)
        }


        textSelectedColor = ContextCompat.getColor(context, R.color.kinecosystem_white)
        textDeselectedColor = ContextCompat.getColor(context, R.color.kinecosystem_tab_deselected_text)

        selectedBG = findViewById(R.id.selected_bg)
        leftTab = findViewById<TextView>(R.id.left_tab).apply {
            text = leftTabText
            setOnClickListener {
                setSelectedTab(Tab.LEFT)
            }
        }
        rightTab = findViewById<TextView>(R.id.right_tab).apply {
            text = rightTabText
            setOnClickListener {
                setSelectedTab(Tab.RIGHT)
            }
        }
        onPreDraw {
            rightXPos = selectedBG.width.toFloat()
            setSelectedTab(defaultSelectedTab, animate = false)
        }
    }

    private fun setSelectedTab(selectedTabIndex: Tab, animate: Boolean = true) {
        if (currentSelectedTab == selectedTabIndex) return

        if (animate) {

            if (isAnimating) return

            isAnimating = true
            selectedBG.background.setColorFilter(leftColor, PorterDuff.Mode.MULTIPLY)
            if (selectedTabIndex == Tab.LEFT) {
                val propertyLeftTop = PropertyValuesHolder.ofInt(PROP_NAME_LEFT_TOP, rightTabRadius[0].toInt(), leftTabRadius[0].toInt())
                val propertyLeftBottom = PropertyValuesHolder.ofInt(PROP_NAME_LEFT_BOT, rightTabRadius[7].toInt(), leftTabRadius[7].toInt())
                val propertyRightTop = PropertyValuesHolder.ofInt(PROP_NAME_RIGHT_TOP, rightTabRadius[3].toInt(), leftTabRadius[3].toInt())
                val propertyRightBottom = PropertyValuesHolder.ofInt(PROP_NAME_RIGHT_BOTTOM, rightTabRadius[5].toInt(), leftTabRadius[5].toInt())
                val propertyXPos = PropertyValuesHolder.ofFloat(PROP_NAME_X_POS, rightXPos, leftXPos)
                val propertyColor = PropertyValuesHolder.ofObject(PROP_NAME_COLOR, argbEvaluator, rightColor, leftColor)

                ValueAnimator.ofPropertyValuesHolder(propertyLeftTop, propertyLeftBottom, propertyRightTop, propertyRightBottom, propertyXPos, propertyColor).let { animator ->
                    animator.addUpdateListener {
                        val lt = (it.getAnimatedValue(PROP_NAME_LEFT_TOP) as Int).toFloat()
                        val lb = (it.getAnimatedValue(PROP_NAME_LEFT_BOT) as Int).toFloat()
                        val rt = (it.getAnimatedValue(PROP_NAME_RIGHT_TOP) as Int).toFloat()
                        val rb = (it.getAnimatedValue(PROP_NAME_RIGHT_BOTTOM) as Int).toFloat()
                        val xPos = it.getAnimatedValue(PROP_NAME_X_POS) as Float
                        val color = it.getAnimatedValue(PROP_NAME_COLOR) as Int

                        val toRightRadius = floatArrayOf(lt, lt, rt, rt, rb, rb, lb, lb)
                        val roundRect = RoundRectShape(toRightRadius, null, null)
                        ShapeDrawable(roundRect).let { shape ->
                            shape.paint.color = color
                            shape.paint.style = Paint.Style.FILL
                            shape.paint.isAntiAlias = true
                            with(selectedBG) {
                                background = shape
                                x = xPos
                            }
                        }
                    }
                    animator.duration = DURATION_SLIDE_ANIM
                    animator.interpolator = FastOutSlowInInterpolator()
                    animator.withEndAction {
                        isAnimating = false
                    }
                    animator.start()
                }

                ObjectAnimator.ofInt(leftTab, PROP_NAME_TEXT_COLOR, leftTab.currentTextColor, textSelectedColor).apply {
                    duration = DURATION_FADE_ANIM
                    setEvaluator(argbEvaluator)
                    start()
                }

                ObjectAnimator.ofInt(rightTab, PROP_NAME_TEXT_COLOR, rightTab.currentTextColor, textDeselectedColor).apply {
                    duration = DURATION_FADE_ANIM
                    setEvaluator(argbEvaluator)
                    start()
                }

            } else {
                val propertyLeftTop = PropertyValuesHolder.ofInt(PROP_NAME_LEFT_TOP, leftTabRadius[0].toInt(), rightTabRadius[0].toInt())
                val propertyLeftBottom = PropertyValuesHolder.ofInt(PROP_NAME_LEFT_BOT, leftTabRadius[7].toInt(), rightTabRadius[7].toInt())
                val propertyRightTop = PropertyValuesHolder.ofInt(PROP_NAME_RIGHT_TOP, leftTabRadius[3].toInt(), rightTabRadius[3].toInt())
                val propertyRightBottom = PropertyValuesHolder.ofInt(PROP_NAME_RIGHT_BOTTOM, leftTabRadius[5].toInt(), rightTabRadius[5].toInt())
                val propertyXPos = PropertyValuesHolder.ofFloat(PROP_NAME_X_POS, leftXPos, rightXPos)
                val propertyColor = PropertyValuesHolder.ofObject(PROP_NAME_COLOR, argbEvaluator, leftColor, rightColor)

                ValueAnimator.ofPropertyValuesHolder(propertyLeftTop, propertyLeftBottom, propertyRightTop, propertyRightBottom, propertyXPos, propertyColor).let { animator ->
                    animator.addUpdateListener {
                        val lt = (it.getAnimatedValue(PROP_NAME_LEFT_TOP) as Int).toFloat()
                        val lb = (it.getAnimatedValue(PROP_NAME_LEFT_BOT) as Int).toFloat()
                        val rt = (it.getAnimatedValue(PROP_NAME_RIGHT_TOP) as Int).toFloat()
                        val rb = (it.getAnimatedValue(PROP_NAME_RIGHT_BOTTOM) as Int).toFloat()
                        val xPos = it.getAnimatedValue(PROP_NAME_X_POS) as Float
                        val color = it.getAnimatedValue(PROP_NAME_COLOR) as Int

                        val toLeftRadius = floatArrayOf(lt, lt, rt, rt, rb, rb, lb, lb)
                        val roundRect = RoundRectShape(toLeftRadius, null, null)
                        ShapeDrawable(roundRect).let { shape ->
                            shape.paint.color = color
                            shape.paint.style = Paint.Style.FILL
                            shape.paint.isAntiAlias = true
                            with(selectedBG) {
                                background = shape
                                x = xPos
                            }
                        }
                    }
                    animator.duration = DURATION_SLIDE_ANIM
                    animator.interpolator = FastOutSlowInInterpolator()
                    animator.withEndAction {
                        isAnimating = false
                        rightTab.setTextColor(textSelectedColor)
                        leftTab.setTextColor(textDeselectedColor)
                    }
                    animator.start()
                }

                ObjectAnimator.ofInt(leftTab, PROP_NAME_TEXT_COLOR, leftTab.currentTextColor, textDeselectedColor).apply {
                    duration = DURATION_FADE_ANIM
                    setEvaluator(argbEvaluator)
                    start()
                }

                ObjectAnimator.ofInt(rightTab, PROP_NAME_TEXT_COLOR, rightTab.currentTextColor, textSelectedColor).apply {
                    duration = DURATION_FADE_ANIM
                    setEvaluator(argbEvaluator)
                    start()
                }
            }
        } else {
            if (selectedTabIndex == Tab.LEFT) {
                val roundRect = RoundRectShape(leftTabRadius, null, null)
                ShapeDrawable(roundRect).apply {
                    paint.color = leftColor
                    paint.style = Paint.Style.FILL
                    paint.isAntiAlias = true
                    with(selectedBG) {
                        background = this@apply
                        x = 0F
                    }
                }
                leftTab.setTextColor(textSelectedColor)
                rightTab.setTextColor(textDeselectedColor)

            } else {
                val roundRect = RoundRectShape(rightTabRadius, null, null)
                ShapeDrawable(roundRect).apply {
                    paint.color = rightColor
                    paint.style = Paint.Style.FILL
                    paint.isAntiAlias = true
                    with(selectedBG) {
                        background = this@apply
                        x += width
                    }
                }
                rightTab.setTextColor(textSelectedColor)
                leftTab.setTextColor(textDeselectedColor)
            }
        }

        if (currentSelectedTab != null) {
            onTabClickedListener?.onTabClicked(selectedTabIndex)
        }
        currentSelectedTab = selectedTabIndex
    }

    fun setOnTabClickedListener(listener: (Tab) -> Unit) {
        setListener(object : OnTabClickedListener {
            override fun onTabClicked(tab: Tab) {
                listener.invoke(tab)
            }
        })
    }

    private fun setListener(onTabClickedListener: OnTabClickedListener) {
        this.onTabClickedListener = onTabClickedListener
    }

    companion object {
        private const val PROP_NAME_TEXT_COLOR = "textColor"

        private const val PROP_NAME_LEFT_TOP = "left_top"
        private const val PROP_NAME_LEFT_BOT = "left_bottom"

        private const val PROP_NAME_RIGHT_TOP = "right_top"
        private const val PROP_NAME_RIGHT_BOTTOM = "right_bottom"

        private const val PROP_NAME_X_POS = "x_pos"

        private const val PROP_NAME_COLOR = "color"

        private const val DURATION_SLIDE_ANIM = 300L
        private const val DURATION_FADE_ANIM = 250L

        private var leftColor: Int = -1
        private var rightColor: Int = -1

        private var textSelectedColor: Int = -1
        private var textDeselectedColor: Int = -1

        private const val leftXPos = 0F
        private var rightXPos = -1F

        // Initialize two float arrays - radius for tabs
        private val leftTabRadius = floatArrayOf(15f, 15f, 0f, 0f, 0f, 0f, 15f, 15f)
        private val rightTabRadius = floatArrayOf(0f, 0f, 15f, 15f, 15f, 15f, 0f, 0f)

        private val argbEvaluator = ArgbEvaluator()
    }

    enum class Tab {
        LEFT,
        RIGHT;

        companion object {

            fun fromInt(tabIndex: Int): Tab {
                return when (tabIndex) {
                    LEFT.ordinal -> LEFT
                    RIGHT.ordinal -> RIGHT
                    else -> LEFT
                }
            }
        }
    }

    interface OnTabClickedListener {
        fun onTabClicked(tab: Tab)
    }
}