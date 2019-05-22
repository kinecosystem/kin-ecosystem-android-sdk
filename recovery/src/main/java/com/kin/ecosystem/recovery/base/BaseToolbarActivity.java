package com.kin.ecosystem.recovery.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.kin.ecosystem.recovery.KinRecoveryTheme;
import com.kin.ecosystem.recovery.R;
import com.kin.ecosystem.recovery.widget.KinRecoveryToolbar;


public abstract class BaseToolbarActivity extends AppCompatActivity implements KeyboardHandler {

	public static final int EMPTY_TITLE = -1;
	public static final String KEY_THEME = "kin_recovery_theme";

	protected abstract @LayoutRes
	int getContentLayout();

	private KinRecoveryToolbar topToolBar;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(getKinRecoveryTheme());
		setContentView(getContentLayout());
		topToolBar = findViewById(R.id.toolbar);
	}

	private int getKinRecoveryTheme() {
		final String theme = getIntent().getStringExtra(KEY_THEME);
		if (theme == null) {
			return R.style.KinEcosystem_Light;
		} else {
			KinRecoveryTheme kinRecoveryTheme = KinRecoveryTheme.valueOf(theme);
			switch (kinRecoveryTheme) {
				default:
				case LIGHT:
					return R.style.KinRecovery_Light;
				case DARK:
					return R.style.KinRecovery_Dark;
			}
		}
	}

	public void setToolbarTitle(@StringRes int titleRes) {
		if (titleRes != EMPTY_TITLE) {
			topToolBar.setTitle(titleRes);
		} else {
			topToolBar.setTitle("");
		}
	}

	public void setNavigationIcon(@DrawableRes int iconRes) {
		topToolBar.setNavigationIcon(iconRes);
	}

	public void setStep(int current, int total) {
		topToolBar.setStepText(getString(R.string.kinrecovery_steps_format, current, total));
	}

	public void clearSteps() {
		topToolBar.setStepText("");
	}

	public void setNavigationClickListener(View.OnClickListener clickListener) {
		topToolBar.setNavigationOnClickListener(clickListener);
	}

	@Override
	public void openKeyboard(View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null) {
			view.requestFocus();
			inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		}
	}

	@Override
	public void closeKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null) {
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			}
		}
	}
}
