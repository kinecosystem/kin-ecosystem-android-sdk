package com.kin.ecosystem;

import kin.sdk.core.ServiceProvider;

enum StellarNetwork {

    NETWORK_MAIN(ServiceProvider.NETWORK_ID_MAIN, "https://horizon.stellar.org"),
    NETWORK_TEST(ServiceProvider.NETWORK_ID_TEST, "https://horizon-testnet.stellar.org");

    final int networkId;
    final String networkUrl;

    StellarNetwork(int id, String networkUrl) {
        this.networkId = id;
        this.networkUrl = networkUrl;
    }

    ServiceProvider getProvider() {
        return new ServiceProvider(networkUrl, networkId);
    }
}
