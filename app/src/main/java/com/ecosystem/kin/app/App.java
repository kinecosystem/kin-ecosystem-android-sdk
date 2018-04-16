package com.ecosystem.kin.app;

import android.app.Application;
import com.ecosystem.kin.app.model.SignInRepo;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.exception.InitializeException;
import com.kin.ecosystem.network.model.SignInData;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
/*            //Use whitelist signing data for small scale testing
            SignInData signInData = SignInRepo.getWhitelistSignInData(this);*/

            /*
            * SignInData should be created with registration JWT {see https://jwt.io/} created securely by server side
            * In the the this example 'SignInRepo.getJWTSignInData' receive an empty JWT String and the sample app generate the JWT locally.
            * DO NOT!!!! use this approach in your real app.
            * */

            SignInData signInData = SignInRepo.getJWTSignInData(this, "");
            Kin.start(getApplicationContext(), signInData);
        } catch (InitializeException e) {
            e.printStackTrace();
        }
    }
}
