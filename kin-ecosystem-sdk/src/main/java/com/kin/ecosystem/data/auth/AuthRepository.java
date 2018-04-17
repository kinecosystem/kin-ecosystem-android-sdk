package com.kin.ecosystem.data.auth;

import static com.kin.ecosystem.util.DateUtil.getDateFromUTCString;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.network.model.AuthToken;
import com.kin.ecosystem.network.model.SignInData;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;

public class AuthRepository implements AuthDataSource {

    private static AuthRepository instance = null;

    private final AuthDataSource.Local localData;
    private final AuthDataSource.Remote remoteData;

    private SignInData cachedSignInData;
    private AuthToken cachedAuthToken;
    private ObservableData<String> appId = ObservableData.create(null);

    private AuthRepository(@NonNull AuthDataSource.Local local, @NonNull AuthDataSource.Remote remote) {
        this.localData = local;
        this.remoteData = remote;
    }

    public static void init(@NonNull SignInData signInData, @NonNull AuthDataSource.Local localData,
        @NonNull AuthDataSource.Remote remoteData) {
        if (instance == null) {
            synchronized (AuthRepository.class) {
                if (instance == null) {
                    instance = new AuthRepository(localData, remoteData);
                    instance.setSignInData(signInData);
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
        return appId;
    }

    @Override
    public void getAuthToken(@NonNull final Callback<AuthToken> callback) {
        if (cachedAuthToken != null) {
            callback.onResponse(cachedAuthToken);
        } else {
            localData.getAuthToken(new Callback<AuthToken>() {
                @Override
                public void onResponse(final AuthToken response) {
                    setAuthToken(response);
                    callback.onResponse(cachedAuthToken);

                }

                @Override
                public void onFailure(Throwable t) {
                    getRemoteAuthToken(callback);
                }
            });
        }
    }

    private void getRemoteAuthToken(final Callback<AuthToken> callback) {
        remoteData.getAuthToken(new Callback<AuthToken>() {
            @Override
            public void onResponse(AuthToken response) {
                setAuthToken(response);
                callback.onResponse(cachedAuthToken);
            }

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }
        });
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
    public void activateAccount(@NonNull final Callback<Void> callback) {
        remoteData.activateAccount(new Callback<AuthToken>() {
            @Override
            public void onResponse(AuthToken response) {
                localData.activateAccount();
                setAuthToken(response);
                callback.onResponse(null);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    private boolean isAuthTokenExpired(AuthToken authToken) {
        if (authToken == null) {
            return true;
        } else {
            Date expirationDate = getDateFromUTCString(authToken.getExpirationDate());
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

    private void postAppID(@Nullable String appID) {
        appId.postValue(appID);
    }
}
