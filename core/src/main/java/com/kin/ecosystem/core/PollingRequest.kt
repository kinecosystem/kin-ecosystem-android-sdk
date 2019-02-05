package com.kin.ecosystem.core

import java.util.concurrent.Callable

internal class PollingRequest<T>(private val intervals: IntArray, private val callable: Callable<T>) {

    companion object {
        const val SECOND_IN_MILLIS: Long = 1000
    }

    private val pollingLimitIndex: Int = intervals.size
    private var request: Request<T>? = null
    private val pollingCallable = Callable<T> {
        Logger.log(Log().withTag("PollingRequest").text("start polling"))
        poll(0)
    }


    private fun poll(pollingIndex: Int): T {
        return try {
            Logger.log(Log().withTag("PollingRequest").put("pollingIndex", pollingIndex))
            callable.call()
        } catch (e: Exception) {
            if (pollingIndex < pollingLimitIndex) {
                Thread.sleep(intervals[pollingIndex] * SECOND_IN_MILLIS)
                poll(pollingIndex.inc())
            } else {
                throw e
            }
        }
    }

    fun run(callback: CoreCallback<T>) {
        request = Request(pollingCallable)
        request?.run(callback)
    }

    fun cancel(mayInterruptIfRunning: Boolean) {
        request?.cancel(mayInterruptIfRunning)
    }
}