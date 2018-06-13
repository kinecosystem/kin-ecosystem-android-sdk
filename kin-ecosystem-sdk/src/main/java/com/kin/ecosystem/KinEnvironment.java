package com.kin.ecosystem;

public interface KinEnvironment {

	Environment PRODUCTION = new Environment("https://horizon-kik.kininfrastructure.com", "private testnet",
		"GBQ3DQOA7NF52FVV7ES3CR3ZMHUEY4LTHDAQKDTO6S546JCLFPEQGCPK", "http://api.kinmarketplace.com/v1",
		"http://htmlpoll.kinecosystem.com.s3-website-us-east-1.amazonaws.com/", "");

	Environment PLAYGROUND = new Environment("https://stellar.kinplayground.com", "ecosystem playground",
		"GDVIWJ2NYBCPHMGTIBO5BBZCP5QCYC4YT4VINTV5PZOSE7BAJCH5JI64", "http://api.kinplayground.com",
		"https://s3.amazonaws.com/assets.kinecosystembeta.com/index.html", "");

	String getBlockchainNetworkUrl();

	String getBlockchainPassphrase();

	String getIssuer();

	String getEcosystemServerUrl();

	String getEcosystemWebFront();
}


