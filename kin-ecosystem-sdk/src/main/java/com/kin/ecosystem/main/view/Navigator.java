package com.kin.ecosystem.main.view;

import android.content.Context;
import com.kin.ecosystem.main.INavigator;

class Navigator implements INavigator {

	private final Context context;

	Navigator(final Context context) {
		this.context = context;
	}

	@Override
	public void navigateToMarketplace() {

	}

	@Override
	public void navigateToOrderHistory(boolean isFirstSpendOrder) {

	}
}
