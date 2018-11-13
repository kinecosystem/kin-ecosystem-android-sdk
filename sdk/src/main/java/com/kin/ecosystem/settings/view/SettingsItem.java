package com.kin.ecosystem.settings.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.kin.ecosystem.R;
import com.kin.ecosystem.widget.TouchIndicatorIcon;

public class SettingsItem extends LinearLayout {

	private TouchIndicatorIcon touchIndicatorIcon;
	private TextView title;

	public SettingsItem(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		int height = getResources().getDimensionPixelSize(R.dimen.kinecosystem_settings_item_height);
		setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER);
		setWeightSum(1f);

		LayoutInflater.from(context).inflate(R.layout.kinecosystem_settings_item_layout, this, true);
		touchIndicatorIcon = findViewById(R.id.settings_icon);
		title = findViewById(R.id.settings_text);

		final int icon;
		final boolean indicatorVisibility;
		final String text;
		TypedArray styledAttributes = context.getTheme()
			.obtainStyledAttributes(attrs, R.styleable.KinEcosystemSettingsItem, 0, 0);

		try {
			icon = styledAttributes.getResourceId(R.styleable.KinEcosystemSettingsItem_kinecosystem_src, -1);
			indicatorVisibility = styledAttributes
				.getBoolean(R.styleable.KinEcosystemSettingsItem_kinecosystem_indicatorVisibility, false);
			text = styledAttributes.getString(R.styleable.KinEcosystemSettingsItem_kinecosysem_text);
		} finally {
			styledAttributes.recycle();
		}

		if (icon != -1) {
			touchIndicatorIcon.setIcon(icon);
			touchIndicatorIcon.setTouchIndicatorVisibility(indicatorVisibility);
		}

		title.setText(text);
	}

	public void setIcon(@DrawableRes final int iconRes) {
		touchIndicatorIcon.setIcon(iconRes);
	}

	public void changeIconColor(@ColorRes final int colorRes) {
		touchIndicatorIcon.setIconColor(colorRes);
	}

	public void setTouchIndicatorVisibility(final boolean isVisible) {
		touchIndicatorIcon.setTouchIndicatorVisibility(isVisible);
	}

	public void setText(@StringRes final int stringRes) {
		title.setText(stringRes);
	}
}
