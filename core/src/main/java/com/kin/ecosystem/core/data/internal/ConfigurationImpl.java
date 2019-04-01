package com.kin.ecosystem.core.data.internal;

import static com.kin.ecosystem.core.network.ApiClient.DELETE;
import static com.kin.ecosystem.core.network.ApiClient.GET;
import static com.kin.ecosystem.core.network.ApiClient.POST;

import android.os.Build;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.kin.ecosystem.common.KinEnvironment;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.common.exception.ServiceException;
import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;
import com.kin.ecosystem.core.data.auth.AuthRepository;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.network.ApiClient;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.AuthToken;
import com.kin.ecosystem.core.util.ErrorUtil;
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
	private static final String HEADER_BLOCKCHAIN_VERSION = "X-KIN-BLOCKCHAIN-VERSION";

	private static final String BEARER = "Bearer ";
	private static final String AUTHORIZATION = "Authorization";

	static final String API_VERSION = "v2";

	private static final int NO_TOKEN_ERROR_CODE = 666;
	private static final String AUTH_TOKEN_COULD_NOT_BE_GENERATED = "AuthToken could not be generated";

	private static final String USERS_PATH = "/" + API_VERSION + "/users";
	private static final String LOGOUT_PATH = "/" + API_VERSION + "/users/me/session";
	private static final String KIN_VERSION_END_PATH = "/blockchain_version";
	private static final String KIN_MIGRATION_INFO_PATH = "/" + API_VERSION + "/migration/info";
	private static final String PREFIX_ANDROID = "android ";

	private static final Object apiClientLock = new Object();
	private static ApiClient defaultApiClient;
	private static BlockchainSource blockchainSource;

	private final KinEnvironment kinEnvironment;
	private static volatile ConfigurationImpl instance;

	private ConfigurationImpl(@NonNull String environmentName) {
		this.kinEnvironment = getEnvironmentByName(environmentName);
	}

	public static void init(@NonNull String environmentName) {
		if (instance == null) {
			synchronized (ConfigurationImpl.class) {
				if (instance == null) {

					instance = new ConfigurationImpl(environmentName);
				}
			}
		}
	}

	public static void setBlockchainSource(BlockchainSource bcSource) {
		blockchainSource = bcSource;
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
						Request originalRequest = blockchainSource == null ?
							chain.request() :
							chain.request() // new request with the BV version header
								.newBuilder()
								.addHeader(HEADER_BLOCKCHAIN_VERSION, blockchainSource.getBlockchainVersion().getVersion())
								.build();

						if (shouldntBeAuthenticated(originalRequest)) {
							return chain.proceed(originalRequest);
						} else {
							AuthToken authToken = AuthRepository.getInstance().getAuthTokenSync();
							if (authToken != null) {
								Request authorisedRequest = originalRequest.newBuilder()
									.header(AUTHORIZATION, BEARER + authToken.getToken())
									.build();

								final Response response = chain.proceed(authorisedRequest);
								final ResponseBody body = response.body();
								final String bodyString = body.string();
								final MediaType contentType = body.contentType();

								try {
									final Object result = defaultApiClient.handleResponse(response
										.newBuilder()
										.body(ResponseBody.create(contentType, bodyString))
										.build(), Object.class);
								} catch (ApiException e) {
									KinEcosystemException serviceException = ErrorUtil.fromApiException(e);
									if (serviceException.getCode() == ServiceException.BLOCKCHAIN_ENDPOINT_CHANGED) {
										blockchainSource.startMigrationProcess();
									}
								}

								return response.newBuilder().body(ResponseBody.create(contentType, bodyString)).build();
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

	private boolean shouldntBeAuthenticated(Request originalRequest) {
		final String path = originalRequest.url().encodedPath();
		final String method = originalRequest.method();
		return path.equals(USERS_PATH) && method.equals(POST) ||
			path.equals(LOGOUT_PATH) && method.equals(DELETE) ||
			path.contains(KIN_VERSION_END_PATH) && method.equals(GET) ||
			path.contains(KIN_MIGRATION_INFO_PATH) && method.equals(GET);
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
		return kinEnvironment;
	}

	private KinEnvironment getEnvironmentByName(@EnvironmentName String environment) {
		switch (environment) {
			case EnvironmentName.PRODUCTION:
				return Environment.getProduction();
			case EnvironmentName.BETA:
				return Environment.getBeta();
			case EnvironmentName.TEST:
				return Environment.getTest();
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
