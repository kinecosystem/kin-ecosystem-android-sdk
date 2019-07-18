package com.kin.ecosystem.base

import android.support.annotation.AnimRes

class CustomAnimation constructor(@AnimRes val enter: Int = 0, @AnimRes var exit: Int = 0, @AnimRes val popEnter: Int = 0, @AnimRes val popExit: Int = 0) {

    private constructor(builder: Builder) : this(builder.enter, builder.exit, builder.popEnter, builder.popExit)

    class Builder() {

        constructor(init: Builder.() -> Unit) : this() {
            init()
        }

        @AnimRes var  enter: Int = 0
        @AnimRes var exit: Int = 0
        @AnimRes var popEnter: Int = 0
        @AnimRes var popExit: Int = 0

        fun enter(animation: Int) : Builder = apply { enter = animation}

        fun exit(animation: Int): Builder = apply { exit = animation }

        fun popEnter(animation: Int): Builder = apply { popEnter = animation }

        fun popExit(animation: Int): Builder = apply { popExit = animation }

        fun build() = CustomAnimation(this)
    }
}

fun customAnimation(block: CustomAnimation.Builder.() -> Unit): CustomAnimation = CustomAnimation.Builder().apply(block).build()