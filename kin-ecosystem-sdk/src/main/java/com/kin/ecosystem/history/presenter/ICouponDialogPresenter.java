package com.kin.ecosystem.history.presenter;

import com.kin.ecosystem.base.IBottomDialogPresenter;
import com.kin.ecosystem.history.view.ICouponDialog;


public interface ICouponDialogPresenter extends IBottomDialogPresenter<ICouponDialog> {

	void redeemUrlClicked();
}
