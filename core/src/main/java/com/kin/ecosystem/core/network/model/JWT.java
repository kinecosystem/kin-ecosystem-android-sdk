package com.kin.ecosystem.core.network.model;

import com.google.gson.annotations.SerializedName;

public class JWT {

	@SerializedName("jwt")
	private String jwt;

	@SerializedName("sign_in_type")
	private String signInType = "jwt";

	public JWT(String jwt) {
		this.jwt = jwt;
	}

	public String getJwt() {
		return jwt;
	}

	public boolean isEmpty() {
		return jwt == null || jwt.isEmpty();
	}
}
