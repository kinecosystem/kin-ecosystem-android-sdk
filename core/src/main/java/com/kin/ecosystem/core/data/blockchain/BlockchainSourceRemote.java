package com.kin.ecosystem.core.data.blockchain;

import android.support.annotation.NonNull;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource.Remote;
import com.kin.ecosystem.core.network.ApiCallback;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.api.MigrationApi;
import com.kin.ecosystem.core.network.model.MigrationInfo;
import com.kin.ecosystem.core.util.ExecutorsUtil;
import java.util.List;
import java.util.Map;
import kin.sdk.migration.common.KinSdkVersion;

public class BlockchainSourceRemote implements Remote {
	private static volatile BlockchainSourceRemote instance;

	private MigrationApi migrationApi;
	private ExecutorsUtil executorsUtil;

	private BlockchainSourceRemote(@NonNull ExecutorsUtil executorsUtil) {
		this.migrationApi = new MigrationApi();
		this.executorsUtil = executorsUtil;
	}

	public static BlockchainSourceRemote getInstance(@NonNull ExecutorsUtil executorsUtil) {
		if (instance == null) {
			synchronized (BlockchainSourceRemote.class) {
				if (instance == null) {
					instance = new BlockchainSourceRemote(executorsUtil);
				}
			}
		}
		return instance;
	}

	@Override
	public KinSdkVersion getBlockchainVersion() throws ApiException {
		String version = migrationApi.getBlockchainVersionSync("");
		return KinSdkVersion.get(version);
	}

	@Override
	public void getBlockchainVersion(@NonNull final Callback<KinSdkVersion, ApiException> callback) {
		try {
			migrationApi.getBlockchainVersionAsync("", new ApiCallback<String>() {
				@Override
				public void onFailure(final ApiException e, final int statusCode, final Map<String, List<String>> responseHeaders) {
					executorsUtil.mainThread().execute(new Runnable() {
						@Override
						public void run() {
							callback.onFailure(e);
						}
					});
				}

				@Override
				public void onSuccess(final String result, final int statusCode, final Map<String, List<String>> responseHeaders) {
					executorsUtil.mainThread().execute(new Runnable() {
						@Override
						public void run() {
							callback.onResponse(KinSdkVersion.get(result));
						}
					});
				}
			});
		} catch (final ApiException e) {
			executorsUtil.mainThread().execute(new Runnable() {
				@Override
				public void run() {
					callback.onFailure(e);
				}
			});
		}
	}

	@Override
	public MigrationInfo getMigrationInfo(@NonNull String publicAddress) throws ApiException {
		return migrationApi.getMigrationInfoSync(publicAddress);
	}

	@Override
	public void getMigrationInfo(final String publicAddress, final @NonNull Callback<MigrationInfo, ApiException> callback) {
		try {
			migrationApi.getMigrationInfoAsync(publicAddress, new ApiCallback<MigrationInfo>() {
				@Override
				public void onFailure(final ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
					executorsUtil.mainThread().execute(new Runnable() {
						@Override
						public void run() {
							callback.onFailure(e);
						}
					});
				}

				@Override
				public void onSuccess(final MigrationInfo result, int statusCode, Map<String, List<String>> responseHeaders) {
					executorsUtil.mainThread().execute(new Runnable() {
						@Override
						public void run() {
							callback.onResponse(result);
						}
					});
				}
			});
		} catch (final ApiException e) {
			executorsUtil.mainThread().execute(new Runnable() {
				@Override
				public void run() {
					callback.onFailure(e);
				}
			});
		}
	}
}
