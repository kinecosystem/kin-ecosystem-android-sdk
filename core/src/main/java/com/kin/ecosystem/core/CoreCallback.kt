package com.kin.ecosystem.core

import java.lang.Exception

interface CoreCallback<T> {
    fun onResponse(response: T?)

    fun onFailure(exception: Exception)
}