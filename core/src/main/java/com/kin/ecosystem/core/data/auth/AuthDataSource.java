package com.kin.ecosystem.core.data.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.model.UserStats;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.AccountInfo;
import com.kin.ecosystem.core.network.model.AuthToken;
import com.kin.ecosystem.core.network.model.JWT;
import com.kin.ecosystem.core.network.model.UserProfile;
import com.kin.ecosystem.core.network.model.UserProperties;
import org.json.JSONException;

public interface AuthDataSource {

	void setJWT(@NonNull final String jwt) throws JSONException;

	void updateWalletAddress(String address, @NonNull final KinCallback<Boolean> callback);

	String getAppID();

	String getDeviceID();

	String getUserID();

	String getEcosystemUserID();

	void getAuthToken(@Nullable final KinCallback<AuthToken> callback);

	AuthToken getAuthTokenSync();

	void hasAccount(@NonNull String userId, @NonNull final KinCallback<Boolean> callback);

	void userStats(@NonNull final KinCallback<UserStats> callback);

	interface Local {

		void setJWT(@NonNull final String signInData) throws JSONException;

		String getJWT();

		void setAccountInfo(@NonNull final AccountInfo accountInfo);

		String getAppId();

		String getDeviceID();

		String getUserID();

		String getEcosystemUserID();

		AuthToken getAuthTokenSync();

	}

	interface Remote {

		void getAccountInfo(@NonNull JWT jwt, @NonNull final Callback<AccountInfo, ApiException> callback);

		AccountInfo getAccountInfoSync(@NonNull JWT jwt);

		void hasAccount(@NonNull String userId, @NonNull final Callback<Boolean, ApiException> callback);

		void userProfile(@NonNull final Callback<UserProfile, ApiException> callback) ;

		void updateWalletAddress(@NonNull UserProperties userProperties, @NonNull final Callback<Void, ApiException> callback);
	}
}
