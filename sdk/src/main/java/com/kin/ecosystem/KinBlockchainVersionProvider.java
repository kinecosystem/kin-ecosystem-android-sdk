package com.kin.ecosystem;

import kin.sdk.migration.common.KinSdkVersion;
import kin.sdk.migration.common.exception.FailedToResolveSdkVersionException;
import kin.sdk.migration.common.interfaces.IKinVersionProvider;

class KinBlockchainVersionProvider implements IKinVersionProvider {

	public KinBlockchainVersionProvider(String appId) {
	}

	@Override
	public KinSdkVersion getKinSdkVersion() throws FailedToResolveSdkVersionException {
		return KinSdkVersion.OLD_KIN_SDK;
	}
}
