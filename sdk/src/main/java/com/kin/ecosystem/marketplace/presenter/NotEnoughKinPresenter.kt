package com.kin.ecosystem.marketplace.presenter

import com.kin.ecosystem.R
import com.kin.ecosystem.base.BaseFragmentPresenter
import com.kin.ecosystem.base.customAnimation
import com.kin.ecosystem.core.bi.EventLogger
import com.kin.ecosystem.core.bi.events.APageViewed
import com.kin.ecosystem.core.bi.events.ContinueButtonTapped
import com.kin.ecosystem.core.bi.events.PageCloseTapped
import com.kin.ecosystem.core.data.auth.AuthDataSource
import com.kin.ecosystem.core.data.settings.SettingsDataSource
import com.kin.ecosystem.main.INavigator
import com.kin.ecosystem.marketplace.view.INotEnoughKinView

class NotEnoughKinPresenter(override var navigator: INavigator?,
                            private val eventLogger: EventLogger,
                            private val authDataSource: AuthDataSource,
                            private val settingsDataSource: SettingsDataSource)
	: BaseFragmentPresenter<INotEnoughKinView>(navigator), INotEnoughKinPresenter {

	override fun onEarnMoreKinClicked() {
		eventLogger.send(ContinueButtonTapped.create(ContinueButtonTapped.PageName.DIALOGS_NOT_ENOUGH_KIN,
				ContinueButtonTapped.PageContinue.NOT_ENOUGH_KIN_CONTINUE_BUTTON, null))

		val kinUserId = authDataSource.ecosystemUserID
		if (!settingsDataSource.isSawOnboarding(kinUserId)) {
			navigator?.navigateToOnboarding()
		} else {
			navigator?.navigateToMarketplace(customAnimation = customAnimation {
				enter = R.anim.kinecosystem_slide_in_right
			})
		}
	}

	override fun onAttach(view: INotEnoughKinView) {
		super.onAttach(view)
		eventLogger.send(APageViewed.create(APageViewed.PageName.DIALOGS_NOT_ENOUGH_KIN))
	}

	override fun closeClicked() {
		eventLogger.send(PageCloseTapped.create(PageCloseTapped.ExitType.X_BUTTON, PageCloseTapped.PageName.DIALOGS_NOT_ENOUGH_KIN))
		navigator?.close()
	}
}