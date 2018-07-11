package com.kin.ecosystem.marketplace.presenter;

import android.os.Handler;
import android.util.Log;
import com.kin.ecosystem.KinCallback;
import com.kin.ecosystem.base.BaseDialogPresenter;
import com.kin.ecosystem.bi.EventLogger;
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
    private final EventLogger eventLogger;

    private final Handler handler = new Handler();

    private final OfferInfo offerInfo;
    private final Offer offer;
    private OpenOrder openOrder;

	private final BigDecimal amount;

    private boolean isOrderSubmitted;

    private static final int CLOSE_DELAY = 2000;

    public SpendDialogPresenter(OfferInfo offerInfo, Offer offer, BlockchainSource blockchainSource,
        OrderDataSource orderRepository, EventLogger eventLogger) {
        this.offerInfo = offerInfo;
        this.offer = offer;
        this.orderRepository = orderRepository;
        this.blockchainSource = blockchainSource;
        this.eventLogger = eventLogger;
        this.amount = new BigDecimal(offer.getAmount());
    }

    @Override
    public void onAttach(final ISpendDialog view) {
        super.onAttach(view);
		eventLogger.send(ConfirmPurchasePageViewed.create(amount.doubleValue(), offer.getId(), getOrderID()));
        createOrder();
        loadInfo();
    }

    private void createOrder() {
		eventLogger.send(SpendOrderCreationRequested.create(offer.getId(), false));
        orderRepository.createOrder(offer.getId(), new KinCallback<OpenOrder>() {
            @Override
            public void onResponse(OpenOrder response) {
                openOrder = response;
				eventLogger.send(SpendOrderCreationReceived.create(offer.getId(), response != null ? response.getId() : null,false));
            }

            @Override
            public void onFailure(KinEcosystemException exception) {
                showToast("Oops something went wrong...");
				eventLogger.send(SpendOrderCreationFailed.create(exception.getCause().getMessage(), offer.getId(), false));
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
        eventLogger.send(CloseButtonOnOfferPageTapped.create(offer.getId(), getOrderID()));
        closeDialog();
    }

	@Override
    public void bottomButtonClicked() {
		eventLogger.send(ConfirmPurchaseButtonTapped.create(amount.doubleValue(), offer.getId(), getOrderID()));
        if (view != null) {

            if (openOrder != null) {
                final String addressee = offer.getBlockchainData().getRecipientAddress();
                final String orderID = openOrder.getId();


                submitOrder(offer.getId(), orderID);
                sendTransaction(addressee, amount, orderID);
            }

            Confirmation confirmation = offerInfo.getConfirmation();
            view.showThankYouLayout(confirmation.getTitle(), confirmation.getDescription());
			eventLogger.send(SpendThankyouPageViewed.create(amount.doubleValue(), offer.getId(), getOrderID()));
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
				eventLogger.send(SpendOrderCancelled.create(offerId, orderId));
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
		blockchainSource.sendTransaction(addressee, amount, orderID, offer.getId());
    }

    private void submitOrder(String offerID, String orderID) {
        isOrderSubmitted = true;
		eventLogger.send(SpendOrderCompletionSubmitted.create(offerID, orderID, false));
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
