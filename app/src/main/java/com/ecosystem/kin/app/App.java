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
            SignInData signInData = SignInRepo.getSignInData(this);
            Kin.start(getApplicationContext(), signInData);
        } catch (InitializeException e) {
            e.printStackTrace();
        }
    }
}
