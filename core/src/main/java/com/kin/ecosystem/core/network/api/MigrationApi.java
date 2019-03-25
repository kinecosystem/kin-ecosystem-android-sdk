package com.kin.ecosystem.core.network.api;

import com.kin.ecosystem.core.data.auth.AuthRepository;
import com.kin.ecosystem.core.network.ApiCallback;
import com.kin.ecosystem.core.network.ApiClient;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;

public class MigrationApi extends Api {
	private Call getBlockchainVersion(String X_REQUEST_ID) throws ApiException {
		String path = "/applications/" + AuthRepository.getInstance().getAppID() + "/blockchain_version";
		Map<String, String> localVarHeaderParams = new HashMap<String, String>();

		if (X_REQUEST_ID != null) {
			localVarHeaderParams.put("X-REQUEST-ID", apiClient.parameterToString(X_REQUEST_ID));
		}

		return apiClient
			.buildCall(path, ApiClient.GET, null, null,
				null,
				localVarHeaderParams, null, null);
	}

	public String getBlockchainVersionSync(String X_REQUEST_ID) throws ApiException {
		Call call = getBlockchainVersion(X_REQUEST_ID);
		ApiResponse<String> response = apiClient.execute(call);
		return response.getData();
	}

	public Call getBlockchainVersionAsync(String X_REQUEST_ID, final ApiCallback<String> callback) throws ApiException {
		Call call = getBlockchainVersion(X_REQUEST_ID);
		apiClient.executeAsync(call, callback);
		return call;
	}
}
