package com.kin.ecosystem.core.data.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.ObservableData;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.util.DateUtil;
import com.kin.ecosystem.core.util.ErrorUtil;
import java.util.Calendar;
import java.util.Date;
import com.kin.ecosystem.core.network.model.AuthToken;
import com.kin.ecosystem.core.network.model.SignInData;

public class AuthRepository implements AuthDataSource {


	private static AuthRepository instance = null;

	private final AuthDataSource.Local localData;
	private final AuthDataSource.Remote remoteData;

	private SignInData cachedSignInData;
	private AuthToken cachedAuthToken;
	private ObservableData<String> appId = ObservableData.create(null);

	private AuthRepository(@NonNull AuthDataSource.Local local,
		@NonNull AuthDataSource.Remote remote) {
		this.localData = local;
		this.remoteData = remote;
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
	public void setSignInData(@NonNull SignInData signInData) {
		cachedSignInData = signInData;
		localData.setSignInData(signInData);
		remoteData.setSignInData(signInData);
		postAppID(signInData.getAppId());
	}

	@Override
	public ObservableData<String> getAppID() {
		loadCachedAppIDIfNeeded();
		return appId;
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
		return localData.getEcosystemUserID();
	}

	private void loadCachedAppIDIfNeeded() {
		if (TextUtils.isEmpty(appId.getValue())) {
			final String localAppId = localData.getAppId();
			if (!TextUtils.isEmpty(localAppId)){
				postAppID(localAppId);
			}
		}
	}

	@Override
	public AuthToken getAuthTokenSync() {
		if (cachedAuthToken != null) {
			return cachedAuthToken;
		} else {
			if (cachedSignInData != null) {
				AuthToken authToken = localData.getAuthTokenSync();
				if (authToken != null && !isAuthTokenExpired(authToken)) {
					setAuthToken(authToken);
				} else {
					refreshTokenSync();
				}
				return cachedAuthToken;
			} else {
				return null;
			}
		}
	}

	@Override
	public boolean isActivated() {
		return localData.isActivated();
	}

	@Override
	public void activateAccount(@NonNull final KinCallback<Void> callback) {
		remoteData.activateAccount(new Callback<AuthToken, ApiException>() {
			@Override
			public void onResponse(AuthToken response) {
				localData.activateAccount();
				setAuthToken(response);
				callback.onResponse(null);
			}

			@Override
			public void onFailure(ApiException e) {
				callback.onFailure(ErrorUtil.fromApiException(e));
			}
		});
	}

	private boolean isAuthTokenExpired(AuthToken authToken) {
		if (authToken == null) {
			return true;
		} else {
			Date expirationDate = DateUtil.getDateFromUTCString(authToken.getExpirationDate());
			if (expirationDate != null) {
				return Calendar.getInstance().getTimeInMillis() > expirationDate.getTime();
			} else {
				return true;
			}
		}
	}

	private void refreshTokenSync() {
		AuthToken authToken = remoteData.getAuthTokenSync();
		if (authToken != null) {
			setAuthToken(authToken);
		}
	}

	@Override
	public void setAuthToken(@NonNull AuthToken authToken) {
		cachedAuthToken = authToken;
		localData.setAuthToken(authToken);
		postAppID(authToken.getAppID());
	}

	@Override
	public void getAuthToken(@Nullable final KinCallback<AuthToken> callback) {
		remoteData.getAuthToken(new Callback<AuthToken, ApiException>() {
			@Override
			public void onResponse(AuthToken authToken) {
				setAuthToken(authToken);
				if (callback != null) {
					callback.onResponse(cachedAuthToken);
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

	private void postAppID(@Nullable String appID) {
		appId.postValue(appID);
	}
}
