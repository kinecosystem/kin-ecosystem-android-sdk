package com.kin.ecosystem.main.presenter


import com.kin.ecosystem.Kin.KEY_ECOSYSTEM_EXPERIENCE

import android.os.Bundle
import com.kin.ecosystem.EcosystemExperience
import com.kin.ecosystem.R
import com.kin.ecosystem.base.BasePresenter
import com.kin.ecosystem.base.CustomAnimation
import com.kin.ecosystem.common.Observer
import com.kin.ecosystem.common.exception.BlockchainException
import com.kin.ecosystem.common.exception.ClientException
import com.kin.ecosystem.common.model.Balance
import com.kin.ecosystem.core.bi.EventLogger
import com.kin.ecosystem.core.bi.events.GeneralEcosystemSdkError
import com.kin.ecosystem.core.data.auth.AuthDataSource
import com.kin.ecosystem.core.data.blockchain.BlockchainSource
import com.kin.ecosystem.core.data.settings.SettingsDataSource
import com.kin.ecosystem.core.util.StringUtil
import com.kin.ecosystem.main.INavigator
import com.kin.ecosystem.main.ScreenId
import com.kin.ecosystem.main.view.IEcosystemView
import java.math.BigDecimal

class EcosystemPresenter(private val authDataSource: AuthDataSource,
                         private val settingsDataSource: SettingsDataSource,
                         private val blockchainSource: BlockchainSource, eventLogger: EventLogger,
                         private val navigator: INavigator?, savedInstanceState: Bundle?, extras: Bundle) : BasePresenter<IEcosystemView>(), IEcosystemPresenter {
    @ScreenId
    private var visibleScreen: Int = 0
    @EcosystemExperience
    private var experience: Int = 0
    private var isConsumedIntentExtras: Boolean = false

    private var balanceObserver: Observer<Balance>? = null
    private var currentBalance: Balance? = null
    private var publicAddress: String? = null

    init {
        this.currentBalance = blockchainSource.balance
        try {
            this.publicAddress = blockchainSource.publicAddress
        } catch (e: ClientException) {
            eventLogger.send(GeneralEcosystemSdkError
                    .create("EcosystemPresenter blockchainSource.getPublicAddress() thrown an exception"))
        } catch (e: BlockchainException) {
            eventLogger.send(GeneralEcosystemSdkError.create("EcosystemPresenter blockchainSource.getPublicAddress() thrown an exception"))
        }

        // Must come before processIntentExtras, so we can define if the intent was consumed already.
        processSavedInstanceState(savedInstanceState)
        processIntentExtras(extras)
    }

    private fun processSavedInstanceState(savedInstanceState: Bundle?) {
        this.visibleScreen = getVisibleScreen(savedInstanceState)
        this.isConsumedIntentExtras = getIsConsumedIntentExtras(savedInstanceState)
    }

    private fun getIsConsumedIntentExtras(savedInstanceState: Bundle?): Boolean {
        return savedInstanceState?.getBoolean(KEY_CONSUMED_INTENT_EXTRAS) ?: run { false }
    }

    private fun getVisibleScreen(savedInstanceState: Bundle?): Int {
        return savedInstanceState?.getInt(KEY_SCREEN_ID, ScreenId.NONE) ?: ScreenId.NONE
    }

    private fun processIntentExtras(extras: Bundle) {
        if (!isConsumedIntentExtras) {
            this.experience = getExperience(extras)
            this.isConsumedIntentExtras = true
        }
    }

    private fun getExperience(extras: Bundle?): Int {
        return extras?.getInt(KEY_ECOSYSTEM_EXPERIENCE, EcosystemExperience.NONE) ?: EcosystemExperience.NONE
    }

    override fun onAttach(view: IEcosystemView) {
        super.onAttach(view)
        val kinUserId = authDataSource.ecosystemUserID
        if (!settingsDataSource.isSawOnboarding(kinUserId)) {
            navigateToOnboarding()
        } else {
            if (experience == EcosystemExperience.ORDER_HISTORY) {
                experience = EcosystemExperience.NONE
                launchOrderHistory()
            } else {
                navigateToVisibleScreen(visibleScreen)
            }
        }
    }

    private fun navigateToOnboarding() {
        view?.let {
            navigator?.navigateToOnboarding()
        }
    }

    private fun launchOrderHistory() {
        view?.let {
            navigator?.navigateToOrderHistory(false)
        }
    }

    private fun navigateToVisibleScreen(visibleScreen: Int) {
        view?.let {
            when (visibleScreen) {
                ScreenId.ORDER_HISTORY -> navigator?.navigateToOrderHistory(false)
                ScreenId.MARKETPLACE, ScreenId.NONE -> navigator?.navigateToMarketplace(CustomAnimation.Builder().enter(R.anim.kinecosystem_slide_in_right).exit(R.anim.kinecosystem_slide_out_right).build())
                else -> navigator?.navigateToMarketplace(CustomAnimation.Builder().enter(R.anim.kinecosystem_slide_in_right).exit(R.anim.kinecosystem_slide_out_right).build())
            }
        }
    }

    override fun onStart() {
        blockchainSource.reconnectBalanceConnection()
        updateMenuSettingsIcon()
    }

    override fun onStop() {
        removeBalanceObserver()
    }

    override fun touchedOutside() {
        navigator?.close()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        with(outState) {
            putInt(KEY_SCREEN_ID, visibleScreen)
            putBoolean(KEY_CONSUMED_INTENT_EXTRAS, isConsumedIntentExtras)
        }
    }

    override fun onDetach() {
        super.onDetach()
        removeBalanceObserver()
    }

    private fun addBalanceObserver() {
        removeBalanceObserver()
        balanceObserver = object : Observer<Balance>() {
            override fun onChanged(value: Balance) {
                currentBalance = value
                if (isGreaterThenZero(value)) {
                    updateMenuSettingsIcon()
                }
            }
        }
        blockchainSource.addBalanceObserver(balanceObserver!!, false)
    }

    private fun isGreaterThenZero(value: Balance): Boolean {
        return value.amount.compareTo(BigDecimal.ZERO) == 1
    }

    private fun updateMenuSettingsIcon() {
        publicAddress?.let{
            if (!StringUtil.isEmpty(it)) {
                if (!settingsDataSource.isBackedUp(it)) {
                    if (isGreaterThenZero(currentBalance!!)) {
                        changeMenuTouchIndicator(true)
                        removeBalanceObserver()
                    } else {
                        addBalanceObserver()
                        changeMenuTouchIndicator(false)
                    }
                } else {
                    changeMenuTouchIndicator(false)
                }
            }
        }
    }

    private fun removeBalanceObserver() {
        balanceObserver?.let {
            blockchainSource.removeBalanceObserver(it, false)
            balanceObserver = null
        }
    }

    override fun backButtonPressed() {
        view?.navigateBack()
    }

    override fun visibleScreen(@ScreenId id: Int) {
        visibleScreen = id
    }

    private fun changeMenuTouchIndicator(isVisible: Boolean) {
        view?.showMenuTouchIndicator(isVisible)
    }

    override fun settingsMenuClicked() {
        navigator?.navigateToSettings()
    }

    override fun onMenuInitialized() {
        updateMenuSettingsIcon()
    }

    companion object {

        private const val KEY_SCREEN_ID = "screen_id"
        private const val KEY_CONSUMED_INTENT_EXTRAS = "consumed_intent_extras"
    }
}
