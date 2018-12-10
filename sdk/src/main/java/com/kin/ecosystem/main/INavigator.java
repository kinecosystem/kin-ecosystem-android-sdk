package com.kin.ecosystem.main;

public interface INavigator {

	void navigateToMarketplace(boolean addAnimation);

	void navigateToOrderHistory(boolean isFirstSpendOrder, boolean launchMarketplaceToStack);

	void navigateToSettings();

	void close();
}
