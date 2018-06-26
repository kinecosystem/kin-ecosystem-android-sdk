package com.kin.ecosystem.history.presenter;

import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.history.view.IOrderHistoryView;

public interface IOrderHistoryPresenter extends IBasePresenter<IOrderHistoryView> {

    void backButtonPressed();

    void onItemCLicked(int position);
}
