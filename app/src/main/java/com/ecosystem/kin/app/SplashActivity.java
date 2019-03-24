package com.ecosystem.kin.app;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.ecosystem.kin.app.login.LoginActivity;
import com.ecosystem.kin.app.main.MainActivity;
import com.ecosystem.kin.app.model.SignInRepo;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.KinTheme;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.common.exception.KinEcosystemException;

public class SplashActivity extends AppCompatActivity {

	private static final String TAG = SplashActivity.class.getSimpleName();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		boolean isUserCreated = SignInRepo.getUserId(this) != null;
		if(isUserCreated) {
			login();
		} else {
			navigateToLoginActivity();
		}
	}

	private void login() {
		try {
			Kin.initialize(getApplicationContext(), SignInRepo.getKinTheme(this));
		} catch (ClientException e) {
			e.printStackTrace();
		}
		Kin.enableLogs(true);
		/**
		 * SignInData should be created with registration JWT {see https://jwt.io/} created securely by server side
		 * In the the this example {@link SignInRepo#getJWT} generate the JWT locally.
		 * DO NOT!!!! use this approach in your real app.
		 * */
		String jwt = SignInRepo.getJWT(this);
		Kin.login(jwt, new KinCallback<Void>() {
			@Override
			public void onResponse(Void response) {
				Log.d(TAG, "JWT onResponse: login");
				navigateToMainActivity();
			}

			@Override
			public void onFailure(KinEcosystemException exception) {
				Log.e(TAG, "JWT onFailure: " + exception.getMessage());
				showAlertDialog(exception.getMessage());
			}
		});
	}

	private void showAlertDialog(String message) {
		new AlertDialog.Builder(this)
			.setTitle("Login Error")
			.setMessage(message)
			.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).show();
	}

	private void navigateToLoginActivity() {
		Intent mainIntent = new Intent(this, LoginActivity.class);
		startActivity(mainIntent);
		finish();
	}

	private void navigateToMainActivity() {
		Intent mainIntent = new Intent(this, MainActivity.class);
		startActivity(mainIntent);
		finish();
	}
}
