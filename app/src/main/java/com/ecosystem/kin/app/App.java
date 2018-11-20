package com.ecosystem.kin.app;

import android.app.Application;
import android.support.annotation.NonNull;


public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

//        Fabric.with(this, new Crashlytics());
//
//		KinEnvironment environment = Environment.getBeta();
//
//        if (BuildConfig.IS_JWT_REGISTRATION) {
//            /**
//            * SignInData should be created with registration JWT {see https://jwt.io/} created securely by server side
//            * In the the this example {@link SignInRepo#getJWT} generate the JWT locally.
//            * DO NOT!!!! use this approach in your real app.
//            * */
//            String jwt = SignInRepo.getJWT(this);
//
//            try {
//                Kin.start(getApplicationContext(), jwt, environment);
//            } catch (ClientException | BlockchainException e) {
//                e.printStackTrace();
//            }
//        } else {
//            /** Use {@link WhitelistData} for small scale testing */
//            WhitelistData whitelistData = SignInRepo.getWhitelistSignInData(this, getAppId(), getApiKey());
//            try {
//                Kin.start(getApplicationContext(), whitelistData, environment);
//            } catch (ClientException | BlockchainException e) {
//                e.printStackTrace();
//            }
//        }
//
//        Kin.enableLogs(true);
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
