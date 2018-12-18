package com.kin.ecosystem.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class KinEcosystemBaseActivity extends AppCompatActivity {

	protected abstract @LayoutRes int getLayoutRes();

	protected abstract void initViews();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutRes());
		KinEcosystemInitiator.init(getApplicationContext());
		initViews();
	}
}
