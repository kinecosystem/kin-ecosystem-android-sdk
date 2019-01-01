package com.kin.ecosystem.core.network.model;

import com.google.gson.annotations.SerializedName;

public class User {

	//Data check
	@SerializedName("created_date")
	private String createdDate;

	@SerializedName("stats")
	private UserStats userStats;

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
}
