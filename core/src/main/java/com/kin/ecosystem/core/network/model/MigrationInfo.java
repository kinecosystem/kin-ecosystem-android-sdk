package com.kin.ecosystem.core.network.model;

import com.google.gson.annotations.SerializedName;

public class MigrationInfo {

	@SerializedName("app_blockchain_version")
	private String blockchainVersion;

	@SerializedName("should_migrate")
	private boolean shouldMigrate;

	@SerializedName("restore_allowed")
	private boolean isRestorable;

	public String getBlockchainVersion() {
		return blockchainVersion;
	}

	public boolean shouldMigrate() {
		return shouldMigrate;
	}

	public boolean isRestorable() {
		return isRestorable;
	}
}
