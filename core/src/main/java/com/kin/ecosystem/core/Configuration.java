package com.kin.ecosystem.core;

import android.os.Build;
import android.os.Build.VERSION;
import com.kin.ecosystem.common.KinEnvironment;
import com.kin.ecosystem.core.data.auth.AuthRepository;
import com.kin.ecosystem.core.network.ApiClient;
import com.kin.ecosystem.core.network.model.AuthToken;
import java.io.IOException;
import java.util.Locale;
import kin.ecosystem.core.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Configuration {

	private static final String HEADER_SDK_VERSION = "X-SDK-VERSION";
	private static final String HEADER_DEVICE_MODEL = "X-DEVICE-MODEL";
	private static final String HEADER_DEVICE_MANUFACTURER = "X-DEVICE-MANUFACTURER";
	private static final String HEADER_DEVICE_LANGUAGE = "Accept-Language";
	private static final String HEADER_OS = "X-OS";

	private static final String BEARER = "Bearer ";
	private static final String AUTHORIZATION = "Authorization";

	private static final int NO_TOKEN_ERROR_CODE = 666;
	private static final String AUTH_TOKEN_COULD_NOT_BE_GENERATED = "AuthToken could not be generated";

	private static final String USERS_PATH = "/v1/users";
	public static final String PREFIX_ANDROID = "android ";

	private static KinEnvironment environment;

	private static final Object apiClientLock = new Object();
	private static ApiClient defaultApiClient;


	/**
	 * Get the default API client, which would be used when creating API
	 * instances without providing an API client.
	 *
	 * @return Default API client
	 */
	public static ApiClient getDefaultApiClient() {
		if (defaultApiClient == null) {
			synchronized (apiClientLock) {
				defaultApiClient = new ApiClient(environment.getEcosystemServerUrl());
				defaultApiClient.addInterceptor(new Interceptor() {
					@Override
					public Response intercept(Chain chain) throws IOException {
						Request originalRequest = chain.request();
						final String path = originalRequest.url().encodedPath();

						if (path.equals(USERS_PATH)) {
							return chain.proceed(originalRequest);
						} else {
							AuthToken authToken = AuthRepository.getInstance().getAuthTokenSync();
							if (authToken != null) {
								Request authorisedRequest = originalRequest.newBuilder()
									.header(AUTHORIZATION, BEARER + authToken.getToken())
									.build();
								return chain.proceed(authorisedRequest);
							} else { 
								// Stop the request from being executed.
								Logger.log(new Log().withTag("ApiClient").text("No token - response error on client"));
								return new Response.Builder()
									.code(NO_TOKEN_ERROR_CODE)
									.body(ResponseBody.create(MediaType.parse("application/json"),
										"{error: \"" + AUTH_TOKEN_COULD_NOT_BE_GENERATED + "\"}"))
									.message(AUTH_TOKEN_COULD_NOT_BE_GENERATED)
									.protocol(Protocol.HTTP_2)
									.request(originalRequest)
									.build();
							}
						}
					}
				});
			}
		}

		addHeaders(defaultApiClient);
		return defaultApiClient;
	}

	private static void addHeaders(ApiClient apiClient) {
		apiClient.addDefaultHeader(HEADER_OS, PREFIX_ANDROID + VERSION.RELEASE);
		apiClient.addDefaultHeader(HEADER_SDK_VERSION, BuildConfig.VERSION_NAME);
		apiClient.addDefaultHeader(HEADER_DEVICE_MODEL, Build.MODEL);
		apiClient.addDefaultHeader(HEADER_DEVICE_MANUFACTURER, Build.MANUFACTURER);
		apiClient.addDefaultHeader(HEADER_DEVICE_LANGUAGE, Locale.getDefault().toString());
	}

	public static KinEnvironment getEnvironment() {
		return environment;
	}

	public static void setEnvironment(KinEnvironment environment) {
		Configuration.environment = environment;
	}

	private Configuration() {
	}
}
