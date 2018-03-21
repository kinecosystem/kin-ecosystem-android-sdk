package com.kin.ecosystem.history.view;

import android.support.annotation.NonNull;
import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.base.IBottomDialogPresenter;
import com.kin.ecosystem.history.presenter.OrderHistoryPresenter;
import com.kin.ecosystem.network.model.Order;

import java.util.List;

public interface IOrderHistoryView extends IBaseView<OrderHistoryPresenter>{

    void updateOrderHistoryList(List<Order> orders);

    void onItemInserted();

    void onItemUpdated(int index);

    void showCouponDialog(@NonNull final IBottomDialogPresenter<ICouponDialog> presenter);
}
