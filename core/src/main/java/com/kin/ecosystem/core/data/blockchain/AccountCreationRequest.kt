package com.kin.ecosystem.core.data.blockchain

import com.kin.ecosystem.common.KinCallback
import com.kin.ecosystem.common.exception.BlockchainException
import com.kin.ecosystem.common.exception.ClientException
import com.kin.ecosystem.common.exception.ClientException.INTERNAL_INCONSISTENCY
import com.kin.ecosystem.common.exception.KinEcosystemException
import com.kin.ecosystem.core.CoreCallback
import com.kin.ecosystem.core.PollingRequest
import com.kin.ecosystem.core.util.ErrorUtil
import java.util.concurrent.Callable

class AccountCreationRequest(val blockchainSource: BlockchainSource) {

    private var req: PollingRequest<Void>

    init {
        req = PollingRequest(intervals = intArrayOf(2, 2, 3, 5, 10), callable = Callable<Void> {
            blockchainSource.balanceSync
            return@Callable null
        })
    }

    fun run(callback: KinCallback<Void>) {
        req.run(object : CoreCallback<Void> {
            override fun onResponse(response: Void?) {
                callback.onResponse(response)
            }

            override fun onFailure(exception: Exception) {
                if (exception is ClientException || exception is BlockchainException) {
                    callback.onFailure(exception as KinEcosystemException)
                } else {
                    callback.onFailure(ErrorUtil.getClientException(INTERNAL_INCONSISTENCY, exception))
                }
            }
        })
    }

    fun cancel() {
        req.cancel(true)
    }
}


