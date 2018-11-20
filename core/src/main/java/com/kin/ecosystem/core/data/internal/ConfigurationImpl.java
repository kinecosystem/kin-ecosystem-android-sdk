package com.kin.ecosystem.core.data.internal;

import static com.kin.ecosystem.core.network.ApiClient.POST;

import android.os.Build;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.kin.ecosystem.common.KinEnvironment;
import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;
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

public class ConfigurationImpl implements Configuration {

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
	private static final String PREFIX_ANDROID = "android ";

	private static final Object apiClientLock = new Object();
	private static ApiClient defaultApiClient;

	private KinEnvironment kinEnvironment;
	private final Configuration.Local localData;
	private static volatile ConfigurationImpl instance;

	private ConfigurationImpl(@NonNull Configuration.Local local) {
		this.localData = local;
		this.kinEnvironment = local.getEnvironment();
	}

	public static void init(@NonNull Configuration.Local local) {
		if (instance == null) {
			synchronized (ConfigurationImpl.class) {
				if (instance == null) {
					instance = new ConfigurationImpl(local);
				}
			}
		}
	}

	public static ConfigurationImpl getInstance() {
		return instance;
	}

	/**
	 * Get the default API client, which would be used when creating API
	 * instances without providing an API client.
	 *
	 * @return Default API client
	 */
	public ApiClient getDefaultApiClient() {
		if (defaultApiClient == null) {
			synchronized (apiClientLock) {
				defaultApiClient = new ApiClient(kinEnvironment.getEcosystemServerUrl());
				defaultApiClient.addInterceptor(new Interceptor() {
					@Override
					public Response intercept(Chain chain) throws IOException {
						Request originalRequest = chain.request();
						final String path = originalRequest.url().encodedPath();
						if (path.equals(USERS_PATH) && originalRequest.method().equals(POST)) {
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

	private void addHeaders(ApiClient apiClient) {
		apiClient.addDefaultHeader(HEADER_OS, PREFIX_ANDROID + VERSION.RELEASE);
		apiClient.addDefaultHeader(HEADER_SDK_VERSION, BuildConfig.VERSION_NAME);
		apiClient.addDefaultHeader(HEADER_DEVICE_MODEL, Build.MODEL);
		apiClient.addDefaultHeader(HEADER_DEVICE_MANUFACTURER, Build.MANUFACTURER);
		apiClient.addDefaultHeader(HEADER_DEVICE_LANGUAGE, getDeviceAcceptedLanguage());
	}

	@Override
	public KinEnvironment getEnvironment() {
		if (kinEnvironment == null) {
			kinEnvironment = localData.getEnvironment();
		}
		return kinEnvironment;
	}

	@Override
	public void setEnvironment(String environment) {
		kinEnvironment = getEnvironmentByName(environment);
		localData.setEnvironment(kinEnvironment);
	}

	private KinEnvironment getEnvironmentByName(String environment) {
		switch (environment) {
			case "prod":
				return Environment.getProduction();
			case "beta":
				return Environment.getBeta();
			default:
				throw new IllegalArgumentException("Environment name: " + environment + "is not valid,"
					+ " please specify \"production\" or \"beta\"");
		}
	}

	private String getDeviceAcceptedLanguage() {
		Locale defaultLocale = Locale.getDefault();
		String acceptedLanguage;
		if (TextUtils.isEmpty(defaultLocale.getCountry())) {
			acceptedLanguage = defaultLocale.getLanguage();
		} else {
			acceptedLanguage = defaultLocale.getLanguage() + "-" + defaultLocale.getCountry();
		}
		return acceptedLanguage;
	}
}
