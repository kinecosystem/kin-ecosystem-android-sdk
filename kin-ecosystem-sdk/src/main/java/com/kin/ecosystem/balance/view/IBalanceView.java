package com.kin.ecosystem.balance.view;

import android.support.annotation.Nullable;
import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.network.model.Order.Status;

public interface IBalanceView extends IBaseView<IBasePresenter<IBalanceView>> {

	void updateBalance(String balance);

	void updateSubTitle(String subTitle, @Nullable Status status);
}
