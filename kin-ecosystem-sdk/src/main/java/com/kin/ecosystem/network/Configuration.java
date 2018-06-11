package com.kin.ecosystem.network;

import com.kin.ecosystem.data.auth.AuthRepository;
import com.kin.ecosystem.data.offer.OfferRepository;
import com.kin.ecosystem.network.model.AuthToken;
import java.io.IOException;
import kin.ecosystem.core.network.ApiClient;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class Configuration {

    private static Configuration instance = null;

    private final ApiClient apiClient;

    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";

    private static final String USERS_PATH = "/v1/users";

    private Configuration(ApiClient apiClient) {
        this.apiClient = apiClient;
        this.apiClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                final String path = originalRequest.url().encodedPath();
                AuthToken authToken = null;
                if (!path.equals(USERS_PATH)) {
                    authToken = AuthRepository.getInstance().getAuthTokenSync();
                }

                if (authToken != null) {
                    Request authorisedRequest = originalRequest.newBuilder()
                        .header(AUTHORIZATION, BEARER + authToken.getToken())
                        .build();
                    return chain.proceed(authorisedRequest);
                } else {
                    return chain.proceed(originalRequest);
                }
            }
        });
    }

    public static ApiClient getApiClient() {
        if (instance == null) {
            synchronized (Configuration.class) {
                if (instance == null) {
                    instance = new Configuration(new ApiClient());
                }
            }
        }
        return instance.apiClient;
    }
}
