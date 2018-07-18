package com.kin.ecosystem.marketplace.presenter;

import android.os.Handler;
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
import com.kin.ecosystem.data.KinCallbackAdapter;
import com.kin.ecosystem.data.blockchain.BlockchainSource;
import com.kin.ecosystem.data.order.OrderDataSource;
import com.kin.ecosystem.exception.KinEcosystemException;
import com.kin.ecosystem.marketplace.view.ISpendDialog;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.OfferInfo;
import com.kin.ecosystem.network.model.OfferInfo.Confirmation;
import com.kin.ecosystem.network.model.OpenOrder;
import java.math.BigDecimal;


public class SpendDialogPresenter extends BaseDialogPresenter<ISpendDialog> implements ISpendDialogPresenter {

	private final OrderDataSource orderRepository;
	private final BlockchainSource blockchainSource;
	private final EventLogger eventLogger;

	private final Handler handler = new Handler();

	private final OfferInfo offerInfo;
	private final Offer offer;
	private OpenOrder openOrder;

	private final BigDecimal amount;

	private boolean isUserConfirmedPurchase;
	private boolean isSubmitted;

	private static final int CLOSE_DELAY = 2000;

	SpendDialogPresenter(OfferInfo offerInfo, Offer offer, BlockchainSource blockchainSource,
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
		createOrder();
		loadInfo();
		eventLogger.send(ConfirmPurchasePageViewed.create(amount.doubleValue(), offer.getId(), getOrderID()));
	}

	private void createOrder() {
		eventLogger.send(SpendOrderCreationRequested.create(offer.getId(), false));
		orderRepository.createOrder(offer.getId(), new KinCallback<OpenOrder>() {
			@Override
			public void onResponse(OpenOrder response) {
				openOrder = response;
				eventLogger.send(SpendOrderCreationReceived
					.create(offer.getId(), response != null ? response.getId() : null, false));
				if (isUserConfirmedPurchase && !isSubmitted) {
					submitAndSendTransaction();
				}
			}

			@Override
			public void onFailure(KinEcosystemException exception) {
				showToast("Oops something went wrong...");
				eventLogger
					.send(SpendOrderCreationFailed.create(exception.getCause().getMessage(), offer.getId(), false));
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
		isUserConfirmedPurchase = true;
		eventLogger.send(ConfirmPurchaseButtonTapped.create(amount.doubleValue(), offer.getId(), getOrderID()));
		if (view != null) {
			Confirmation confirmation = offerInfo.getConfirmation();
			view.showThankYouLayout(confirmation.getTitle(), confirmation.getDescription());
			eventLogger.send(SpendThankyouPageViewed.create(amount.doubleValue(), offer.getId(), getOrderID()));
			closeDialogWithDelay(CLOSE_DELAY);
		}

		submitAndSendTransaction();
	}

	private void submitAndSendTransaction() {
		if (openOrder != null) {
			isSubmitted = true;
			final String addressee = offer.getBlockchainData().getRecipientAddress();
			final String orderID = openOrder.getId();

			submitOrder(offer.getId(), orderID);
			sendTransaction(addressee, amount, orderID);
		}
	}

	@Override
	public void dialogDismissed() {
		if (isUserConfirmedPurchase) {
			orderRepository.isFirstSpendOrder(new KinCallbackAdapter<Boolean>() {
				@Override
				public void onResponse(Boolean response) {
					if (response) {
						navigateToOrderHistory();
						orderRepository.setIsFirstSpendOrder(false);
					}
					onDetach();
				}
			});
		} else {
			if (openOrder != null) {
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
		eventLogger.send(SpendOrderCompletionSubmitted.create(offerID, orderID, false));
		orderRepository.submitOrder(offerID, null, orderID, null);
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
