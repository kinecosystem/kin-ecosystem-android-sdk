package com.kin.ecosystem.marketplace.presenter

import com.kin.ecosystem.core.bi.EventLogger
import com.kin.ecosystem.core.bi.events.APageViewed
import com.kin.ecosystem.core.bi.events.ContinueButtonTapped
import com.kin.ecosystem.core.bi.events.PageCloseTapped
import com.kin.ecosystem.core.data.auth.AuthDataSource
import com.kin.ecosystem.core.data.settings.SettingsDataSource
import com.kin.ecosystem.main.INavigator
import com.kin.ecosystem.marketplace.view.INotEnoughKinView
import com.nhaarman.mockitokotlin2.*
import junit.framework.Assert.assertEquals
import kin.ecosystem.test.base.BaseTestClass
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class NotEnoughKinPresenterTest: BaseTestClass() {

	private val navigator: INavigator = mock()
	private val eventLogger: EventLogger = mock()
	private val authDataSource: AuthDataSource = mock {
		on { ecosystemUserID } doAnswer { "some_eco_user_id" }
	}
	private val settingsDataSource: SettingsDataSource = mock()

	private lateinit var notEnoughKinPresenter: NotEnoughKinPresenter

	@Before
	override fun setUp() {
		super.setUp()
		notEnoughKinPresenter = NotEnoughKinPresenter(navigator, eventLogger, authDataSource, settingsDataSource)
	}

	@Test
	fun `on attach send page viewed event`() {
		val view : INotEnoughKinView = mock()
		notEnoughKinPresenter.onAttach(view)

		val eventCaptor = argumentCaptor<APageViewed>()
		verify(eventLogger).send(eventCaptor.capture())

		eventCaptor.firstValue.apply {
			assertEquals(APageViewed.PageName.DIALOGS_NOT_ENOUGH_KIN, pageName)
		}
	}

	@Test
	fun `on close clicked send page close tapped event`() {
		notEnoughKinPresenter.closeClicked()

		val eventCaptor = argumentCaptor<PageCloseTapped>()
		verify(eventLogger).send(eventCaptor.capture())

		eventCaptor.firstValue.apply {
			assertEquals(PageCloseTapped.ExitType.X_BUTTON, exitType)
			assertEquals(PageCloseTapped.PageName.DIALOGS_NOT_ENOUGH_KIN, pageName)
		}
	}

	@Test
	fun `earn more clicked send continue button tapped event`() {
		notEnoughKinPresenter.onEarnMoreKinClicked()

		val eventCaptor = argumentCaptor<ContinueButtonTapped>()
		verify(eventLogger).send(eventCaptor.capture())

		eventCaptor.firstValue.apply {
			assertEquals(ContinueButtonTapped.PageContinue.NOT_ENOUGH_KIN_CONTINUE_BUTTON, pageContinue)
			assertEquals(ContinueButtonTapped.PageName.DIALOGS_NOT_ENOUGH_KIN, pageName)
		}
	}

	@Test
	fun `earn more kin clicked, user didn't see onboarding, navigate to onboarding`() {
		whenever(settingsDataSource.hasSeenOnboarding(any())).thenReturn(false)
		notEnoughKinPresenter.onEarnMoreKinClicked()
		verify(navigator).navigateToOnboarding()
		verifyNoMoreInteractions(navigator)
	}

	@Test
	fun `earn more kin clicked, user saw onboarding, navigate to marketplace`() {
		whenever(settingsDataSource.hasSeenOnboarding(any())).thenReturn(true)
		notEnoughKinPresenter.onEarnMoreKinClicked()
		verify(navigator).navigateToMarketplace(any())
		verifyNoMoreInteractions(navigator)
	}
}