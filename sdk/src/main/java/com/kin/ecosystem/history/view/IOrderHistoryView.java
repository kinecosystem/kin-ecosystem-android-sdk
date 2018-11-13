package com.kin.ecosystem.history.view;

import android.support.annotation.NonNull;
import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.history.presenter.ICouponDialogPresenter;
import com.kin.ecosystem.history.presenter.IOrderHistoryPresenter;
import java.util.List;

public interface IOrderHistoryView extends IBaseView<IOrderHistoryPresenter>{

    void updateOrderHistoryList(List<Order> orders);

    void onItemInserted();

    void onItemUpdated(int index);

    void showCouponDialog(@NonNull final ICouponDialogPresenter presenter);
}
