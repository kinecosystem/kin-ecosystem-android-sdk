package com.ecosystem.kin.app;

import android.support.multidex.MultiDexApplication;
import com.crashlytics.android.Crashlytics;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.common.exception.ClientException;
import com.squareup.leakcanary.LeakCanary;
import io.fabric.sdk.android.Fabric;


public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        initSDK();
    }

    private void initSDK() {
		if (LeakCanary.isInAnalyzerProcess(this)) {
			// This process is dedicated to LeakCanary for heap analysis.
			// You should not init your app in this process.
			return;
		}
		LeakCanary.install(this);
        try {
            Kin.initialize(getApplicationContext());
        } catch (ClientException e) {
            e.printStackTrace();
        }
        Kin.enableLogs(true);
    }

}
