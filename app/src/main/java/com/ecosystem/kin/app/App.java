package com.ecosystem.kin.app;

import android.app.Application;
import android.support.annotation.NonNull;
import com.crashlytics.android.Crashlytics;
import com.ecosystem.kin.app.model.SignInRepo;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.KinEnvironment;
import com.kin.ecosystem.data.model.WhitelistData;
import com.kin.ecosystem.exception.InitializeException;
import io.fabric.sdk.android.Fabric;


public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        Kin.setEnvironment(KinEnvironment.PLAYGROUND);

        if (BuildConfig.IS_JWT_REGISTRATION) {
            /**
            * SignInData should be created with registration JWT {see https://jwt.io/} created securely by server side
            * In the the this example {@link SignInRepo#getJWT} generate the JWT locally.
            * DO NOT!!!! use this approach in your real app.
            * */
            String jwt = SignInRepo.getJWT(this);

            try {
                Kin.start(getApplicationContext(), jwt);
            } catch (InitializeException e) {
                e.printStackTrace();
            }
        } else {
            /** Use {@link WhitelistData} for small scale testing */
            WhitelistData whitelistData = SignInRepo.getWhitelistSignInData(this, getAppId(), getApiKey());
            try {
                Kin.start(getApplicationContext(), whitelistData);
            } catch (InitializeException e) {
                e.printStackTrace();
            }
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
