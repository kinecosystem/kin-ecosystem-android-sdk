package com.kin.ecosystem

import android.content.Context
import android.content.ContextWrapper

class KinContext(context: Context): ContextWrapper(context.applicationContext)