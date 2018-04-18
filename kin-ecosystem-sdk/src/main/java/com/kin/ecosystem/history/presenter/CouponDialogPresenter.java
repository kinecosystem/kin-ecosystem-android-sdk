package com.kin.ecosystem.history.presenter;

import android.support.annotation.NonNull;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.BaseDialogPresenter;
import com.kin.ecosystem.base.IBottomDialogPresenter;
import com.kin.ecosystem.data.model.Coupon;
import com.kin.ecosystem.data.model.Coupon.CouponInfo;
import com.kin.ecosystem.history.view.ICouponDialog;

public class CouponDialogPresenter extends BaseDialogPresenter<ICouponDialog> implements
    IBottomDialogPresenter<ICouponDialog> {

    private final Coupon coupon;
    private static final String HTTP_URL_PATTERN= "http://";
    private static final String HTTPS_URL_PATTERN= "https://";

    public CouponDialogPresenter(@NonNull final Coupon coupon) {
        this.coupon = coupon;
    }

    @Override
    public void onAttach(ICouponDialog view) {
        super.onAttach(view);
        loadInfo();
    }

    private void loadInfo() {
        if (view != null) {
            CouponInfo info = coupon.getCouponInfo();
            view.setupImage(info.getImage());
            view.setupTitle(info.getTitle());
            view.setUpRedeemDescription(info.getDescription(), info.getLink(), createUrl(info.getLink()));
            view.setupCouponCode(coupon.getCouponCode().getCode());
            view.setUpButtonText(R.string.copy_code);
        }
    }

    private String createUrl(String link) {
        if (link.contains(HTTP_URL_PATTERN) || link.contains(HTTPS_URL_PATTERN)) {
            return link;
        } else {
            return HTTP_URL_PATTERN + link;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void closeClicked() {
        closeDialog();
    }

    @Override
    public void bottomButtonClicked() {
        copyCouponCodeToClipboard();
    }

    private void copyCouponCodeToClipboard() {
        if (view != null) {
            view.copyCouponCode(coupon.getCouponCode().getCode());
        }
    }
}
