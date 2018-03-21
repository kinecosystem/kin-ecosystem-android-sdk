package com.kin.ecosystem.history.view;

import com.kin.ecosystem.base.IBottomDialogPresenter;
import com.kin.ecosystem.base.IBottomDialog;

public interface ICouponDialog extends IBottomDialog<IBottomDialogPresenter> {

    void copyCouponCode(String couponCode);

    void setUpRedeemDescription(String description, String clickableText, String url);

    void setupCouponCode(String code);
}
