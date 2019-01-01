package com.kin.ecosystem.core.network.model;

import com.google.gson.annotations.SerializedName;

public class AccountInfo {

	@SerializedName("auth")
	private AuthToken authToken;

	@SerializedName("user")
	private User user;


	public AuthToken getAuthToken() {
		return authToken;
	}

	public void setAuthToken(AuthToken authToken) {
		this.authToken = authToken;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
