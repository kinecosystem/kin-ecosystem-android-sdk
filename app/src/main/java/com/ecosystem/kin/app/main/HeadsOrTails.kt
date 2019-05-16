package com.ecosystem.kin.app.main

import java.util.*

enum class HeadsOrTails {
    HEADS,
    TAILS;

    companion object {
        @JvmStatic
        fun flip(): HeadsOrTails {
            val rand = Random().nextInt(2)
            return if (rand == 1) HEADS else TAILS
        }
    }
}