package com.kin.ecosystem.data.auth;

import android.support.annotation.NonNull;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.network.model.AuthToken;
import com.kin.ecosystem.network.model.SignInData;

public interface AuthDataSource {

    void setSignInData(@NonNull final SignInData signInData);

    ObservableData<String> getAppID();

    void setAuthToken(@NonNull final AuthToken authToken);

    void getAuthToken(@NonNull final Callback<AuthToken> callback);

    AuthToken getAuthTokenSync();

    boolean isActivated();

    void activateAccount(@NonNull final Callback<Void> callback);

    interface Local {

        void setSignInData(@NonNull final SignInData signInData);

        void setAuthToken(@NonNull final AuthToken authToken);

        void getAuthToken(@NonNull final Callback<AuthToken> callback);

        AuthToken getAuthTokenSync();

        boolean isActivated();

        void activateAccount();

    }

    interface Remote {

        void setSignInData(@NonNull final SignInData signInData);

        void getAuthToken(@NonNull final Callback<AuthToken> callback);

        AuthToken getAuthTokenSync();

        void activateAccount(@NonNull final Callback<AuthToken> callback);
    }
}
