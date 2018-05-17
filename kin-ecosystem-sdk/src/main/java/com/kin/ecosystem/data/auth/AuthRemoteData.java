package com.kin.ecosystem.data.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.network.ApiCallback;
import com.kin.ecosystem.network.ApiClient;
import com.kin.ecosystem.network.ApiException;
import com.kin.ecosystem.network.api.AuthApi;
import com.kin.ecosystem.network.model.AuthToken;
import com.kin.ecosystem.network.model.SignInData;
import com.kin.ecosystem.util.ExecutorsUtil;
import java.util.List;
import java.util.Map;

public class AuthRemoteData implements AuthDataSource.Remote {

    /**
     * This is new api client to be different from oder apis without access token interceptor.
     */
    private static volatile AuthRemoteData instance;

    private final AuthApi authApi;
    private final ExecutorsUtil executorsUtil;

    private SignInData signInData;

    private AuthRemoteData(@NonNull ExecutorsUtil executorsUtil) {
        this.authApi = new AuthApi();
        this.executorsUtil = executorsUtil;
    }


    public static AuthRemoteData getInstance(@NonNull ExecutorsUtil executorsUtil) {
        if (instance == null) {
            synchronized (AuthRemoteData.class) {
                if (instance == null) {
                    instance = new AuthRemoteData(executorsUtil);
                }
            }
        }
        return instance;
    }

    @Override
    public void setSignInData(@NonNull SignInData signInData) {
        this.signInData = signInData;
    }

    @Override
    @Nullable
    public AuthToken getAuthTokenSync() {
        try {
            return authApi.signIn(signInData, "");
        } catch (ApiException e) {
            return null;
        }
    }

    @Override
    public void activateAccount(@NonNull final Callback<AuthToken> callback) {
        try {
            authApi.activateAcountAsync("", new ApiCallback<AuthToken>() {
                @Override
                public void onFailure(final ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    executorsUtil.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(e);
                        }
                    });
                }

                @Override
                public void onSuccess(final AuthToken result, int statusCode, Map<String, List<String>> responseHeaders) {
                    executorsUtil.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(result);
                        }
                    });
                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                }
            });
        } catch (final ApiException e) {
            executorsUtil.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    callback.onFailure(e);
                }
            });
        }
    }
}
