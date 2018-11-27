package com.ecosystem.kin.app;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.ecosystem.kin.app.model.SignInRepo;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.common.model.WhitelistData;
import io.fabric.sdk.android.Fabric;


public class App extends Application {

    private static final String TAG = "SampleApp";

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());
        initSDK();
    }

    private void initSDK() {
        if (BuildConfig.IS_JWT_REGISTRATION) {
            /**
             * SignInData should be created with registration JWT {see https://jwt.io/} created securely by server side
             * In the the this example {@link SignInRepo#getJWT} generate the JWT locally.
             * DO NOT!!!! use this approach in your real app.
             * */
            String jwt = SignInRepo.getJWT(this);

            Kin.login(jwt, new KinCallback<Void>() {
                @Override
                public void onResponse(Void response) {
                    Log.d(TAG, "JWT onResponse: login");
                }

                @Override
                public void onFailure(KinEcosystemException exception) {
                    Log.e(TAG, "JWT onFailure: " + exception.getMessage());
                }
            });
        } else {
            /** Use {@link WhitelistData} for small scale testing */
            WhitelistData whitelistData = SignInRepo.getWhitelistSignInData(this, getAppId(), getApiKey());
            Kin.login(whitelistData, new KinCallback<Void>() {
                @Override
                public void onResponse(Void response) {
                    Log.d(TAG, "WhiteList onResponse: login");
                }

                @Override
                public void onFailure(KinEcosystemException exception) {
                    Log.e(TAG, "WhiteList onFailure: " + exception.getMessage());
                }
            });
        }

        Kin.enableLogs(true);
    }

    @NonNull
    public static String getAppId() {
        return BuildConfig.SAMPLE_APP_ID;
    }

    @NonNull
    public static String getApiKey() {
        return BuildConfig.SAMPLE_API_KEY;
    }

}
