package com.kin.ecosystem.core.data.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import com.kin.ecosystem.common.KinEnvironment;

public class ConfigurationLocal implements Configuration.Local {

	private static volatile ConfigurationLocal instance;

	private static final String CONFIGURATION_PREF_NAME = "com.kin.ecosystem.sdk_configuration_pref";

	private static final String BLOCKCHAIN_NETWORK_URL_KEY = "blockchain_network_url";
	private static final String BLOCKCHAIN_PASSPHRASE_KEY = "blockchain_passphrase";
	private static final String ISSUER_KEY = "issuer";
	private static final String ECOSYSTEM_SERVER_URL_KEY = "ecosystem_server_url";
	private static final String ECOSYSTEM_WEB_FRONT_URL_KEY = "ecosystem_web_front_url";
	private static final String BI_URL_KEY = "bi_url";


	private final SharedPreferences configurationSharedPreferences;

	private ConfigurationLocal(Context context) {
		this.configurationSharedPreferences = context
			.getSharedPreferences(CONFIGURATION_PREF_NAME, Context.MODE_PRIVATE);
	}

	public static ConfigurationLocal getInstance(@NonNull Context context) {
		if (instance == null) {
			synchronized (ConfigurationLocal.class) {
				if (instance == null) {
					instance = new ConfigurationLocal(context);
				}
			}
		}

		return instance;
	}

	@Override
	public KinEnvironment getEnvironment() {
		final String blockchainNetworkUrl = configurationSharedPreferences.getString(BLOCKCHAIN_NETWORK_URL_KEY, null);
		final String blockchainPassphrase = configurationSharedPreferences.getString(BLOCKCHAIN_PASSPHRASE_KEY, null);
		final String issuer = configurationSharedPreferences.getString(ISSUER_KEY, null);
		final String ecosystemServerUrl = configurationSharedPreferences.getString(ECOSYSTEM_SERVER_URL_KEY, null);
		final String ecosystemWebFrontUrl = configurationSharedPreferences.getString(ECOSYSTEM_WEB_FRONT_URL_KEY, null);
		final String biUrl = configurationSharedPreferences.getString(BI_URL_KEY, null);
		if (blockchainNetworkUrl == null || blockchainPassphrase == null ||
			issuer == null || ecosystemServerUrl == null || ecosystemWebFrontUrl == null || biUrl == null) {
			return null;
		} else {
			return new Environment(blockchainNetworkUrl, blockchainPassphrase, issuer, ecosystemServerUrl,
				ecosystemWebFrontUrl, biUrl);
		}

	}

	@Override
	public void setEnvironment(@NonNull KinEnvironment kinEnvironment) {
		Editor editor = configurationSharedPreferences.edit();
		editor.putString(BLOCKCHAIN_NETWORK_URL_KEY, kinEnvironment.getBlockchainNetworkUrl());
		editor.putString(BLOCKCHAIN_PASSPHRASE_KEY, kinEnvironment.getBlockchainPassphrase());
		editor.putString(ISSUER_KEY, kinEnvironment.getIssuer());
		editor.putString(ECOSYSTEM_SERVER_URL_KEY, kinEnvironment.getEcosystemServerUrl());
		editor.putString(ECOSYSTEM_WEB_FRONT_URL_KEY, kinEnvironment.getEcosystemWebFront());
		editor.putString(BI_URL_KEY, kinEnvironment.getBiUrl());
		editor.apply();
	}
}
