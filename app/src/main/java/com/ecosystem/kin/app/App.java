package com.ecosystem.kin.app;

import android.support.multidex.MultiDexApplication;
import com.crashlytics.android.Crashlytics;
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
    }

}
