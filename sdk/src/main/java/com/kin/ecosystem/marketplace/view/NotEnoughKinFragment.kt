package com.kin.ecosystem.marketplace.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.kin.ecosystem.R
import com.kin.ecosystem.base.KinEcosystemBaseFragment
import com.kin.ecosystem.core.bi.EventLoggerImpl
import com.kin.ecosystem.core.data.auth.AuthRepository
import com.kin.ecosystem.core.data.settings.SettingsDataSourceImpl
import com.kin.ecosystem.core.data.settings.SettingsDataSourceLocal
import com.kin.ecosystem.main.INavigator
import com.kin.ecosystem.marketplace.presenter.INotEnoughKinPresenter
import com.kin.ecosystem.marketplace.presenter.NotEnoughKinPresenter

class NotEnoughKinFragment : KinEcosystemBaseFragment<INotEnoughKinPresenter, INotEnoughKinView>(), INotEnoughKinView {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val root = inflater.inflate(R.layout.kinecosystem_fragment_not_enough_kin, container, false)
		initViews(root)
		presenter = NotEnoughKinPresenter(navigator, EventLoggerImpl.getInstance(),
				AuthRepository.getInstance(),
				SettingsDataSourceImpl(SettingsDataSourceLocal(context)))
		return root
	}

	private fun initViews(root: View) {
		root.findViewById<ImageView>(R.id.close_btn).setOnClickListener { presenter?.closeClicked() }
		root.findViewById<Button>(R.id.earn_kin_button).setOnClickListener { presenter?.onEarnMoreKinClicked() }
	}

	companion object {
		fun newInstance(navigator: INavigator): NotEnoughKinFragment {
			val notEnoughKinFragment = NotEnoughKinFragment()
			notEnoughKinFragment.navigator = navigator
			return notEnoughKinFragment
		}
	}
}