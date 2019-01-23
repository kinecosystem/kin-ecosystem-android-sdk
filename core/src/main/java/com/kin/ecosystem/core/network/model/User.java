package com.kin.ecosystem.core.network.model;

import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

public class User {

	//Data check
	@SerializedName("created_date")
	private String createdDate;

	@SerializedName("stats")
	@Nullable
	private UserStats userStats;

	@SerializedName("current_wallet")
	private String currentWallet;

	public User(String createdDate, String currentWallet) {
		this.createdDate = createdDate;
		this.currentWallet = currentWallet;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public UserStats getUserStats() {
		return userStats;
	}

	public void setUserStats(UserStats userStats) {
		this.userStats = userStats;
	}

	public String getCurrentWallet() {
		return currentWallet;
	}

	public void setCurrentWallet(String currentWallet) {
		this.currentWallet = currentWallet;
	}
}
