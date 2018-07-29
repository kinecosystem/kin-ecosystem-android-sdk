package kin.ecosystem.core.data.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import kin.ecosystem.common.KinCallback;
import kin.ecosystem.common.ObservableData;
import kin.ecosystem.common.Callback;
import kin.ecosystem.core.network.ApiException;
import kin.ecosystem.core.network.model.AuthToken;
import kin.ecosystem.core.network.model.SignInData;

public interface AuthDataSource {

	void setSignInData(@NonNull final SignInData signInData);

	ObservableData<String> getAppID();

	String getDeviceID();

	String getUserID();

	String getEcosystemUserID();

	void setAuthToken(@NonNull final AuthToken authToken);

	void getAuthToken(@Nullable final KinCallback<AuthToken> callback);

	AuthToken getAuthTokenSync();

	boolean isActivated();

	void activateAccount(@NonNull final KinCallback<Void> callback);

	interface Local {

		void setSignInData(@NonNull final SignInData signInData);

		void setAuthToken(@NonNull final AuthToken authToken);

		void getAppId(@NonNull final Callback<String, Void> callback);

		String getDeviceID();

		String getUserID();

		String getEcosystemUserID();

		AuthToken getAuthTokenSync();

		boolean isActivated();

		void activateAccount();

	}

	interface Remote {

		void setSignInData(@NonNull final SignInData signInData);

		void getAuthToken(@NonNull final Callback<AuthToken, ApiException> callback);

		AuthToken getAuthTokenSync();

		void activateAccount(@NonNull final Callback<AuthToken, ApiException> callback);
	}
}
