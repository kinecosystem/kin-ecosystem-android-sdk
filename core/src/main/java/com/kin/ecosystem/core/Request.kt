package com.kin.ecosystem.core

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Each request will run sequentially on background thread,
 * and will notify [CoreCallback] onResponse or onFailure on main thread.
 *
 * @param <T> request result type
</T> */
internal open class Request<T> internal constructor(private val callable: Callable<T>) {
    private val mainHandler: Handler = Handler(Looper.getMainLooper())
    private var cancelled: Boolean = false
    private var executed: Boolean = false
    private var future: Future<*>? = null
    private var resultCallback: CoreCallback<T>? = null

    /**
     * Run request asynchronously, notify `callback` with successful result or error
     */
    @Synchronized
    fun run(callback: CoreCallback<T>?) {
        checkBeforeRun()
        executed = true
        submitFuture(callable, callback)
    }

    private fun checkBeforeRun() {
        if (executed) {
            throw IllegalStateException("Request already running.")
        }
        if (cancelled) {
            throw IllegalStateException("Request already cancelled.")
        }
    }

    private fun submitFuture(callable: Callable<T>, callback: CoreCallback<T>?) {
        resultCallback = callback
        future = executorService.submit {
            try {
                val result = callable.call()
                executeOnMainThreadIfNotCancelled(Runnable { resultCallback?.onResponse(result) })
            } catch (e: Exception) {
                executeOnMainThreadIfNotCancelled(Runnable { resultCallback?.onFailure(e) })
            }
        }
    }

    @Synchronized
    private fun executeOnMainThreadIfNotCancelled(runnable: Runnable) {
        if (!cancelled) {
            mainHandler.post(runnable)
        }
    }

    /**
     * Cancel `Request` and detach its callback,
     * an attempt will be made to cancel ongoing request, if request has not run yet it will never run.
     *
     * @param mayInterruptIfRunning true if the request should be interrupted; otherwise, in-progress requests are
     * allowed to complete
     */
    @Synchronized
    fun cancel(mayInterruptIfRunning: Boolean) {
        if (!cancelled) {
            cancelled = true
            future?.cancel(mayInterruptIfRunning)
            future = null
            mainHandler.apply {
                removeCallbacksAndMessages(null)
                post { resultCallback = null }
            }
        }
    }

    companion object {

        private val executorService = Executors.newSingleThreadExecutor()
    }
}
