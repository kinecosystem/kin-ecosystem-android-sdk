package com.kin.ecosystem.main.presenter


import android.os.Bundle
import com.kin.ecosystem.EcosystemExperience
import com.kin.ecosystem.Kin.KEY_ECOSYSTEM_EXPERIENCE
import com.kin.ecosystem.R
import com.kin.ecosystem.base.BasePresenter
import com.kin.ecosystem.base.CustomAnimation
import com.kin.ecosystem.base.customAnimation
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
                         private val blockchainSource: BlockchainSource,
                         private val navigator: INavigator?, savedInstanceState: Bundle?, extras: Bundle) : BasePresenter<IEcosystemView>(), IEcosystemPresenter {
    @ScreenId
    private var visibleScreen: Int = ScreenId.NONE
    @EcosystemExperience
    private var experience: Int = EcosystemExperience.NONE
    private var isConsumedIntentExtras: Boolean = false

    init {
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
            navigator?.navigateToOrderHistory(customAnimation {
                enter = 0
                exit = R.anim.kinecosystem_slide_out_right
            }, addToBackStack = false)
        }
    }

    private fun navigateToVisibleScreen(visibleScreen: Int) {
        view?.let {
            when (visibleScreen) {
                ScreenId.ORDER_HISTORY -> navigator?.navigateToOrderHistory(customAnimation {
                    enter = R.anim.kinecosystem_slide_in_right
                    exit = R.anim.kinecosystem_slide_out_left
                    popEnter = R.anim.kinrecovery_slide_in_left
                    popExit = R.anim.kinecosystem_slide_out_right
                },addToBackStack = false)
                ScreenId.MARKETPLACE,
                ScreenId.NONE ->
                    navigator?.navigateToMarketplace()
                else -> navigator?.navigateToMarketplace(customAnimation {
                    enter = R.anim.kinecosystem_slide_in_right
                    exit = R.anim.kinecosystem_slide_out_right
                })
            }
        }
    }

    override fun onStart() {
        blockchainSource.reconnectBalanceConnection()
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

    override fun backButtonPressed() {
        view?.navigateBack()
    }

    override fun visibleScreen(@ScreenId id: Int) {
        visibleScreen = id
    }

    companion object {
        private const val KEY_SCREEN_ID = "screen_id"
        private const val KEY_CONSUMED_INTENT_EXTRAS = "consumed_intent_extras"
    }
}
