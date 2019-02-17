package com.kin.ecosystem.core.data.settings;

public interface SettingsDataSource {

	void setIsBackedUp(final String publicAddress, boolean isBackedUp);

	boolean isBackedUp(final String publicAddress);

	interface Local {

		void setIsBackedUp(final String publicAddress, boolean isBackedUp);

		boolean isBackedUp(final String publicAddress);
	}
}
