package com.kin.ecosystem.core.data.settings;

public interface SettingsDataSource {

	void setIsBackedUp(boolean isBackedUp);

	boolean isBackedUp();

	interface Local {

		void setIsBackedUp(boolean isBackedUp);

		boolean isBackedUp();

	}
}
