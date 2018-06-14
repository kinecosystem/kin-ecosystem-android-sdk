package com.kin.ecosystem.data.auth;

import android.support.annotation.NonNull;
import com.kin.ecosystem.KinCallback;
import com.kin.ecosystem.data.Callback;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.network.ApiException;
import com.kin.ecosystem.network.model.AuthToken;
import com.kin.ecosystem.network.model.SignInData;

public interface AuthDataSource {

    void setSignInData(@NonNull final SignInData signInData);

    ObservableData<String> getAppID();

    String getDeviceID();

    void setAuthToken(@NonNull final AuthToken authToken);

    AuthToken getAuthTokenSync();

    boolean isActivated();

    void activateAccount(@NonNull final KinCallback<Void> callback);

    interface Local {

        void setSignInData(@NonNull final SignInData signInData);

        void setAuthToken(@NonNull final AuthToken authToken);

        void getAppId(@NonNull final Callback<String, Void> callback);

        String getDeviceID();

        AuthToken getAuthTokenSync();

        boolean isActivated();

        void activateAccount();

    }

    interface Remote {

        void setSignInData(@NonNull final SignInData signInData);

        AuthToken getAuthTokenSync();

        void activateAccount(@NonNull final Callback<AuthToken, ApiException> callback);
    }
}
