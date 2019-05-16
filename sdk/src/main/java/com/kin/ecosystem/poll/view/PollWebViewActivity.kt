package com.kin.ecosystem.poll.view

import com.kin.ecosystem.common.exception.ClientException.INTERNAL_INCONSISTENCY

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.widget.Toast
import com.kin.ecosystem.R
import com.kin.ecosystem.base.KinEcosystemBaseActivity
import com.kin.ecosystem.common.exception.ClientException
import com.kin.ecosystem.core.bi.EventLoggerImpl
import com.kin.ecosystem.core.data.internal.ConfigurationImpl
import com.kin.ecosystem.core.data.order.OrderRepository
import com.kin.ecosystem.core.network.model.Offer
import com.kin.ecosystem.core.util.ErrorUtil
import com.kin.ecosystem.poll.presenter.IPollWebViewPresenter
import com.kin.ecosystem.poll.presenter.PollWebViewPresenter
import com.kin.ecosystem.web.EcosystemWebView

class PollWebViewActivity : KinEcosystemBaseActivity(), IPollWebView {

    private lateinit var pollWebViewPresenter: IPollWebViewPresenter
    private var webView: EcosystemWebView? = null

    override val layoutRes: Int
        get() = R.layout.kinecosystem_activity_poll

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pollBundle = PollBundle(intent.extras)
        pollWebViewPresenter = PollWebViewPresenter(pollBundle.jsonData,
                pollBundle.offerID,
                pollBundle.contentType,
                pollBundle.amount,
                ConfigurationImpl.getInstance(),
                OrderRepository.getInstance(),
                EventLoggerImpl.getInstance())
        pollWebViewPresenter.onAttach(this)
    }

    override fun onBackPressed() {
        pollWebViewPresenter.closeClicked()
    }

    override fun initViews() {
        webView = findViewById(R.id.webview)
    }

    override fun showToast(msg: IPollWebView.Message) {
        runOnUiThread { Toast.makeText(this@PollWebViewActivity, getMessageResId(msg), Toast.LENGTH_SHORT).show() }
    }

    @StringRes
    private fun getMessageResId(msg: IPollWebView.Message): Int {
        return when (msg) {
            IPollWebView.Message.ORDER_SUBMISSION_FAILED -> R.string.kinecosystem_order_submission_failed
            IPollWebView.Message.SOMETHING_WENT_WRONG -> R.string.kinecosystem_something_went_wrong
        }
    }

    override fun initWebView() {
        webView?.let {
            it.setListener(pollWebViewPresenter)
            it.load()
        }
    }

    override fun renderJson(pollJsonString: String, kinTheme: String) {
        webView?.let {
            it.setTheme(kinTheme)
            it.renderPoll(pollJsonString)
        }
    }

    override fun close() {
        finish()
    }

    override fun onDestroy() {
        pollWebViewPresenter.onDetach()
        releaseWebView()
        super.onDestroy()
    }

    private fun releaseWebView() {
        webView?.release()
    }

    class PollBundle {

        private var bundle: Bundle

        val jsonData: String
            get() = bundle.getString(EXTRA_JSON_DATA_KEY)

        val offerID: String
            get() = bundle.getString(EXTRA_OFFER_ID_KEY)

        val contentType: String
            get() = bundle.getString(EXTRA_CONTENT_TYPE_KEY)

        val amount: Int
            get() = bundle.getInt(EXTRA_AMOUNT_KEY)

        constructor() {
            this.bundle = Bundle()
        }

        constructor(bundle: Bundle) {
            this.bundle = bundle
        }

        fun setJsonData(jsonData: String): PollBundle {
            this.bundle.putString(EXTRA_JSON_DATA_KEY, jsonData)
            return this
        }

        fun setOfferID(offerID: String): PollBundle {
            this.bundle.putString(EXTRA_OFFER_ID_KEY, offerID)
            return this
        }

        fun setTitle(title: String): PollBundle {
            this.bundle.putString(EXTRA_TITLE_KEY, title)
            return this
        }

        fun setContentType(contentType: Offer.ContentTypeEnum): PollBundle {
            this.bundle.putString(EXTRA_CONTENT_TYPE_KEY, contentType.value)
            return this
        }

        fun setAmount(amount: Int): PollBundle {
            this.bundle.putInt(EXTRA_AMOUNT_KEY, amount)
            return this
        }

        @Throws(ClientException::class)
        fun build(): Bundle {
            if (bundle.size() < FIELD_COUNT) {
                throw ErrorUtil.getClientException(INTERNAL_INCONSISTENCY,
                        IllegalArgumentException("You must specified all the fields."))
            }
            return bundle
        }

        companion object {

            private const val FIELD_COUNT = 3

            private const val EXTRA_JSON_DATA_KEY = "jsondata"
            private const val EXTRA_OFFER_ID_KEY = "offer_id"
            private const val EXTRA_CONTENT_TYPE_KEY = "content_type"
            private const val EXTRA_AMOUNT_KEY = "amount"
            private const val EXTRA_TITLE_KEY = "title"
        }

    }

    companion object {

        @Throws(ClientException::class)
        fun createIntent(context: Context, bundle: PollBundle): Intent {
            val intent = Intent(context, PollWebViewActivity::class.java)
            intent.putExtras(bundle.build())
            return intent
        }
    }
}
