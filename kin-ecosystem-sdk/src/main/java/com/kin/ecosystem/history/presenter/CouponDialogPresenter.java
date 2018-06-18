package com.kin.ecosystem.history.presenter;

import android.support.annotation.NonNull;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.BaseDialogPresenter;
import com.kin.ecosystem.bi.events.RedeemUrlTapped;
import com.kin.ecosystem.bi.events.SpendRedeemButtonTapped;
import com.kin.ecosystem.bi.events.SpendRedeemPageViewed;
import com.kin.ecosystem.bi.events.SpendRedeemPageViewed.RedeemTrigger;
import com.kin.ecosystem.data.model.Coupon;
import com.kin.ecosystem.data.model.Coupon.CouponInfo;
import com.kin.ecosystem.history.view.ICouponDialog;
import com.kin.ecosystem.network.model.Order;

public class CouponDialogPresenter extends BaseDialogPresenter<ICouponDialog> implements ICouponDialogPresenter {

    private final Coupon coupon;
    private final Order order;
    private final RedeemTrigger redeemTrigger;
    private static final String HTTP_URL_PATTERN= "http://";
    private static final String HTTPS_URL_PATTERN= "https://";

    CouponDialogPresenter(@NonNull final Coupon coupon, @NonNull Order order, RedeemTrigger redeemTrigger) {
        this.coupon = coupon;
        this.order = order;
        this.redeemTrigger = redeemTrigger;
    }

    @Override
    public void onAttach(ICouponDialog view) {
        super.onAttach(view);
		loadInfo();
		SpendRedeemPageViewed.fire(redeemTrigger, (double) order.getAmount(), order.getOfferId(), order.getOrderId());
    }

    private void loadInfo() {
        if (view != null && coupon != null) {
            CouponInfo info = coupon.getCouponInfo();
            view.setupImage(info.getImage());
            view.setupTitle(info.getTitle());
            view.setUpRedeemDescription(info.getDescription(), info.getLink(), createUrl(info.getLink()));
            if(coupon.getCouponCode() != null) {
                view.setupCouponCode(coupon.getCouponCode().getCode());
            }
            view.setUpButtonText(R.string.kinecosystem_copy_code);
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
		SpendRedeemButtonTapped.fire((double)order.getAmount(), order.getOfferId(), order.getOrderId());
        copyCouponCodeToClipboard();
    }

    private void copyCouponCodeToClipboard() {
        if (view != null && coupon.getCouponCode() != null) {
            view.copyCouponCode(coupon.getCouponCode().getCode());
        }
    }

    @Override
    public void redeemUrlClicked() {
        RedeemUrlTapped.fire();
        if(view != null) {
            String url = createUrl(coupon.getCouponInfo().getLink());
            view.openUrl(url);
        }
    }
}
