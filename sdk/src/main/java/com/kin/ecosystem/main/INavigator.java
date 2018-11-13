package com.kin.ecosystem.main;

public interface INavigator {

	void navigateToMarketplace();

	void navigateToOrderHistory(boolean isFirstSpendOrder);

	void navigateToSettings();

	void close();
}
