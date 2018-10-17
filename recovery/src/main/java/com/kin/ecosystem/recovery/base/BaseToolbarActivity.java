package com.kin.ecosystem.recovery.base;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.kin.ecosystem.recovery.R;


public abstract class BaseToolbarActivity extends AppCompatActivity {

	public static final int EMPTY_TITLE = -1;

	protected abstract @LayoutRes
	int getContentLayout();

	private Toolbar topToolBar;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentLayout());
		setupToolbar();
	}

	private void setupToolbar() {
		topToolBar = findViewById(R.id.toolbar);

		setSupportActionBar(topToolBar);
	}

	public void setToolbarTitle(@StringRes int titleRes) {
		topToolBar = findViewById(R.id.toolbar);
		if (titleRes != EMPTY_TITLE) {
			getSupportActionBar().setTitle(titleRes);
		} else {
			getSupportActionBar().setTitle("");
		}
	}

	public void setNavigationIcon(@DrawableRes int iconRes) {
		topToolBar.setNavigationIcon(iconRes);
	}

	public void setToolbarColor(@ColorRes int colorRes) {
		topToolBar.setBackgroundResource(colorRes);
	}

	public void setStep(int current, int total) {

	}

	public void setNavigationClickListener(View.OnClickListener clickListener) {
		topToolBar.setNavigationOnClickListener(clickListener);
	}

}
