package com.kin.ecosystem.recovery.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.Gravity;
import com.kin.ecosystem.recovery.R;


public class PasswordEditText extends AppCompatEditText {

	public static final int INDEX_RIGHT = 2;
	private boolean hasRevealIcon;

	public PasswordEditText(Context context) {
		super(context);
	}

	public PasswordEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		boolean addRevealIcon;
		TypedArray styledAttributes = context.getTheme()
			.obtainStyledAttributes(attrs, R.styleable.KinRecoveryPasswordEditText, 0, 0);

		try {
			addRevealIcon = styledAttributes
				.getBoolean(R.styleable.KinRecoveryPasswordEditText_kinrecovery_show_reveal_icon, false);
		} finally {
			styledAttributes.recycle();
		}

		final int sidesPadding = getResources().getDimensionPixelSize(R.dimen.kinrecovery_margin_main);
		final int topBottomPadding = getResources().getDimensionPixelSize(R.dimen.kinrecovery_margin_block);
		setPadding(sidesPadding, topBottomPadding, sidesPadding, topBottomPadding);
		setGravity(Gravity.CENTER_VERTICAL);
		setBackgroundResource(R.drawable.kinrecovery_edittext_frame);
		if (addRevealIcon) {
			setRevealIconVisibility(true);
		}
	}

	public void setBackgroundColor(@ColorRes final int colorRes) {
		Drawable background = getBackground();
		if (background != null) {
			final int color = ContextCompat.getColor(getContext(), colorRes);
			background.setColorFilter(color, Mode.SRC_ATOP);
		}
	}

	public void setRevealIconVisibility(final boolean isVisible) {
		Drawable revealDrawable = getCompoundDrawables()[INDEX_RIGHT];
		if (isVisible) {
			if (revealDrawable == null) {
				revealDrawable = ContextCompat.getDrawable(getContext(), R.drawable.kinrecovery_grey_reveal_icon);
				setCompoundDrawables(null, null, revealDrawable, null);
			} else {
				revealDrawable.setVisible(true, true);
			}

		} else {
			if (revealDrawable != null) {
				revealDrawable.setVisible(false, true);
			}
		}
	}

	public void setRevealIconColor(@ColorRes final int colorRes) {
		Drawable revealDrawable = getCompoundDrawables()[INDEX_RIGHT];
		if (revealDrawable != null) {
			final int color = ContextCompat.getColor(getContext(), colorRes);
			revealDrawable.setColorFilter(color, Mode.SRC_ATOP);
		}
	}

}
