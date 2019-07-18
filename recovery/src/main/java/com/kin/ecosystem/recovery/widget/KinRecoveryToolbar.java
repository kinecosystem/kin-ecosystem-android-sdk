package com.kin.ecosystem.recovery.widget;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.kin.ecosystem.recovery.R;

public class KinRecoveryToolbar extends ConstraintLayout {

	private ImageView navigationIcon;
	private TextView title;
	private TextView steps;

	public KinRecoveryToolbar(Context context) {
		super(context);
		init();
	}

	public KinRecoveryToolbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public KinRecoveryToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.kinrecovery_toolbar_layout, this, true);
		final int height = getActionBarHeight();
		setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
		navigationIcon = findViewById(R.id.navigation_icon);
		title = findViewById(R.id.title);
		steps = findViewById(R.id.steps_text);
	}

	private int getActionBarHeight() {
		TypedValue tv = new TypedValue();
		if (getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			return TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
		} else {
			return getResources().getDimensionPixelSize(R.dimen.kinrecovery_action_bar_size);
		}
	}

	public void setNavigationIcon(@DrawableRes int iconRes) {
		navigationIcon.setImageResource(iconRes);
	}

	public void setStepText(String text) {
		steps.setText(text);
	}

	public void setNavigationOnClickListener(OnClickListener clickListener) {
		navigationIcon.setOnClickListener(clickListener);
	}

	public void setTitle(@StringRes int titleRes) {
		title.setText(titleRes);
	}

	public void setTitle(String text) {
		title.setText(text);
	}
}
