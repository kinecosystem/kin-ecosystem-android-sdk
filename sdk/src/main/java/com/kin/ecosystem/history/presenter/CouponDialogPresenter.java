package com.kin.ecosystem.history.presenter;

import android.support.annotation.NonNull;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.BaseDialogPresenter;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.bi.events.RedeemUrlTapped;
import com.kin.ecosystem.core.bi.events.SpendRedeemButtonTapped;
import com.kin.ecosystem.core.bi.events.SpendRedeemPageViewed;
import com.kin.ecosystem.core.bi.events.SpendRedeemPageViewed.RedeemTrigger;
import com.kin.ecosystem.core.network.model.Coupon;
import com.kin.ecosystem.core.network.model.Coupon.CouponInfo;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.history.view.ICouponDialog;

public class CouponDialogPresenter extends BaseDialogPresenter<ICouponDialog> implements ICouponDialogPresenter {

	private final EventLogger eventLogger;
    private final Coupon coupon;
    private final Order order;
    private final RedeemTrigger redeemTrigger;
    private static final String HTTP_URL_PATTERN= "http://";
    private static final String HTTPS_URL_PATTERN= "https://";

    CouponDialogPresenter(@NonNull final Coupon coupon, @NonNull Order order, RedeemTrigger redeemTrigger, @NonNull EventLogger eventLogger) {
        this.eventLogger = eventLogger;
        this.coupon = coupon;
        this.order = order;
        this.redeemTrigger = redeemTrigger;
    }

    @Override
    public void onAttach(ICouponDialog view) {
        super.onAttach(view);
		loadInfo();
		eventLogger.send(SpendRedeemPageViewed.create(redeemTrigger, (double) order.getAmount(), order.getOfferId(), order.getOrderId()));
    }

    private void loadInfo() {
        if (getView() != null && coupon != null) {
            CouponInfo info = coupon.getCouponInfo();
            getView().setupImage(info.getImage());
            getView().setupTitle(info.getTitle());
            getView().setUpRedeemDescription(info.getDescription(), info.getLink(), createUrl(info.getLink()));
            if(coupon.getCouponCode() != null) {
                getView().setupCouponCode(coupon.getCouponCode().getCode());
            }
            getView().setUpButtonText(R.string.kinecosystem_copy_code);
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
		eventLogger.send(SpendRedeemButtonTapped.create((double)order.getAmount(), order.getOfferId(), order.getOrderId()));
        copyCouponCodeToClipboard();
    }

    private void copyCouponCodeToClipboard() {
        if (getView() != null && coupon.getCouponCode() != null) {
            getView().copyCouponCode(coupon.getCouponCode().getCode());
        }
    }

    @Override
    public void redeemUrlClicked() {
        eventLogger.send(RedeemUrlTapped.create());
        if(getView() != null) {
            String url = createUrl(coupon.getCouponInfo().getLink());
            getView().openUrl(url);
        }
    }
}
