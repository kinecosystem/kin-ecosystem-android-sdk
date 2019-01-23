package com.kin.ecosystem.core.data.auth;

public class JwtBody {

	private String appId;
	private String userId;
	private String deviceId;

	public JwtBody(String appId, String userId, String deviceId) {
		this.appId = appId;
		this.userId = userId;
		this.deviceId = deviceId;
	}

	public String getAppId() {
		return appId;
	}

	public String getUserId() {
		return userId;
	}

	public String getDeviceId() {
		return deviceId;
	}
}
