package com.kin.ecosystem.poll.presenter;

import android.support.annotation.NonNull;
import com.kin.ecosystem.KinCallback;
import com.kin.ecosystem.data.Callback;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.order.OrderDataSource;
import com.kin.ecosystem.exception.KinEcosystemException;
import com.kin.ecosystem.network.model.OpenOrder;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.poll.view.IPollWebView;


public class PollWebViewPresenter extends BasePresenter<IPollWebView> implements IPollWebViewPresenter {

    private final OrderDataSource orderRepository;

    private final String pollJsonString;
    private final String offerID;
    private final String title;

    private Observer<OpenOrder> openOrderObserver;
    private OpenOrder openOrder;
    private boolean isOrderSubmitted = false;

    public PollWebViewPresenter(@NonNull final String pollJsonString, @NonNull final String offerID,
        String title, @NonNull final OrderDataSource orderRepository) {
        this.pollJsonString = pollJsonString;
        this.offerID = offerID;
        this.title = title;
        this.orderRepository = orderRepository;
    }

    @Override
    public void onAttach(IPollWebView view) {
        super.onAttach(view);
        loadUrl();
        setTitle(title);
        listenToOpenOrders();
        createOrder();
    }

    private void loadUrl() {
        if (view != null) {
            view.loadUrl();
        }
    }

    private void setTitle(String title) {
        if (view != null) {
            view.setTitle(title);
        }
    }

    private void createOrder() {
        orderRepository.createOrder(offerID, new KinCallback<OpenOrder>() {
            @Override
            public void onResponse(OpenOrder response) {
                // we are listening to open orders.
            }

            @Override
            public void onFailure(KinEcosystemException error) {
                if (view != null) {
                    showToast(error.getMessage());
                }
                closeView();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
    public void closeClicked() {
        cancelOrderAndClose();
    }

    @Override
    public void onPageCancel() {
        cancelOrderAndClose();
    }

    private void cancelOrderAndClose() {
        if (openOrder != null && !isOrderSubmitted) {
            orderRepository.cancelOrder(offerID, openOrder.getId(), null);
        }
        closeView();
    }

    @Override
    public void onPageResult(String result) {
        if (openOrder != null) {
            isOrderSubmitted = true;
            orderRepository.submitOrder(offerID, result, openOrder.getId(), new KinCallback<Order>() {
                @Override
                public void onResponse(Order response) {

                }

                @Override
                public void onFailure(KinEcosystemException error) {
                    showToast("Order submission failed");
                }
            });
        }
    }

    @Override
    public void onPageClosed() {
        closeView();
    }

    private void listenToOpenOrders() {
        openOrderObserver = new Observer<OpenOrder>() {
            @Override
            public void onChanged(OpenOrder value) {
                openOrder = value;
            }
        };
        orderRepository.getOpenOrder().addObserver(openOrderObserver);
    }

    private void showToast(final String msg) {
        if (view != null) {
            view.showToast(msg);
        }
    }

    private void closeView() {
        if (view != null) {
            view.close();
        }
    }

    @Override
    public void showToolbar() {
        if (view != null) {
            view.showToolbar();
        }
    }

    @Override
    public void hideToolbar() {
        if (view != null) {
            view.hideToolbar();
        }
    }
}
