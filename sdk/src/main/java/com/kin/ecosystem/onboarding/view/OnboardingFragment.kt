package com.kin.ecosystem.onboarding.view

import android.annotation.SuppressLint
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.kin.ecosystem.R
import com.kin.ecosystem.base.AnimConsts
import com.kin.ecosystem.core.accountmanager.AccountManagerImpl
import com.kin.ecosystem.core.bi.EventLoggerImpl
import com.kin.ecosystem.core.data.auth.AuthRepository
import com.kin.ecosystem.core.data.settings.SettingsDataSourceImpl
import com.kin.ecosystem.core.data.settings.SettingsDataSourceLocal
import com.kin.ecosystem.main.INavigator
import com.kin.ecosystem.onboarding.presenter.OnboardingPresenter
import com.kin.ecosystem.onboarding.presenter.OnboardingPresenter.Message
import com.kin.ecosystem.onboarding.presenter.OnboardingPresenterImpl
import java.util.*

class OnboardingFragment: Fragment(), IOnboardingView {

    private lateinit var onboardingPresenter: OnboardingPresenter
    private lateinit var navigator: INavigator

    private lateinit var welcomeImage: ImageView
    private lateinit var closeButton: ImageView
    private lateinit var letsGetStartedBtn: Button
    private lateinit var loadingText: TextView
    private lateinit var loadingAVD: ImageView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.kinecosystem_fragment_onboarding, container, false)
        onboardingPresenter = OnboardingPresenterImpl(AccountManagerImpl.getInstance(),
                AuthRepository.getInstance(),
                SettingsDataSourceImpl(SettingsDataSourceLocal(context.applicationContext)),
                navigator, EventLoggerImpl.getInstance(), Timer(), arguments)
        onboardingPresenter.onAttach(this)
        initViews(root)
        return root
    }

    @SuppressLint("NewApi")
    private fun initViews(root: View) {
        welcomeImage = root.findViewById(R.id.welcome_image)
        loadingText = root.findViewById(R.id.loading_text)
        loadingAVD = root.findViewById(R.id.loading_image);
        closeButton = root.findViewById<ImageView>(R.id.close_button).apply {
            setOnClickListener { onboardingPresenter.closeButtonPressed() }
        }
        letsGetStartedBtn = root.findViewById<Button>(R.id.lets_start_button).apply {
            setOnClickListener { onboardingPresenter.getStartedClicked() }
        }
    }


    @SuppressLint("NewApi")
    override fun animateLoading() {
        with(letsGetStartedBtn) {
            animate().alpha(AnimConsts.Value.ALPHA_0)
                    .setDuration(AnimConsts.Duration.FADE_ANIM)
                    .withEndAction { visibility = View.INVISIBLE }.start()
        }
        with(loadingText) {
            visibility = View.VISIBLE
            animate().alpha(AnimConsts.Value.ALPHA_1).setDuration(AnimConsts.Duration.FADE_ANIM).start()
        }
        with(loadingAVD) {
            visibility = View.VISIBLE
            animate().alpha(AnimConsts.Value.ALPHA_1).setDuration(AnimConsts.Duration.FADE_ANIM).start()
            (drawable as AnimatedVectorDrawable).start()
        }
    }

    @SuppressLint("NewApi")
    override fun stopLoading(reset: Boolean) {
        activity.runOnUiThread {
            with(loadingText) {
                animate().alpha(AnimConsts.Value.ALPHA_0)
                        .setDuration(AnimConsts.Duration.FADE_ANIM)
                        .withStartAction { visibility = View.INVISIBLE }.start()
            }
            with(loadingAVD) {
                animate().alpha(AnimConsts.Value.ALPHA_0)
                        .setDuration(AnimConsts.Duration.FADE_ANIM)
                        .withStartAction {
                            visibility = View.INVISIBLE
                            (drawable as AnimatedVectorDrawable).stop()
                        }
                        .start()

            }
            with(letsGetStartedBtn) {
                visibility = View.VISIBLE
                animate().alpha(AnimConsts.Value.ALPHA_1).setDuration(AnimConsts.Duration.FADE_ANIM).start()
            }
        }
    }

    override fun showToast(msg: OnboardingPresenter.Message) {
        activity.runOnUiThread {
            Toast.makeText(context, getMessageResId(msg), Toast.LENGTH_SHORT).show()
        }
    }

    @StringRes
    private fun getMessageResId(msg: OnboardingPresenter.Message): Int {
        return when (msg) {
            Message.TRY_AGAIN -> R.string.kinecosystem_try_again_later
            Message.SOMETHING_WENT_WRONG -> R.string.kinecosystem_something_went_wrong
        }
    }

    override fun onDestroy() {
        onboardingPresenter.onDetach()
        super.onDestroy()
    }

    companion object {

        fun getInstance(extras: Bundle, navigator: INavigator): OnboardingFragment {
            return OnboardingFragment().apply {
                this.arguments = extras
                this.navigator = navigator
            }
        }
    }
}
