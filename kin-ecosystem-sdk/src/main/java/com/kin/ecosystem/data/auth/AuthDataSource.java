package com.kin.ecosystem.data.auth;

import android.support.annotation.NonNull;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.network.model.AuthToken;
import com.kin.ecosystem.network.model.SignInData;

public interface AuthDataSource {

    void setSignInData(@NonNull final SignInData signInData);

    void getSignInData(@NonNull final Callback<SignInData> callback);

    void setAuthToken(@NonNull final AuthToken authToken);

    void getAuthToken(@NonNull final Callback<AuthToken> callback);

    AuthToken getAuthTokenSync();

}
