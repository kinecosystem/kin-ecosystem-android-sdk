package com.kin.ecosystem.core.data.settings;

import android.support.annotation.NonNull;

public class SettingsDataSourceImpl implements SettingsDataSource {

	private final SettingsDataSource.Local local;

	public SettingsDataSourceImpl(@NonNull final SettingsDataSource.Local local) {
		this.local = local;
	}

	@Override
	public void setIsBackedUp(final String publicAddress, boolean isBackedUp) {
		local.setIsBackedUp(publicAddress, isBackedUp);
	}

	@Override
	public boolean isBackedUp(final String publicAddress) {
		return local.isBackedUp(publicAddress);
	}

}
