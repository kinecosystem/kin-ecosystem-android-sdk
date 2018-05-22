package com.kin.ecosystem.data.blockchain;


import kin.core.ServiceProvider;

enum Network {

    NETWORK_MAIN(ServiceProvider.NETWORK_ID_MAIN, "https://horizon.stellar.org"),
    NETWORK_TEST(ServiceProvider.NETWORK_ID_TEST, "https://horizon-testnet.stellar.org"),
    NETWORK_PRIVATE_TEST("private testnet", "https://horizon-kik.kininfrastructure.com");

    final String networkId;
    final String networkUrl;

    Network(String id, String networkUrl) {
        this.networkId = id;
        this.networkUrl = networkUrl;
    }

    ServiceProvider getProvider() {
        return new ServiceProvider(networkUrl, networkId);
    }

    ServiceProvider getProviderForIssuer(final String issuer) {
        return new ServiceProvider(networkUrl, networkId) {
            @Override
            protected String getIssuerAccountId() {
                return issuer;
            }
        };
    }
}
