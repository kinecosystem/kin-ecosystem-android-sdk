package com.kin.ecosystem.main;

public interface INavigator {

	void navigateToMarketplace(boolean addAnimation);

	void navigateToOrderHistory(boolean isFirstSpendOrder);

	void navigateToSettings();

	void close();
}
