package com.kin.ecosystem.core.data.internal;


import static com.kin.ecosystem.core.data.internal.ConfigurationImpl.API_VERSION;

import com.kin.ecosystem.common.KinEnvironment;

class Environment implements KinEnvironment {

	private static final Environment PRODUCTION = new Environment(
		"https://horizon-kin-ecosystem.kininfrastructure.com/",
		"Public Global Kin Ecosystem Network ; June 2018",
		"GDF42M3IPERQCBLWFEZKQRK77JQ65SCKTU3CW36HZVCX7XX5A5QXZIVK",
		"TODO",
		"TODO",
		"TODO",
		"https://api.kinmarketplace.com/" + API_VERSION,
		"https://cdn.kinmarketplace.com/",
		"https://kin-bi.appspot.com/eco_");

	private static final Environment BETA = new Environment(
		"https://horizon-playground.kininfrastructure.com",
		"Kin Playground Network ; June 2018",
		"GBC3SG6NGTSZ2OMH3FFGB7UVRQWILW367U4GSOOF4TFSZONV42UJXUH7",
		"TODO",
		"TODO",
		"TODO",
		"http://api.kinecosystembeta.com/" + API_VERSION,
		"https://s3.amazonaws.com/assets.kinecosystembeta.com/web-offers/cards-based/index.html",
		"https://kin-bi.appspot.com/eco_play_");

	private static final Environment TEST = new Environment(
		"https://horizon-playground.kininfrastructure.com",
		"Kin Playground Network ; June 2018",
		"GBC3SG6NGTSZ2OMH3FFGB7UVRQWILW367U4GSOOF4TFSZONV42UJXUH7",
		"TODO",
		"TODO",
		"TODO",
		"http://api.kinecosystemtest.com/" + API_VERSION,
		"https://s3.amazonaws.com/assets.kinecosystemtest.com/web-offers/cards-based/index.html",
		"https://kin-bi.appspot.com/eco_play_");

	private final String oldBlockchainNetworkUrl;
	private final String oldBlockchainPassphrase;
	private final String oldIssuer;
	private final String newBlockchainNetworkUrl;
	private final String newBlockchainPassphrase;
	private final String migrationServiceUrl;
	private final String ecosystemServerUrl;
	private final String ecosystemWebFront;
	private final String biUrl;

	public Environment(
		String oldBlockchainNetworkUrl, String oldBlockchainPassphrase, String oldIssuer,
		String newBlockchainNetworkUrl, String newBlockchainPassphrase, String migrationServiceUrl,
		String ecosystemServerUrl, String ecosystemWebFront, String biUrl) {
		this.oldBlockchainNetworkUrl = oldBlockchainNetworkUrl;
		this.oldBlockchainPassphrase = oldBlockchainPassphrase;
		this.oldIssuer = oldIssuer;
		this.newBlockchainNetworkUrl = newBlockchainNetworkUrl;
		this.newBlockchainPassphrase = newBlockchainPassphrase;
		this.migrationServiceUrl = migrationServiceUrl;
		this.ecosystemServerUrl = ecosystemServerUrl;
		this.ecosystemWebFront = ecosystemWebFront;
		this.biUrl = biUrl;
	}

	@Override
	public String getOldBlockchainNetworkUrl() {
		return oldBlockchainNetworkUrl;
	}

	@Override
	public String getOldBlockchainPassphrase() {
		return oldBlockchainPassphrase;
	}

	@Override
	public String getOldBlockchainIssuer() {
		return oldIssuer;
	}

	@Override
	public String getNewBlockchainNetworkUrl() {
		return newBlockchainNetworkUrl;
	}

	@Override
	public String getNewBlockchainPassphrase() {
		return newBlockchainPassphrase;
	}

	@Override
	public String getMigrationServiceUrl() {
		return migrationServiceUrl;
	}

	@Override
	public String getEcosystemServerUrl() {
		return ecosystemServerUrl;
	}

	@Override
	public String getEcosystemWebFront() {
		return ecosystemWebFront;
	}

	@Override
	public String getBiUrl() {
		return biUrl;
	}

	static KinEnvironment getProduction() {
		return PRODUCTION;
	}

	static KinEnvironment getBeta() {
		return BETA;
	}

	static KinEnvironment getTest() {
		return TEST;
	}
}
