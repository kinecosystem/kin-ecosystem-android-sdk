package com.kin.ecosystem.history.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.BottomDialog;
import com.kin.ecosystem.history.presenter.ICouponDialogPresenter;

public class CouponDialog extends BottomDialog<ICouponDialogPresenter> implements ICouponDialog {

	private TextView couponCode;

	public CouponDialog(@NonNull Context context, @NonNull ICouponDialogPresenter presenter) {
		super(context, presenter, R.layout.kinecosystem_dialog_coupon_code);

	}

	@Override
	protected void initViews() {
		couponCode = findViewById(R.id.coupon_code);
	}

	@Override
	public void copyCouponCode(String couponCode) {
		ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("copied text", couponCode);
		clipboard.setPrimaryClip(clip);

		Toast.makeText(getContext(), "Copied to your clipboard", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void setUpRedeemDescription(String descriptionText, String clickableText, String url) {
		SpannableStringBuilder spannableTitleBuilder = new SpannableStringBuilder(descriptionText).append(" ")
			.append(clickableText);

		spannableTitleBuilder.setSpan(new ForegroundColorSpan(colorBlue),
			descriptionText.length() + 1, spannableTitleBuilder.length(),
			Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		final URLSpan urlSpan = new URLSpan(url);
		spannableTitleBuilder.setSpan(urlSpan, descriptionText.length() + 1, spannableTitleBuilder.length(),
			Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		int start = spannableTitleBuilder.getSpanStart(urlSpan);
		int end = spannableTitleBuilder.getSpanEnd(urlSpan);
		int flags = spannableTitleBuilder.getSpanFlags(urlSpan);
		ClickableSpan linkOnClick = new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				presenter.redeemUrlClicked();
			}
		};
		spannableTitleBuilder.setSpan(linkOnClick, start, end, flags);
		spannableTitleBuilder.removeSpan(urlSpan);

		description.setText(spannableTitleBuilder);
		description.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public void setupCouponCode(String code) {
		couponCode.setText(code);
	}

	@Override
	public void openUrl(String url) {

		Intent webIntent = new Intent(Intent.ACTION_VIEW);
		webIntent.setData(Uri.parse(url));
		getContext().startActivity(webIntent);
	}
}
