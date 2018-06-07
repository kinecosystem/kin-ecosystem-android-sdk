package com.kin.ecosystem.data.blockchain;


import kin.core.ServiceProvider;

public enum Network {

    NETWORK_MAIN(ServiceProvider.NETWORK_ID_MAIN, "https://horizon.stellar.org"),
    NETWORK_TEST(ServiceProvider.NETWORK_ID_TEST, "https://horizon-testnet.stellar.org"),
    NETWORK_PRIVATE_TEST("private testnet", "https://horizon-kik.kininfrastructure.com",
        "GBQ3DQOA7NF52FVV7ES3CR3ZMHUEY4LTHDAQKDTO6S546JCLFPEQGCPK");

    final String networkId;
    final String networkUrl;
    final String issuer;

    Network(String id, String networkUrl) {
        this.networkId = id;
        this.networkUrl = networkUrl;
        this.issuer = null;
    }

    Network(String networkId, String networkUrl, String issuer) {
        this.networkId = networkId;
        this.networkUrl = networkUrl;
        this.issuer = issuer;
    }

    public ServiceProvider getProvider() {
        if (issuer != null) {
            return new ServiceProvider(networkUrl, networkId) {
                @Override
                protected String getIssuerAccountId() {
                    return issuer;
                }
            };
        }
        return new ServiceProvider(networkUrl, networkId);
    }

}
