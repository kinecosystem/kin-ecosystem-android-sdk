package com.kin.ecosystem.core

import java.util.concurrent.Callable

internal class PollingRequest<T>(private val intervals: IntArray, private val callable: Callable<T>) {

    companion object {
        const val SECOND_IN_MILLIS: Long = 1000
    }

    private val pollingLimitIndex: Int = intervals.size
    private var request: Request<T>

    init {
        val pollingCallable = Callable<T> { poll() }
        request = Request(pollingCallable)
    }

    private fun poll(): T {
        Logger.log(Log().withTag("PollingRequest").text("start polling"))
        var pollingIndex = 0
        Polling@ while (true) {
            return try {
                callable.call()
            } catch (e: Exception) {
                if (pollingIndex < pollingLimitIndex) {
                    Logger.log(Log().withTag("PollingRequest").put("pollingIndex", pollingIndex))
                    Thread.sleep(intervals[pollingIndex] * SECOND_IN_MILLIS)
                    pollingIndex++
                    continue@Polling
                } else {
                    throw e
                }
            }
        }
    }

    fun run(callback: CoreCallback<T>?) {
        request.run(callback)
    }

    fun cancel(mayInterruptIfRunning: Boolean) {
        request.cancel(mayInterruptIfRunning)
    }
}