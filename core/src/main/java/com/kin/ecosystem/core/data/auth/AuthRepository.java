package com.kin.ecosystem.core.data.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.common.model.UserStats;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.AccountInfo;
import com.kin.ecosystem.core.network.model.AuthToken;
import com.kin.ecosystem.core.network.model.JWT;
import com.kin.ecosystem.core.network.model.UserProfile;
import com.kin.ecosystem.core.network.model.UserProperties;
import com.kin.ecosystem.core.util.DateUtil;
import com.kin.ecosystem.core.util.ErrorUtil;
import com.kin.ecosystem.core.util.JwtDecoder;
import com.kin.ecosystem.core.util.StringUtil;
import java.util.Calendar;
import java.util.Date;
import org.json.JSONException;

public class AuthRepository implements AuthDataSource {

	private static final long TWO_DAYS_IN_MILLIS = 2 * DateUtils.DAY_IN_MILLIS;
	private static volatile AuthRepository instance = null;

	private final AuthDataSource.Local localData;
	private final AuthDataSource.Remote remoteData;

	private String jwt;
	private AccountInfo cachedAccountInfo;
	private AuthToken cachedAuthToken;

	private AuthRepository(@NonNull AuthDataSource.Local local,
		@NonNull AuthDataSource.Remote remote) {
		this.localData = local;
		this.remoteData = remote;
		this.jwt = local.getJWT();
		this.cachedAuthToken = local.getAuthTokenSync();
		this.cachedAccountInfo = local.getAccountInfo();
	}

	public static void init(@NonNull AuthDataSource.Local localData,
		@NonNull AuthDataSource.Remote remoteData) {
		if (instance == null) {
			synchronized (AuthRepository.class) {
				if (instance == null) {
					instance = new AuthRepository(localData, remoteData);
				}
			}
		}
	}

	public static AuthRepository getInstance() {
		return instance;
	}

	@Override
	public @UserLoginState
	int getUserLoginState(@NonNull String jwt) throws ClientException {
		final JwtBody jwtBody = getJwtBody(jwt);
		final String currentUserID = localData.getUserID();
		final String currentDeviceID = localData.getDeviceID();
		if (StringUtil.isEmpty(currentUserID) ) {
			return UserLoginState.FIRST;
		} else {
			return currentUserID.equals(jwtBody.getUserId()) && currentDeviceID.equals(jwtBody.getDeviceId())
				? UserLoginState.SAME_USER : UserLoginState.DIFFERENT_USER;
		}
	}

	@Override
	public void setJWT(@NonNull String jwt) throws ClientException {
		this.jwt = jwt;
		final JwtBody jwtBody = getJwtBody(jwt);
		localData.setJWT(jwtBody);
	}

	@NonNull
	private JwtBody getJwtBody(@NonNull String jwt) throws ClientException {
		JwtBody jwtBody;
		try {
			jwtBody = JwtDecoder.getJwtBody(jwt);
			if (jwtBody == null) {
				throw new ClientException(ClientException.BAD_CONFIGURATION,
					"The jwt is not in the correct format, please see more details on our documentation.", null);
			}
		} catch (JSONException | IllegalArgumentException e) {
			throw ErrorUtil.getClientException(ClientException.BAD_CONFIGURATION, e);
		}
		return jwtBody;
	}

	@Override
	public void updateWalletAddress(final String address, @NonNull final KinCallback<Boolean> callback) {
		final UserProperties userProperties = new UserProperties().walletAddress(address);
		remoteData.updateWalletAddress(userProperties, new Callback<Void, ApiException>() {
			@Override
			public void onResponse(Void response) {
				callback.onResponse(true);
			}

			@Override
			public void onFailure(ApiException exception) {
				callback.onFailure(ErrorUtil.fromApiException(exception));
			}
		});
	}

	@Override
	public String getAppID() {
		return localData.getAppId();
	}

	@Override
	public String getDeviceID() {
		return localData.getDeviceID();
	}

	@Override
	public String getUserID() {
		return localData.getUserID();
	}

	@Override
	public String getEcosystemUserID() {
		return cachedAuthToken == null ? localData.getEcosystemUserID() : cachedAuthToken.getEcosystemUserID();
	}

	@Override
	@Nullable
	public AuthToken getAuthTokenSync() {
		if (cachedAuthToken != null) {
			return cachedAuthToken;
		} else {
			AuthToken authToken = localData.getAuthTokenSync();
			if (authToken != null && !isAuthTokenExpired(authToken)) {
				setAuthToken(authToken);
			} else {
				refreshTokenSync();
			}
			return cachedAuthToken;
		}
	}

    @Override
    public boolean isCurrentAuthTokenExpired() throws ClientException {
		if(localData.isLoggedIn()) {
			if (cachedAuthToken != null) {
				return isAuthTokenExpired(cachedAuthToken);
			}
			final AuthToken token = localData.getAuthTokenSync();
			if (token != null) {
				return isAuthTokenExpired(token);
			}
		}
        throw ErrorUtil.getClientException(ClientException.ACCOUNT_NOT_LOGGED_IN, null);
    }

	@Override
	public void setLoggedIn(boolean loggedIn) {
		localData.setLoggedIn(loggedIn);
	}

	@Override
	public void hasAccount(@NonNull String userId, @NonNull final KinCallback<Boolean> callback) {
		remoteData.hasAccount(userId, new Callback<Boolean, ApiException>() {
			@Override
			public void onResponse(Boolean response) {
				callback.onResponse(response);
			}

			@Override
			public void onFailure(ApiException exception) {
				callback.onFailure(ErrorUtil.fromApiException(exception));
			}
		});
	}

	@Override
	public void userStats(@NonNull final KinCallback<UserStats> callback) {
		remoteData.userProfile(new Callback<UserProfile, ApiException>() {
			@Override
			public void onResponse(UserProfile response) {
				UserStats userStats = new UserStats();
				com.kin.ecosystem.core.network.model.UserStats userNetworkStats = response.getStats();
				if (userNetworkStats != null) {
					userStats.setEarnCount(userNetworkStats.getEarnCount().intValue());
					userStats.setLastEarnDate(userNetworkStats.getLastEarnDate());
					userStats.setSpendCount(userNetworkStats.getSpendCount().intValue());
					userStats.setLastSpendDate(userNetworkStats.getLastSpendDate());
				}

				callback.onResponse(userStats);
			}

			@Override
			public void onFailure(ApiException exception) {
				callback.onFailure(ErrorUtil.fromApiException(exception));
			}
		});
	}

	@Override
	public void logout() {
		final String token = cachedAuthToken.getToken();
		remoteData.logout(token);
		localData.logout();

		cachedAuthToken = null;
		jwt = null;
	}

	private boolean isAuthTokenExpired(AuthToken authToken) {
		if (authToken == null) {
			return true;
		} else {
			Date expirationDate = DateUtil.getDateFromUTCString(authToken.getExpirationDate());
			if (expirationDate != null) {
				return Calendar.getInstance().getTimeInMillis() > (expirationDate.getTime() - TWO_DAYS_IN_MILLIS);
			} else {
				return true;
			}
		}
	}

	private void refreshTokenSync() {
		if (!StringUtil.isEmpty(jwt)) {
			AccountInfo accountInfo = remoteData.getAccountInfoSync(new JWT(jwt));
			if (accountInfo != null) {
				setAccountInfo(accountInfo);
			}
		}
	}

	private void setAuthToken(@NonNull AuthToken authToken) {
		cachedAuthToken = authToken;
	}

	private void setAccountInfo(AccountInfo accountInfo) {
		if (accountInfo != null) {
			cachedAccountInfo = accountInfo;
			localData.setAccountInfo(accountInfo);
			AuthToken authToken = accountInfo.getAuthToken();
			if (authToken != null) {
				setAuthToken(authToken);
			}
		}
	}

	@Override
	public void getAccountInfo(@Nullable final KinCallback<AccountInfo> callback) {
		if (cachedAccountInfo == null || isAuthTokenExpired(cachedAuthToken)) {
			if (StringUtil.isEmpty(jwt)) {
				if (callback != null) {
					callback.onFailure(ErrorUtil.getClientException(ClientException.ACCOUNT_NOT_LOGGED_IN, null));
				}
			} else {
				remoteData.getAccountInfo(new JWT(jwt), new Callback<AccountInfo, ApiException>() {
					@Override
					public void onResponse(AccountInfo accountInfo) {
						setAccountInfo(accountInfo);
						if (callback != null) {
							callback.onResponse(accountInfo);
						}
					}

					@Override
					public void onFailure(ApiException exception) {
						if (callback != null) {
							callback.onFailure(ErrorUtil.fromApiException(exception));
						}
					}
				});
			}
		} else {
			if (callback != null) {
				callback.onResponse(cachedAccountInfo);
			}
		}
	}
}
