package com.kin.ecosystem.core.data.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.ObservableData;
import com.kin.ecosystem.common.model.UserStats;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.AuthToken;
import com.kin.ecosystem.core.network.model.SignInData;
import com.kin.ecosystem.core.network.model.UserProfile;
import com.kin.ecosystem.core.network.model.UserProperties;

public interface AuthDataSource {

	void setSignInData(@NonNull final SignInData signInData);

	void updateWalletAddress(String address, @NonNull final KinCallback<Boolean> callback);

	ObservableData<String> getAppID();

	String getDeviceID();

	String getUserID();

	String getEcosystemUserID();

	void setAuthToken(@NonNull final AuthToken authToken);

	void getAuthToken(@Nullable final KinCallback<AuthToken> callback);

	AuthToken getAuthTokenSync();

	void hasAccount(@NonNull String userId, @NonNull final KinCallback<Boolean> callback);

	void userStats(@NonNull final KinCallback<UserStats> callback);

	interface Local {

		void setSignInData(@NonNull final SignInData signInData);

		void setAuthToken(@NonNull final AuthToken authToken);

		String getAppId();

		String getDeviceID();

		String getUserID();

		String getEcosystemUserID();

		AuthToken getAuthTokenSync();
	}

	interface Remote {

		void setSignInData(@NonNull final SignInData signInData);

		void getAuthToken(@NonNull final Callback<AuthToken, ApiException> callback);

		AuthToken getAuthTokenSync();

		void hasAccount(@NonNull String userId, @NonNull final Callback<Boolean, ApiException> callback);

		void userProfile(@NonNull final Callback<UserProfile, ApiException> callback) ;


		void updateWalletAddress(@NonNull UserProperties userProperties, @NonNull final Callback<Void, ApiException> callback);
	}
}
