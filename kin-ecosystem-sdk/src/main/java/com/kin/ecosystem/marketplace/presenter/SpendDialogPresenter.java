package com.kin.ecosystem.marketplace.presenter;

import android.os.Handler;
import android.util.Log;
import com.kin.ecosystem.KinCallback;
import com.kin.ecosystem.base.BaseDialogPresenter;
import com.kin.ecosystem.bi.events.CloseButtonOnOfferPageTapped;
import com.kin.ecosystem.bi.events.ConfirmPurchaseButtonTapped;
import com.kin.ecosystem.bi.events.ConfirmPurchasePageViewed;
import com.kin.ecosystem.bi.events.SpendOrderCancelled;
import com.kin.ecosystem.bi.events.SpendOrderCompletionSubmitted;
import com.kin.ecosystem.bi.events.SpendOrderCreationFailed;
import com.kin.ecosystem.bi.events.SpendOrderCreationReceived;
import com.kin.ecosystem.bi.events.SpendOrderCreationRequested;
import com.kin.ecosystem.bi.events.SpendThankyouPageViewed;
import com.kin.ecosystem.bi.events.SpendTransactionBroadcastToBlockchainSubmitted;
import com.kin.ecosystem.data.blockchain.BlockchainSource;
import com.kin.ecosystem.data.order.OrderDataSource;
import com.kin.ecosystem.exception.KinEcosystemException;
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
    private final BlockchainSource blockchainSource;

    private final Handler handler = new Handler();

    private final OfferInfo offerInfo;
    private final Offer offer;
    private OpenOrder openOrder;

	private final BigDecimal amount;

    private boolean isOrderSubmitted;

    private static final int CLOSE_DELAY = 2000;

    public SpendDialogPresenter(OfferInfo offerInfo, Offer offer, BlockchainSource blockchainSource,
        OrderDataSource orderRepository) {
        this.offerInfo = offerInfo;
        this.offer = offer;
        this.orderRepository = orderRepository;
        this.blockchainSource = blockchainSource;
        this.amount = new BigDecimal(offer.getAmount());
    }

    @Override
    public void onAttach(final ISpendDialog view) {
        super.onAttach(view);
        createOrder();
        loadInfo();
		ConfirmPurchasePageViewed.fire(amount.doubleValue(), offer.getId(), getOrderID());
    }

    private void createOrder() {
		SpendOrderCreationRequested.fire(offer.getId());
        orderRepository.createOrder(offer.getId(), new KinCallback<OpenOrder>() {
            @Override
            public void onResponse(OpenOrder response) {
                openOrder = response;
				SpendOrderCreationReceived.fire(offer.getId(), response != null ? response.getId() : null);
            }

            @Override
            public void onFailure(KinEcosystemException exception) {
                showToast("Oops something went wrong...");
				SpendOrderCreationFailed.fire(exception.getCause().getMessage(), offer.getId(), null);
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
        CloseButtonOnOfferPageTapped.fire(offer.getId(), getOrderID());
        closeDialog();
    }

	@Override
    public void bottomButtonClicked() {
		ConfirmPurchaseButtonTapped.fire(amount.doubleValue(), offer.getId(), getOrderID());
        if (view != null) {

            if (openOrder != null) {
                final String addressee = offer.getBlockchainData().getRecipientAddress();
                final String orderID = openOrder.getId();


                submitOrder(offer.getId(), orderID);
                sendTransaction(addressee, amount, orderID);
            }

            Confirmation confirmation = offerInfo.getConfirmation();
            view.showThankYouLayout(confirmation.getTitle(), confirmation.getDescription());
			SpendThankyouPageViewed.fire(amount.doubleValue(), offer.getId(), getOrderID());
			closeDialogWithDelay(CLOSE_DELAY);
        }
    }

    @Override
    public void dialogDismissed() {
        if (isOrderSubmitted) {
            orderRepository.isFirstSpendOrder(new KinCallback<Boolean>() {
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
                public void onFailure(KinEcosystemException exception) {

                }
            });
        }
        else {
        	if(openOrder != null) {
        		final String offerId = offer.getId();
        		final String orderId = openOrder.getId();
				SpendOrderCancelled.fire(offerId, orderId);
				orderRepository.cancelOrder(offerId, orderId, null);
			}
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
		SpendTransactionBroadcastToBlockchainSubmitted.fire(offer.getId(), orderID);
		blockchainSource.sendTransaction(addressee, amount, orderID);
    }

    private void submitOrder(String offerID, String orderID) {
        isOrderSubmitted = true;
		SpendOrderCompletionSubmitted.fire(offerID, orderID);
		orderRepository.submitOrder(offerID, null, orderID, new KinCallback<Order>() {
            @Override
            public void onResponse(Order response) {
                Log.i(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(KinEcosystemException exception) {
                //TODO handle failure
                Log.i(TAG, "onFailure: " + exception.getMessage());
            }
        });
    }

    private void showToast(String msg) {
        if (view != null) {
            view.showToast(msg);
        }
    }

	private String getOrderID() {
		return openOrder != null ? openOrder.getId() : "null";
	}
}
