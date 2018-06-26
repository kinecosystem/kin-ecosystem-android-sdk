package com.kin.ecosystem;

public class Environment implements KinEnvironment {

	private static final Environment OLD_PRODUCTION = new Environment(
		"https://horizon-kik.kininfrastructure.com/",
		"private testnet",
		"GBQ3DQOA7NF52FVV7ES3CR3ZMHUEY4LTHDAQKDTO6S546JCLFPEQGCPK",
		"http://ec2-34-203-202-205.compute-1.amazonaws.com/v1",
		"http://htmlpoll.kinecosystem.com.s3-website-us-east-1.amazonaws.com/",
		"https://kin-bi.appspot.com");

	private static final Environment PRODUCTION = new Environment(
		"https://horizon-kin-ecosystem.kininfrastructure.com/",
		"Public Global Kin Ecosystem Network ; June 2018",
		"GDF42M3IPERQCBLWFEZKQRK77JQ65SCKTU3CW36HZVCX7XX5A5QXZIVK",
		"https://api.kinmarketplace.com/v1",
		"http://htmlpoll.kinecosystem.com.s3-website-us-east-1.amazonaws.com/",
		"https://kin-bi.appspot.com");

	private static final Environment PLAYGROUND = new Environment(
		"https://stellar.kinplayground.com",
		"ecosystem playground",
		"GDVIWJ2NYBCPHMGTIBO5BBZCP5QCYC4YT4VINTV5PZOSE7BAJCH5JI64",
		"https://api.kinplayground.com/v1",
		"https://s3.amazonaws.com/assets.kinplayground.com/web-offers/cards-based/index.html",
		"https://kin-bi.appspot.com");

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

	public static KinEnvironment getProduction() {
		return OLD_PRODUCTION;
	}

	public static KinEnvironment getPlayground() {
		return PLAYGROUND;
	}
}
