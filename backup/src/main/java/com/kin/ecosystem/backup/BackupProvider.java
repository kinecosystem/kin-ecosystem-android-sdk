package com.kin.ecosystem.backup;

public class BackupProvider {

	public static BackupManager create() {
		return new BackupManagerImpl();
	}
}
