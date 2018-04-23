package com.ecosystem.kin.app;

import android.app.Application;
import android.support.annotation.NonNull;
import com.crashlytics.android.Crashlytics;
import com.ecosystem.kin.app.model.SignInRepo;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.exception.InitializeException;
import com.kin.ecosystem.network.model.SignInData;
import io.fabric.sdk.android.Fabric;


public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        SignInData signInData;

        if(BuildConfig.IS_JWT_REGISTRATION) {
            /*
            * SignInData should be created with registration JWT {see https://jwt.io/} created securely by server side
            * In the the this example 'SignInRepo.getJWTSignInData' receive an empty/null JWT String and the sample app generate the JWT locally.
            * DO NOT!!!! use this approach in your real app.
            * */
            signInData = SignInRepo.getJWTSignInData(this, null);
        } else {
            //Use whitelist signing data for small scale testing
            signInData = SignInRepo.getWhitelistSignInData(this, getAppId(), getApiKey());
        }

        try {
            Kin.start(getApplicationContext(), signInData);
        } catch (InitializeException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private static String getAppId() {
        return BuildConfig.SAMPLE_APP_ID;
    }

    @NonNull
    private static String getApiKey() {
        return BuildConfig.SAMPLE_API_KEY;
    }

}
