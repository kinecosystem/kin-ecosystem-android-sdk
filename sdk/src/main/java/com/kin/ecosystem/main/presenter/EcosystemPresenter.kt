package com.kin.ecosystem.main.presenter

import android.os.Bundle
import com.kin.ecosystem.EcosystemExperience
import com.kin.ecosystem.Kin.KEY_ECOSYSTEM_EXPERIENCE
import com.kin.ecosystem.R
import com.kin.ecosystem.base.BasePresenter
import com.kin.ecosystem.base.customAnimation
import com.kin.ecosystem.core.bi.EventLogger
import com.kin.ecosystem.core.bi.events.PageCloseTapped
import com.kin.ecosystem.core.data.auth.AuthDataSource
import com.kin.ecosystem.core.data.blockchain.BlockchainSource
import com.kin.ecosystem.core.data.settings.SettingsDataSource
import com.kin.ecosystem.main.INavigator
import com.kin.ecosystem.main.ScreenId
import com.kin.ecosystem.main.view.IEcosystemView

class EcosystemPresenter(private val authDataSource: AuthDataSource,
                         private val settingsDataSource: SettingsDataSource,
                         private val blockchainSource: BlockchainSource,
                         private val eventLogger: EventLogger,
                         private val navigator: INavigator?, savedInstanceState: Bundle?, extras: Bundle) : BasePresenter<IEcosystemView>(), IEcosystemPresenter {

	private var visibleScreen: ScreenId = ScreenId.NONE
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

	private fun getVisibleScreen(savedInstanceState: Bundle?): ScreenId {
		return ScreenId.valueOf(savedInstanceState?.getString(KEY_SCREEN_ID, ScreenId.NONE.name) ?: ScreenId.NONE.name)
	}

	private fun processIntentExtras(extras: Bundle) {
		if (!isConsumedIntentExtras) {
			this.experience = getExperience(extras)
			this.visibleScreen = getVisibleScreen(extras)
			this.isConsumedIntentExtras = true
		}
	}

	private fun getExperience(extras: Bundle?): Int {
		return extras?.getInt(KEY_ECOSYSTEM_EXPERIENCE, EcosystemExperience.NONE) ?: EcosystemExperience.NONE
	}

	override fun onAttach(view: IEcosystemView) {
		super.onAttach(view)
		if (visibleScreen == ScreenId.NOT_ENOUGH_KIN) {
			// first to show should be without animation
			navigator?.showNotEnoughKin(false)
		} else {
			val kinUserId = authDataSource.ecosystemUserID
			if (!settingsDataSource.isSawOnboarding(kinUserId)) {
				navigateToVisibleScreen(ScreenId.ONBOARDING)
			} else {
				if (experience == EcosystemExperience.ORDER_HISTORY) {
					experience = EcosystemExperience.NONE
					launchOrderHistory()
				} else {
					navigateToVisibleScreen(visibleScreen)
				}
			}
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

	private fun navigateToVisibleScreen(visibleScreen: ScreenId) {
		view?.let {
			when (visibleScreen) {
				ScreenId.NOT_ENOUGH_KIN -> navigator?.showNotEnoughKin()
				ScreenId.ONBOARDING -> navigator?.navigateToOnboarding()
				ScreenId.ORDER_HISTORY -> navigator?.navigateToOrderHistory(customAnimation {
					enter = R.anim.kinecosystem_slide_in_right
					exit = R.anim.kinecosystem_slide_out_left
					popEnter = R.anim.kinrecovery_slide_in_left
					popExit = R.anim.kinecosystem_slide_out_right
				}, addToBackStack = false)
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
		sendPageCloseEvent(PageCloseTapped.ExitType.BACKGROUND_APP)
	}

	override fun onSaveInstanceState(outState: Bundle) {
		with(outState) {
			putString(KEY_SCREEN_ID, visibleScreen.name)
			putBoolean(KEY_CONSUMED_INTENT_EXTRAS, isConsumedIntentExtras)
		}
	}

	override fun backButtonPressed() {
		view?.navigateBack()
		sendPageCloseEvent(PageCloseTapped.ExitType.ANDROID_NAVIGATOR)
	}

	private fun sendPageCloseEvent(exitType: PageCloseTapped.ExitType) {
		var pageName: PageCloseTapped.PageName? = null
		when (visibleScreen) {
			ScreenId.MARKETPLACE -> pageName = PageCloseTapped.PageName.MAIN_PAGE
			ScreenId.ORDER_HISTORY -> pageName = PageCloseTapped.PageName.MY_KIN_PAGE
			ScreenId.SETTINGS -> pageName = PageCloseTapped.PageName.SETTINGS
			ScreenId.ONBOARDING -> pageName = PageCloseTapped.PageName.ONBOARDING
			ScreenId.NOT_ENOUGH_KIN -> pageName = PageCloseTapped.PageName.DIALOGS_NOT_ENOUGH_KIN
		}
		eventLogger.send(PageCloseTapped.create(exitType, pageName))
	}

	override fun visibleScreen(id: ScreenId) {
		visibleScreen = id
	}

	companion object {
		const val KEY_SCREEN_ID = "screen_id"
		private const val KEY_CONSUMED_INTENT_EXTRAS = "consumed_intent_extras"
	}
}
