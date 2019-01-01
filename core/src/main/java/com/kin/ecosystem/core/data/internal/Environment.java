package com.kin.ecosystem.core.data.internal;


import static com.kin.ecosystem.core.data.internal.ConfigurationImpl.API_VERSION;

import com.kin.ecosystem.common.KinEnvironment;

class Environment implements KinEnvironment {

	private static final Environment PRODUCTION = new Environment(
		"https://horizon-kin-ecosystem.kininfrastructure.com/",
		"Public Global Kin Ecosystem Network ; June 2018",
		"GDF42M3IPERQCBLWFEZKQRK77JQ65SCKTU3CW36HZVCX7XX5A5QXZIVK",
		"https://api.kinmarketplace.com/" + API_VERSION,
		"https://cdn.kinmarketplace.com/",
		"https://kin-bi.appspot.com/eco_");

	private static final Environment BETA = new Environment(
		"https://horizon-playground.kininfrastructure.com",
		"Kin Playground Network ; June 2018",
		"GBC3SG6NGTSZ2OMH3FFGB7UVRQWILW367U4GSOOF4TFSZONV42UJXUH7",
		"http://api.kinecosystembeta.com/" + API_VERSION,
		"https://s3.amazonaws.com/assets.kinecosystembeta.com/web-offers/cards-based/index.html",
		"https://kin-bi.appspot.com/eco_play_");

	private static final Environment TEST = new Environment(
		"https://horizon-playground.kininfrastructure.com",
		"Kin Playground Network ; June 2018",
		"GBC3SG6NGTSZ2OMH3FFGB7UVRQWILW367U4GSOOF4TFSZONV42UJXUH7",
		"http://api.kinecosystemtest.com/" + API_VERSION,
		"https://s3.amazonaws.com/assets.kinecosystemtest.com/web-offers/cards-based/index.html",
		"https://kin-bi.appspot.com/eco_play_");

	private final String blockchainNetworkUrl;
	private final String blockchainPassphrase;
	private final String issuer;
	private final String ecosystemServerUrl;
	private final String ecosystemWebFront;
	private final String biUrl;

	public Environment(String blockchainNetworkUrl, String blockchainPassphrase,
		String issuer, String ecosystemServerUrl, String ecosystemWebFront, String biUrl) {
		this.blockchainNetworkUrl = blockchainNetworkUrl;
		this.blockchainPassphrase = blockchainPassphrase;
		this.issuer = issuer;
		this.ecosystemServerUrl = ecosystemServerUrl;
		this.ecosystemWebFront = ecosystemWebFront;
		this.biUrl = biUrl;
	}

	@Override
	public String getBlockchainNetworkUrl() {
		return blockchainNetworkUrl;
	}

	@Override
	public String getBlockchainPassphrase() {
		return blockchainPassphrase;
	}

	@Override
	public String getIssuer() {
		return issuer;
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
