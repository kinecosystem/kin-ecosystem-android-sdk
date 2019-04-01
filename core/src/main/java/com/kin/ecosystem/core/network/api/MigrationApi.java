package com.kin.ecosystem.core.network.api;

import com.google.gson.reflect.TypeToken;
import com.kin.ecosystem.core.data.auth.AuthRepository;
import com.kin.ecosystem.core.network.ApiCallback;
import com.kin.ecosystem.core.network.ApiClient;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.ApiResponse;
import com.kin.ecosystem.core.network.model.MigrationInfo;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;

public class MigrationApi extends Api {

	public String getBlockchainVersionSync(String X_REQUEST_ID) throws ApiException {
		Call call = getBlockchainVersion(X_REQUEST_ID);
		ApiResponse<String> response = apiClient.execute(call);
		return response.getData();
	}

	public Call getBlockchainVersionAsync(String X_REQUEST_ID, final ApiCallback<String> callback) throws ApiException {
		Call call = getBlockchainVersion(X_REQUEST_ID);
		Type localVarReturnType = new TypeToken<String>() {
		}.getType();
		apiClient.executeAsync(call, localVarReturnType, callback);
		return call;
	}

	public MigrationInfo getMigrationInfoSync(String publicAddress) throws ApiException {
		Call call = getMigrationInfo(publicAddress);
		ApiResponse<MigrationInfo> response = apiClient.execute(call);
		return response.getData();
	}

	public Call getMigrationInfoAsync(String publicAddress, final ApiCallback<MigrationInfo> callback)
		throws ApiException {
		Call call = getMigrationInfo(publicAddress);
		apiClient.executeAsync(call, callback);
		return call;

	}


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

	private Call getMigrationInfo(String publicAddress) throws ApiException {
		String path = "/migration/info/" + AuthRepository.getInstance().getAppID() + "/" + publicAddress;
		Map<String, String> localVarHeaderParams = new HashMap<String, String>();

		localVarHeaderParams.put("X-REQUEST-ID", apiClient.parameterToString(""));

		return apiClient
			.buildCall(path, ApiClient.GET, null, null,
				null,
				localVarHeaderParams, null, null);
	}


}
