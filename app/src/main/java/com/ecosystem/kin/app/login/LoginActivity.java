package com.ecosystem.kin.app.login;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import com.ecosystem.kin.app.BuildConfig;
import com.ecosystem.kin.app.R;
import com.ecosystem.kin.app.main.MainActivity;
import com.ecosystem.kin.app.model.SignInRepo;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.KinTheme;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.common.exception.KinEcosystemException;

public class LoginActivity extends AppCompatActivity implements OnClickListener {

	private static final String TAG = LoginActivity.class.getSimpleName();
	private ConstraintLayout constraintLayout;
	private TextInputEditText userIdEditText;
	private TextView doneBtn;
	private TextView generateBtn;
	private ProgressBar loader;
	private KinTheme kinTheme = KinTheme.LIGHT;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		constraintLayout = findViewById(R.id.container);
		userIdEditText = findViewById(R.id.user_id_edit_text);
		loader = findViewById(R.id.loader);
		doneBtn = findViewById(R.id.done_btn);
		generateBtn = findViewById(R.id.generate_btn);
		((TextView) findViewById(R.id.sample_app_version)).setText(getString(R.string.version_name, BuildConfig.VERSION_NAME));

		doneBtn.setOnClickListener(this);
		generateBtn.setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.done_btn:
				setUserIdAndLogIn();
				break;
			case R.id.generate_btn:
				generateAndSetUserId();
				login();
				break;
		}
	}

	private void setUserIdAndLogIn() {
		closeKeyboard();
		final String userId = userIdEditText.getText().toString();
		if(TextUtils.isEmpty(userId)) {
			showSnackbar("User id should not be empty", true);
		} else {
			SignInRepo.setUserId(this, userId);
			login();
		}
	}

	private void generateAndSetUserId() {
		closeKeyboard();
		final String randomUserId = SignInRepo.generateUserID();
		SignInRepo.setUserId(this, randomUserId);
		userIdEditText.setText(randomUserId);
	}

	private void login() {
		showLoading();
		try {
			Kin.initialize(getApplicationContext(), kinTheme);
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
				showSnackbar("login succeed jwt", false);
				Log.d(TAG, "JWT onResponse: login");
				stopLoading();
				navigateToMainActivity();
			}

			@Override
			public void onFailure(KinEcosystemException exception) {
				stopLoading();
				showSnackbar("login failed jwt", true);
				Log.e(TAG, "JWT onFailure: " + exception.getMessage());
			}
		});
	}

	private void showLoading() {
		setLoginEnabled(false);
		loader.setVisibility(VISIBLE);
	}

	private void stopLoading() {
		setLoginEnabled(true);
		loader.setVisibility(GONE);
	}

	private void setLoginEnabled(boolean isEnabled) {
		userIdEditText.setEnabled(isEnabled);
		doneBtn.setEnabled(isEnabled);
		generateBtn.setEnabled(isEnabled);

		userIdEditText.setClickable(isEnabled);
		doneBtn.setClickable(isEnabled);
		generateBtn.setClickable(isEnabled);

		if(isEnabled) {
			final int enabledColor = ContextCompat.getColor(this, R.color.colorPrimary);
			doneBtn.setTextColor(enabledColor);
			generateBtn.setTextColor(enabledColor);
		} else {
			final int disabledColor = ContextCompat.getColor(this, R.color.gray);
			doneBtn.setTextColor(disabledColor);
			generateBtn.setTextColor(disabledColor);
		}
	}

	private void navigateToMainActivity() {
		Intent mainIntent = new Intent(this, MainActivity.class);
		startActivity(mainIntent);
	}

	private void showSnackbar(String msg, boolean isError) {
		Snackbar snackbar = Snackbar.make(constraintLayout, msg, isError ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT);
		if (isError) {
			((TextView) snackbar.getView()
				.findViewById(android.support.design.R.id.snackbar_text))
				.setTextColor(Color.RED);
		}
		snackbar.show();
	}

	private void closeKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null) {
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			}
		}
	}

	public void onThemeModeClicked(View view) {
		switch (view.getId()) {
			case R.id.radio_light:
				kinTheme = KinTheme.LIGHT;
				break;
			case R.id.radio_dark:
				kinTheme = KinTheme.DARK;
				break;
		}
		SignInRepo.setKinTheme(this, kinTheme.name());
	}
}
