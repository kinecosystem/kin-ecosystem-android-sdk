package com.kin.ecosystem.core.data.internal

import com.kin.ecosystem.common.KinEnvironment
import com.kin.ecosystem.common.KinTheme

interface Configuration {

    val environment: KinEnvironment

    var kinTheme: KinTheme

    interface Local {

        var kinTheme: KinTheme
    }
}
