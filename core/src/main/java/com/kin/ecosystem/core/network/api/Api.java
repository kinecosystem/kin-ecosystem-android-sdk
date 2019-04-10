package com.kin.ecosystem.core.network.api;

import com.kin.ecosystem.core.data.internal.ConfigurationImpl;
import com.kin.ecosystem.core.network.ApiClient;

/* package */ class Api {
	protected ApiClient apiClient;

	public Api() {
		this(ConfigurationImpl.getInstance().getDefaultApiClient());
	}

	public Api(ApiClient apiClient) {
		this.apiClient = apiClient;
	}

	public ApiClient getApiClient() {
		return apiClient;
	}

	public void setApiClient(ApiClient apiClient) {
		this.apiClient = apiClient;
	}
}
