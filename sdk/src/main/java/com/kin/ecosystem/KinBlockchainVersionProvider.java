package com.kin.ecosystem;

import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.network.ApiException;
import kin.sdk.migration.common.KinSdkVersion;
import kin.sdk.migration.common.exception.FailedToResolveSdkVersionException;
import kin.sdk.migration.common.interfaces.IKinVersionProvider;

class KinBlockchainVersionProvider implements IKinVersionProvider {
	private final BlockchainSource.Local local;
	private final BlockchainSource.Remote remote;

	public KinBlockchainVersionProvider(BlockchainSource.Local local, BlockchainSource.Remote remote) {
		this.local = local;
		this.remote = remote;
	}

	@Override
	public KinSdkVersion getKinSdkVersion() throws FailedToResolveSdkVersionException {
		if (local.getBlockchainVersion() == KinSdkVersion.NEW_KIN_SDK) {
			return KinSdkVersion.NEW_KIN_SDK;
		}

		KinSdkVersion version;
		try {
			version = remote.getBlockchainVersion();
		} catch (ApiException e) {
			throw new FailedToResolveSdkVersionException();
		}

		local.setBlockchainVersion(version);

		return version;
	}
}
