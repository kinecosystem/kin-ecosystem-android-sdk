package com.kin.ecosystem.recovery.widget;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.kin.ecosystem.recovery.R;


public class PasswordEditText extends LinearLayout {

	private static final float LETTER_SPACING_PASSWORD = 0.4f;
	private static final float NO_LETTER_SPACING = 0f;
	private static final int DRAWABLE_RIGHT = 2;

	private EditText passwordField;
	private TextView errorText;

	private final int sidesPadding = getResources().getDimensionPixelSize(R.dimen.kinrecovery_margin_main);
	private final int strokeWidth = getResources().getDimensionPixelSize(R.dimen.kinrecovery_edittext_stroke_width);

	private boolean isRevealIconVisible;
	private boolean isRevealPressed;
	private final int passInputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;

	public PasswordEditText(Context context) {
		super(context, null);
	}

	public PasswordEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT));
		setLayoutTransition(new LayoutTransition());
		setOrientation(VERTICAL);
		boolean addRevealIcon;
		String hint;
		TypedArray styledAttributes = context.getTheme()
			.obtainStyledAttributes(attrs, R.styleable.KinRecoveryPasswordEditText, 0, 0);

		try {
			addRevealIcon = styledAttributes
				.getBoolean(R.styleable.KinRecoveryPasswordEditText_kinrecovery_show_reveal_icon, false);
			hint = styledAttributes.getString(R.styleable.KinRecoveryPasswordEditText_kinrecovery_hint);

		} finally {
			styledAttributes.recycle();
		}
		passwordField = new EditText(getContext());
		errorText = new TextView(getContext());

		setupPasswordField(addRevealIcon, hint);
		setupErrorText();

		addView(passwordField, 0);
		addView(errorText, 1);
	}

	@SuppressLint("ClickableViewAccessibility")
	private void setupPasswordField(boolean addRevealIcon, String hint) {
		final int topBottomPadding = getResources().getDimensionPixelSize(R.dimen.kinrecovery_margin_block);
		final int textSize = getResources().getDimensionPixelSize(R.dimen.kinrecovery_password_edit_text_size);
		final int colorGray = ContextCompat.getColor(getContext(), R.color.kinrecovery_gray);
		if (!TextUtils.isEmpty(hint)) {
			passwordField.setHint(hint);
		}
		passwordField.setMaxLines(1);
		passwordField.setSingleLine();
		passwordField.setLongClickable(false);
		passwordField.setTypeface(Typeface.SANS_SERIF);
		passwordField.setTextColor(colorGray);
		passwordField.setHintTextColor(colorGray);
		passwordField.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		passwordField.setPadding(sidesPadding, topBottomPadding, sidesPadding, topBottomPadding);
		passwordField.setHeight(getResources().getDimensionPixelSize(R.dimen.kinrecovery_edittext_height));
		passwordField.setWidth(getResources().getDimensionPixelSize(R.dimen.kinrecovery_password_edit_frame_height));
		passwordField.setFocusable(true);
		passwordField.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					openKeyboard();
				}
			}
		});
		passwordField.setGravity(Gravity.CENTER_VERTICAL);
		passwordField.setBackgroundResource(R.drawable.kinrecovery_edittext_frame);
		passwordField.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().length() > 0) {
					if (passwordField.getInputType() == passInputType) {
						setLetterSpacing(LETTER_SPACING_PASSWORD);
					} else {
						setLetterSpacing(NO_LETTER_SPACING);
					}
				} else {
					setLetterSpacing(NO_LETTER_SPACING);
				}
			}
		});
		setInputAsPasswordDots();
		if (addRevealIcon) {
			setRevealIconVisibility(true);
		}

		passwordField.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (isRevealIconVisible) {
					if (isInRevealIconBounds(event)) {
						switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN:
								setInputAsVisibleChars();
								return true;
							case MotionEvent.ACTION_UP:
								setInputAsPasswordDots();
								return true;
							default:
								return false;
						}
					}
					if (event.getAction() == MotionEvent.ACTION_MOVE && isRevealPressed) {
						setInputAsPasswordDots();

					}
					return false;
				}
				return false;
			}
		});
	}

	private void openKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getContext()
			.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null) {
			inputMethodManager.showSoftInput(passwordField, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	private void setLetterSpacing(float spacing) {
		if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
			passwordField.setLetterSpacing(spacing);
		}
	}

	private void setupErrorText() {
		final int textSize = getResources().getDimensionPixelSize(R.dimen.kinrecovery_password_edittext_error_size);
		final int color = ContextCompat.getColor(getContext(), R.color.kinrecovery_red);
		errorText.setVisibility(GONE);
		errorText.setTextColor(color);
		errorText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		errorText.setTypeface(Typeface.SANS_SERIF);
		errorText.setPadding(sidesPadding, 0, 0, 0);
	}

	private boolean isInRevealIconBounds(MotionEvent event) {
		final int drawableWidth = passwordField.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
		return (event.getRawX() >= (getRight() - drawableWidth - sidesPadding))
			&& (event.getRawX() <= (getRight() - sidesPadding));
	}

	private void setInputAsPasswordDots() {
		setRevealIconColor(R.color.kinrecovery_gray);
		passwordField.setInputType(passInputType);
		passwordField.setTransformationMethod(LargePasswordDotsTransformationMethod.getInstance());
		passwordField.setTypeface(Typeface.SANS_SERIF);
		passwordField.setSelection(passwordField.getText().length());
		isRevealPressed = false;
	}

	private void setInputAsVisibleChars() {
		setRevealIconColor(R.color.kinrecovery_bluePrimary);
		passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_FILTER);
		passwordField.setTransformationMethod(null);
		passwordField.setTypeface(Typeface.SANS_SERIF);
		isRevealPressed = true;
	}

	public void setFrameBackgroundColor(@ColorRes final int colorRes) {
		GradientDrawable background = (GradientDrawable) passwordField.getBackground();
		if (background != null) {
			final int color = ContextCompat.getColor(getContext(), colorRes);
			background.setStroke(strokeWidth, color);

		}
	}

	public void setRevealIconVisibility(final boolean isVisible) {
		Drawable revealDrawable = passwordField.getCompoundDrawables()[DRAWABLE_RIGHT];
		if (isVisible) {
			isRevealIconVisible = true;
			if (revealDrawable == null) {
				revealDrawable = ContextCompat.getDrawable(getContext(), R.drawable.kinrecovery_grey_reveal_icon);
				passwordField.setCompoundDrawablesWithIntrinsicBounds(null, null, revealDrawable, null);
			} else {
				revealDrawable.setVisible(true, true);
			}

		} else {
			isRevealIconVisible = false;
			if (revealDrawable != null) {
				revealDrawable.setVisible(false, true);
			}
		}
	}

	public void setRevealIconColor(@ColorRes final int colorRes) {
		Drawable revealDrawable = passwordField.getCompoundDrawables()[DRAWABLE_RIGHT];
		if (revealDrawable != null) {
			final int color = ContextCompat.getColor(getContext(), colorRes);
			revealDrawable.setColorFilter(color, Mode.SRC_ATOP);
		}
	}

	public void addTextChangedListener(final TextWatcher textWatcher) {
		passwordField.addTextChangedListener(textWatcher);
	}

	public String getText() {
		return passwordField.getText().toString();
	}

	public void showError(@StringRes final int stringRes) {
		errorText.setText(stringRes);
		errorText.setVisibility(VISIBLE);
	}

	public void removeError() {
		if (errorText.getVisibility() == VISIBLE) {
			errorText.setVisibility(GONE);
			errorText.setText("");
		}
	}

}
