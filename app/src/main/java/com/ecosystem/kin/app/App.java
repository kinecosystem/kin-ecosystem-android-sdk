package com.ecosystem.kin.app;

import android.app.Application;

import com.ecosystem.kin.app.model.UserRepo;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.exception.InitializeException;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            String userID = UserRepo.getUser(this).getUserID().toString();
            Kin.start(getApplicationContext(), "apiKey", userID);
        } catch (InitializeException e) {
            e.printStackTrace();
        }

    }
}
