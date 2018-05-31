package com.kin.ecosystem.history.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.widget.TextView;
import android.widget.Toast;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.BottomDialog;
import com.kin.ecosystem.base.IBottomDialogPresenter;

public class CouponDialog extends BottomDialog<IBottomDialogPresenter> implements ICouponDialog {

    private TextView couponCode;

    public CouponDialog(@NonNull Context context, @NonNull IBottomDialogPresenter presenter) {
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
        spannableTitleBuilder.setSpan(new URLSpan(url), descriptionText.length() + 1, spannableTitleBuilder.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        description.setText(spannableTitleBuilder);
        description.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void setupCouponCode(String code) {
        couponCode.setText(code);
    }
}
