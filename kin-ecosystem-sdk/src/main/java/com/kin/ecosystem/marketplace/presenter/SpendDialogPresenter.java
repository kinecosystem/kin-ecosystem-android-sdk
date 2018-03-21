package com.kin.ecosystem.marketplace.presenter;

import android.os.Handler;
import android.util.Log;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.BaseDialogPresenter;
import com.kin.ecosystem.data.blockchain.BlockchainSource;
import com.kin.ecosystem.data.blockchain.IBlockchainSource;
import com.kin.ecosystem.data.order.OrderDataSource;
import com.kin.ecosystem.marketplace.view.ISpendDialog;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.OfferInfo;
import com.kin.ecosystem.network.model.OfferInfo.Confirmation;
import com.kin.ecosystem.network.model.OpenOrder;
import com.kin.ecosystem.network.model.Order;
import java.math.BigDecimal;


public class SpendDialogPresenter extends BaseDialogPresenter<ISpendDialog> implements ISpendDialogPresenter {

    private static final String TAG = SpendDialogPresenter.class.getSimpleName();

    private final OrderDataSource orderRepository;
    private final IBlockchainSource blockchainSource;

    private final Handler handler = new Handler();

    private final OfferInfo offerInfo;
    private final Offer offer;
    private OpenOrder openOrder;

    private boolean isOrderSubmitted;

    private static final int CLOSE_DELAY = 2000;

    public SpendDialogPresenter(OfferInfo offerInfo, Offer offer, BlockchainSource blockchainSource,
        OrderDataSource orderRepository) {
        this.offerInfo = offerInfo;
        this.offer = offer;
        this.orderRepository = orderRepository;
        this.blockchainSource = blockchainSource;
    }

    @Override
    public void onAttach(final ISpendDialog view) {
        super.onAttach(view);
        createOrder();
        loadInfo();
    }

    private void createOrder() {
        orderRepository.createOrder(offer.getId(), new Callback<OpenOrder>() {
            @Override
            public void onResponse(OpenOrder response) {
                openOrder = response;
            }

            @Override
            public void onFailure(Throwable t) {
                showToast("Oops something went wrong...");
            }
        });
    }

    private void loadInfo() {
        if (view != null) {
            view.setupImage(offerInfo.getImage());
            view.setupTitle(offerInfo.getTitle(), offerInfo.getAmount());
            view.setupDescription(offerInfo.getDescription());
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
        if (view != null) {

            if (openOrder != null) {
                int balance = blockchainSource.getBalance();
                final BigDecimal amount = new BigDecimal(offer.getAmount());

                if (balance < amount.intValue()) {
                    showToast("You don't have enough Kin");
                    closeDialog();
                    return;
                }

                final String addressee = offer.getBlockchainData().getRecipientAddress();
                final String orderID = openOrder.getId();

                submitOrder(offer.getId(), orderID);
                sendTransaction(addressee, amount, orderID);
            }

            Confirmation confirmation = offerInfo.getConfirmation();
            view.showThankYouLayout(confirmation.getTitle(), confirmation.getDescription());
            closeDialogWithDelay(CLOSE_DELAY);
        }
    }

    @Override
    public void dialogDismissed() {
        if (isOrderSubmitted) {
            orderRepository.isFirstSpendOrder(new Callback<Boolean>() {
                @Override
                public void onResponse(Boolean response) {
                    Log.d(TAG, "isFirstSpendOrder: " + response);
                    if (response) {
                        navigateToOrderHistory();
                        orderRepository.setIsFirstSpendOrder(false);
                    }
                    onDetach();
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        }
    }

    private void navigateToOrderHistory() {
        if (view != null) {
            view.navigateToOrderHistory();
        }
    }

    private void closeDialogWithDelay(int delayMilliseconds) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                closeDialog();
            }
        }, delayMilliseconds);
    }

    private void sendTransaction(String addressee, BigDecimal amount, String orderID) {
        blockchainSource.sendTransaction(addressee, amount, orderID);
    }

    private void submitOrder(String offerID, String orderID) {
        isOrderSubmitted = true;
        orderRepository.submitOrder(offerID, null, orderID, new Callback<Order>() {
            @Override
            public void onResponse(Order response) {
                Log.i(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Throwable t) {
                //TODO handle failure
                Log.i(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void showToast(String msg) {
        if (view != null) {
            view.showToast(msg);
        }
    }
}
