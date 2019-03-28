package com.kin.ecosystem.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.kin.ecosystem.R;

public class TouchIndicatorIcon extends View {

	private static final int ALPHA_255 = 255;
	private static final int ALPHA_0 = 0;

	private static final int ID_ICON = 0x00000001;
	private static final int ID_INDICATOR = 0x00000002;

	private LayerDrawable layerDrawable;

	private final int iconSize = getResources().getDimensionPixelSize(R.dimen.kinecosystem_settings_icon_size);
	private final int strokeWidth = getResources().getDimensionPixelSize(R.dimen.kinecosystem_stroke_width);
	private final int strokePadding = getResources().getDimensionPixelSize(R.dimen.kinecosystem_stroke_small_padding);
	private final int indicatorRadius =
		getResources().getDimensionPixelSize(R.dimen.kinecosystem_info_dot_radius) + strokeWidth + strokePadding;

	public TouchIndicatorIcon(@NonNull Context context) {
		super(context, null);

	}

	public TouchIndicatorIcon(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs, 0);
		final int iconRes;

		final boolean indicatorVisibility;
		setLayoutParams(new ViewGroup.LayoutParams(iconSize, iconSize));

		TypedArray styledAttributes = context.getTheme()
			.obtainStyledAttributes(attrs, R.styleable.KinEcosystemTouchIndicatorIcon, 0, 0);

		try {
			iconRes = styledAttributes.getResourceId(R.styleable.KinEcosystemTouchIndicatorIcon_src, -1);
			indicatorVisibility = styledAttributes.getBoolean(R.styleable.KinEcosystemTouchIndicatorIcon_kinecosystem_indicatorVisibility, false);
		} finally {
			styledAttributes.recycle();
		}

		if(iconRes != -1) {
			final Drawable icon = ContextCompat.getDrawable(context, iconRes);
			if (icon != null) {
				setIcon(icon);
				setTouchIndicatorVisibility(indicatorVisibility);
			}
		}
	}

	public void setIcon(@DrawableRes final int iconRes) {
		final Drawable icon = ContextCompat.getDrawable(getContext(), iconRes);
		setIcon(icon);
	}

	private void setIcon(final Drawable icon) {
		final Drawable[] drawables = new Drawable[2];
		icon.setBounds(0, 0, iconSize, iconSize);
		drawables[0] = icon;
		Drawable touchIndicator = ContextCompat.getDrawable(getContext(), R.drawable.kinecosystem_info_red_dot);
		final int left = icon.getIntrinsicWidth() - indicatorRadius;
		touchIndicator.setBounds(left, 0, indicatorRadius + left, indicatorRadius);
		drawables[1] = touchIndicator;
		layerDrawable = new LayerDrawable(drawables);
		layerDrawable.setId(0, ID_ICON);
		layerDrawable.setId(1, ID_INDICATOR);
	}

	public void setIconColor(@ColorRes final int colorRes) {
		if (layerDrawable != null) {
			Drawable icon = layerDrawable.findDrawableByLayerId(ID_ICON);
			if (icon != null) {
				final int color = ContextCompat.getColor(getContext(), colorRes);
				icon.setColorFilter(color, Mode.SRC_ATOP);
				invalidate();
			}
		}
	}

	public void setTouchIndicatorVisibility(boolean isVisible) {
		if (layerDrawable != null) {
			Drawable touchIndicator = layerDrawable.findDrawableByLayerId(ID_INDICATOR);
			if (touchIndicator != null) {
				if (isVisible) {
					touchIndicator.setAlpha(ALPHA_255);
				} else {
					touchIndicator.setAlpha(ALPHA_0);
				}
				invalidate();
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(iconSize, iconSize);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (layerDrawable != null && layerDrawable.getNumberOfLayers() > 0) {
			layerDrawable.draw(canvas);
		}
	}
}
