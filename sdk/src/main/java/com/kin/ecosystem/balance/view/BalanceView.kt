package com.kin.ecosystem.balance.view

import android.annotation.SuppressLint
import android.content.Context
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextSwitcher
import android.widget.TextView
import com.kin.ecosystem.R
import com.kin.ecosystem.balance.presenter.BalancePresenter
import com.kin.ecosystem.balance.presenter.IBalancePresenter
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl
import com.kin.ecosystem.core.data.order.OrderRepository
import com.kin.ecosystem.core.util.StringUtil.getAmountFormatted
import com.kin.ecosystem.widget.util.FontUtil


class BalanceView @JvmOverloads constructor(context: Context,
                                            attrs: AttributeSet? = null,
                                            defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), IBalanceView {

    private var kinAVD: AnimatedVectorDrawableCompat
    private var balanceText: TextSwitcher
    private var presenter: IBalancePresenter? = null


    init {
        View.inflate(context, R.layout.kinecosystem_balance_view, this)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        kinAVD = getKinLogoAVD()
        balanceText = findViewById<TextSwitcher>(R.id.balance_text).apply {
            setFactory {
                val balanceText = TextView(context)
                balanceText.setTextAppearance(context, R.style.KinecosysTitle)
                balanceText.typeface = FontUtil.SAILEC_MEDIUM
                balanceText.setTextColor(ContextCompat.getColor(context, R.color.kinecosystem_primary))
                balanceText
            }
        }
        presenter = BalancePresenter(BlockchainSourceImpl.getInstance(), OrderRepository.getInstance())
    }

    @SuppressLint("NewApi")
    private fun getKinLogoAVD(): AnimatedVectorDrawableCompat = findViewById<ImageView>(R.id.avd_kin_logo).drawable as AnimatedVectorDrawableCompat

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        presenter?.onAttach(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        presenter?.onDetach()
    }

    override fun updateBalance(balance: Int) {
        val balanceString = if (balance == 0) {
            BALANCE_ZERO_TEXT
        } else {
            getAmountFormatted(balance)
        }
        post { balanceText.setText(balanceString) }
    }

    override fun startLoadingAnimation() {
        kinAVD.start()
    }

    override fun stopLoadingAnimation() {
        kinAVD.stop()
    }

    companion object {
        private const val BALANCE_ZERO_TEXT = "0"
    }
}