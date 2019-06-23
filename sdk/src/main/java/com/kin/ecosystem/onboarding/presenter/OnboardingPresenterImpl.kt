package com.kin.ecosystem.onboarding.presenter

import android.os.Bundle
import com.kin.ecosystem.EcosystemExperience
import com.kin.ecosystem.Kin.KEY_ECOSYSTEM_EXPERIENCE
import com.kin.ecosystem.R
import com.kin.ecosystem.base.BaseFragmentPresenter
import com.kin.ecosystem.base.customAnimation
import com.kin.ecosystem.common.Observer
import com.kin.ecosystem.core.Log
import com.kin.ecosystem.core.Logger
import com.kin.ecosystem.core.accountmanager.AccountManager
import com.kin.ecosystem.core.accountmanager.AccountManager.*
import com.kin.ecosystem.core.bi.EventLogger
import com.kin.ecosystem.core.bi.events.APageViewed
import com.kin.ecosystem.core.bi.events.ContinueButtonTapped
import com.kin.ecosystem.core.bi.events.PageCloseTapped
import com.kin.ecosystem.core.data.auth.AuthDataSource
import com.kin.ecosystem.core.data.settings.SettingsDataSource
import com.kin.ecosystem.main.INavigator
import com.kin.ecosystem.onboarding.view.IOnboardingView
import java.util.*


class OnboardingPresenterImpl(private val accountManager: AccountManager,
                              private val authDataSource: AuthDataSource,
                              private val settingsDataSource: SettingsDataSource,
                              navigator: INavigator?,
                              private val eventLogger: EventLogger,
                              private val timer: Timer, extras: Bundle?) : BaseFragmentPresenter<IOnboardingView>(navigator), OnboardingPresenter {

    @EcosystemExperience
    private val nextExperience: Int
    private val accountStateObserver = object : Observer<Int>() {
        override fun onChanged(@AccountState value: Int?) {
            Logger.log(Log().withTag(TAG).put("accountStateObserver", value))
            if (value == CREATION_COMPLETED || value == ERROR) {
                removeAccountStateObserver()
                cancelTimeoutTask()

                if (value == CREATION_COMPLETED) {
                    navigateToExperience()
                } else {
                    Logger.log(Log().withTag(TAG).text("accountStateObserver -> showTryAgainLater"))
                    showTryAgainLater()
                    stopLoading(true)
                }
            }
        }
    }

    private var timeOutTask: TimerTask? = null

    init {
        this.nextExperience = getExperience(extras)
    }

    private fun getExperience(extras: Bundle?): Int {
        return extras?.getInt(KEY_ECOSYSTEM_EXPERIENCE, EcosystemExperience.MARKETPLACE) ?: EcosystemExperience.MARKETPLACE
    }

    override fun onAttach(view: IOnboardingView) {
        super.onAttach(view)
        eventLogger.send(APageViewed.create(APageViewed.PageName.ONBOARDING))
    }

    override fun onDetach() {
        super.onDetach()
        removeAccountStateObserver()
        cancelTimeoutTask()
    }

    private fun removeAccountStateObserver() {
        Logger.log(Log().withTag(TAG).text("removeAccountStateObserver"))
        accountManager.removeAccountStateObserver(accountStateObserver)
    }

    override fun getStartedClicked() {
        Logger.log(Log().withTag(TAG).text("getStartedClicked").put("accountState", accountManager.accountState))
        eventLogger.send(ContinueButtonTapped.create(ContinueButtonTapped.PageName.ONBOARDING,
                ContinueButtonTapped.PageContinue.ONBOARDING_CONTINUE_TO_MAIN_PAGE, null))
        if (accountManager.isAccountCreated) {
            navigateToExperience()
        } else {
            animateLoading()
            startCreationTimeout(TIME_OUT_DURATION)

            Logger.log(Log().withTag(TAG).text("addAccountStateObserver"))
            accountManager.addAccountStateObserver(accountStateObserver)

            if (accountManager.accountState == AccountManager.ERROR) {
                Logger.log(Log().withTag(TAG).text("accountManager -> retry"))
                accountManager.retry()
            }
        }

    }

    private fun createTimeOutTimerTask(): TimerTask {
        return object : TimerTask() {
            override fun run() {
                Logger.log(Log().withTag(TAG).text("Account creation time out"))
                stopLoading(true)
                removeAccountStateObserver()
                showTryAgainLater()
            }
        }
    }

    private fun cancelTimeoutTask() {
        timeOutTask?.let {
            it.cancel()
            timeOutTask = null
        }
        timer.purge()
    }

    private fun startCreationTimeout(sec: Int) {
        cancelTimeoutTask()
        timeOutTask = createTimeOutTimerTask()
        timer.schedule(timeOutTask, (sec * SEC_IN_MILLI).toLong())
    }

    private fun showTryAgainLater() {
        showToast(OnboardingPresenter.Message.TRY_AGAIN)
    }

    private fun animateLoading() {
        view?.animateLoading()
    }


    private fun stopLoading(reset: Boolean) {
        view?.stopLoading(reset)
    }

    private fun showToast(msg: OnboardingPresenter.Message) {
        view?.showToast(msg)
    }

    private fun navigateToExperience() {
        if (accountManager.isAccountCreated) {
            view?.let {
                settingsDataSource.setSawOnboarding(authDataSource.ecosystemUserID)
                navigator?.let { navigator ->
                    when (nextExperience) {
                        EcosystemExperience.MARKETPLACE -> navigator.navigateToMarketplace(customAnimation {
                            enter = R.anim.kinecosystem_fade_in
                            exit = R.anim.kinecosystem_fade_out
                        })
                        EcosystemExperience.ORDER_HISTORY -> navigator.navigateToOrderHistory(addToBackStack = false)
                    }
                }
            }
        }
    }

    override fun closeButtonPressed() {
        view?.let {
            navigator?.close()
            eventLogger.send(PageCloseTapped.create(PageCloseTapped.ExitType.X_BUTTON, PageCloseTapped.PageName.ONBOARDING))
        }
    }

    companion object {

        private val TAG = OnboardingPresenterImpl::class.java.simpleName

        private const val SEC_IN_MILLI = 1000
        private const val TIME_OUT_DURATION = 20
    }
}
