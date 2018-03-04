package com.kin.ecosystem.poll.presenter;

import android.support.annotation.NonNull;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.order.OrderRepository;
import com.kin.ecosystem.network.model.OpenOrder;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.poll.view.IPollWebView;
import com.kin.ecosystem.util.ExecutorsUtil.MainThreadExecutor;
import com.kin.ecosystem.web.EcosystemWebPageListener;


public class PollWebViewPresenter extends BasePresenter<IPollWebView> implements EcosystemWebPageListener {

    private final OrderRepository orderRepository;
    private final MainThreadExecutor mainThreadHandler = new MainThreadExecutor();

    private final String pollJsonString;
    private final String offerID;

    private Observer<OpenOrder> openOrderObserver;
    private OpenOrder openOrder;
    private boolean isOrderSubmitted = false;

    public PollWebViewPresenter(@NonNull final String pollJsonString, @NonNull final String offerID,
        @NonNull final OrderRepository orderRepository) {
        this.pollJsonString = pollJsonString;
        this.offerID = offerID;
        this.orderRepository = orderRepository;
    }

    @Override
    public void onAttach(IPollWebView view) {
        super.onAttach(view);
        view.loadUrl();
        listenToOpenOrders();
        createOrder();
    }

    private void createOrder() {
        orderRepository.createOrder(offerID, new Callback<OpenOrder>() {
            @Override
            public void onResponse(OpenOrder response) {
                // we are listening to open orders.
            }

            @Override
            public void onFailure(Throwable t) {
                if (view != null) {
                    showToast(t.getMessage());
                }
                closeView();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainThreadHandler.removeCallbacksAndMessages(null);
        release();
    }

    private void release() {
        if (openOrderObserver != null) {
            orderRepository.getOpenOrder().removeObserver(openOrderObserver);
        }
    }

    @Override
    public void onPageLoaded() {
        if (view != null) {
            view.renderJson(pollJsonString);
        }
    }

    @Override
    public void onPageCancel() {
        if (openOrder != null && !isOrderSubmitted) {
            orderRepository.cancelOrder(openOrder.getId(), null);
        }
        closeView();
    }

    @Override
    public void onPageResult(String result) {
        if (openOrder != null) {
            isOrderSubmitted = true;
            orderRepository.submitOrder(result, openOrder.getId(), new Callback<Order>() {
                @Override
                public void onResponse(Order response) {

                }

                @Override
                public void onFailure(Throwable t) {
                    showToast("Order submission failed");
                }
            });
        }
    }

    private void listenToOpenOrders() {
        openOrderObserver = new Observer<OpenOrder>() {
            @Override
            public void onChanged(OpenOrder value) {
                openOrder = value;
                if ((value != null)) {
                    showToast("OpenOrder: " + value.getId());
                }
            }
        };
        orderRepository.getOpenOrder().addObserver(openOrderObserver);
    }

    private void showToast(final String msg) {
        mainThreadHandler.execute(new Runnable() {
            @Override
            public void run() {
                if (view != null) {
                    view.showToast(msg);
                }
            }
        });
    }

    private void closeView() {
        mainThreadHandler.execute(new Runnable() {
            @Override
            public void run() {
                if (view != null) {
                    view.close();
                }
            }
        });
    }
}
