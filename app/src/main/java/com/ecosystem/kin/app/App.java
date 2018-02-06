package com.ecosystem.kin.app;

import android.app.Application;

import com.kin.ecosystem.Kin;
import com.kin.ecosystem.exception.InitializeException;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Kin.start(getApplicationContext(), "apiKey", "234");
        } catch (InitializeException e) {
            e.printStackTrace();
        }

    }
}
