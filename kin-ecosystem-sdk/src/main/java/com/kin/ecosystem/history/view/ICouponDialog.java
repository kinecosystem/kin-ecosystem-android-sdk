package com.kin.ecosystem.history.view;

import com.kin.ecosystem.base.IBottomDialog;
import com.kin.ecosystem.history.presenter.ICouponDialogPresenter;

public interface ICouponDialog extends IBottomDialog<ICouponDialogPresenter> {

	void copyCouponCode(String couponCode);

	void setUpRedeemDescription(String description, String clickableText, String url);

	void setupCouponCode(String code);

	void openUrl(String url);
}
