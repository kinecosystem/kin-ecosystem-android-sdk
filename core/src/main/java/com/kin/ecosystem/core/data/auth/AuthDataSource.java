package com.kin.ecosystem.core.data.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.ObservableData;
import com.kin.ecosystem.common.model.UserStats;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.AccountInfo;
import com.kin.ecosystem.core.network.model.AuthToken;
import com.kin.ecosystem.core.network.model.SignInData;
import com.kin.ecosystem.core.network.model.UserProfile;
import com.kin.ecosystem.core.network.model.UserProperties;

public interface AuthDataSource {

	void setSignInData(@NonNull final SignInData signInData);

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

		void setSignInData(@NonNull final SignInData signInData);

		SignInData getSignInData();

		void setAccountInfo(@NonNull final AccountInfo accountInfo);

		String getAppId();

		String getDeviceID();

		String getUserID();

		String getEcosystemUserID();

		AuthToken getAuthTokenSync();

	}

	interface Remote {

		void setSignInData(@NonNull final SignInData signInData);

		void getAccountInfo(@NonNull final Callback<AccountInfo, ApiException> callback);

		AccountInfo getAccountInfoSync();

		void hasAccount(@NonNull String userId, @NonNull final Callback<Boolean, ApiException> callback);

		void userProfile(@NonNull final Callback<UserProfile, ApiException> callback) ;

		void updateWalletAddress(@NonNull UserProperties userProperties, @NonNull final Callback<Void, ApiException> callback);
	}
}
