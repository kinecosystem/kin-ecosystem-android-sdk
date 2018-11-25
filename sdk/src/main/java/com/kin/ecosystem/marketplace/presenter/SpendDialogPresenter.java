package com.kin.ecosystem.marketplace.presenter;

import static com.kin.ecosystem.marketplace.view.ISpendDialog.SOMETHING_WENT_WRONG;

import android.os.Handler;
import com.kin.ecosystem.base.BaseDialogPresenter;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.KinCallbackAdapter;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.bi.events.CloseButtonOnOfferPageTapped;
import com.kin.ecosystem.core.bi.events.ConfirmPurchaseButtonTapped;
import com.kin.ecosystem.core.bi.events.ConfirmPurchasePageViewed;
import com.kin.ecosystem.core.bi.events.SpendOrderCancelled;
import com.kin.ecosystem.core.bi.events.SpendOrderCompletionSubmitted;
import com.kin.ecosystem.core.bi.events.SpendOrderCompletionSubmitted.Origin;
import com.kin.ecosystem.core.bi.events.SpendOrderCreationFailed;
import com.kin.ecosystem.core.bi.events.SpendOrderCreationReceived;
import com.kin.ecosystem.core.bi.events.SpendOrderCreationRequested;
import com.kin.ecosystem.core.bi.events.SpendThankyouPageViewed;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.order.OrderDataSource;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.Offer;
import com.kin.ecosystem.core.network.model.OfferInfo;
import com.kin.ecosystem.core.network.model.OfferInfo.Confirmation;
import com.kin.ecosystem.core.network.model.OpenOrder;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.marketplace.view.ISpendDialog;
import com.kin.ecosystem.marketplace.view.ISpendDialog.Message;
import java.math.BigDecimal;


class SpendDialogPresenter extends BaseDialogPresenter<ISpendDialog> implements ISpendDialogPresenter {

	private static final String TAG = SpendDialogPresenter.class.getSimpleName();

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
		eventLogger.send(
			SpendOrderCreationRequested.create(offer.getId(), false, SpendOrderCreationRequested.Origin.MARKETPLACE));
		orderRepository.createOrder(offer.getId(), new KinCallback<OpenOrder>() {
			@Override
			public void onResponse(OpenOrder response) {
				openOrder = response;
				eventLogger.send(SpendOrderCreationReceived
					.create(offer.getId(), response != null ? response.getId() : null, false,
						SpendOrderCreationReceived.Origin.MARKETPLACE));
				if (isUserConfirmedPurchase && !isSubmitted) {
					submitAndSendTransaction();
				}
			}

			@Override
			public void onFailure(KinEcosystemException exception) {
				showToast(SOMETHING_WENT_WRONG);
				try {
					String errorMsg = exception.getMessage();
					eventLogger.send(SpendOrderCreationFailed
						.create(errorMsg, offer.getId(), false, SpendOrderCreationFailed.Origin.MARKETPLACE));
				} catch (Throwable e) {
					eventLogger.send(SpendOrderCreationFailed.create(exception.getMessage(), offer.getId(), false,
						SpendOrderCreationFailed.Origin.MARKETPLACE));
				}
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
			final String orderID = openOrder.getId();
			submitOrder(offer.getId(), orderID);
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

	private void submitOrder(String offerID, final String orderID) {
		eventLogger.send(SpendOrderCompletionSubmitted.create(offerID, orderID, false, Origin.MARKETPLACE));
		orderRepository.submitOrder(offerID, null, orderID, new KinCallback<Order>() {
			@Override
			public void onResponse(Order response) {
				final String addressee = offer.getBlockchainData().getRecipientAddress();
				sendTransaction(addressee, amount, orderID);
				Logger.log(new Log().withTag(TAG).put(" Submit onResponse", response));
			}

			@Override
			public void onFailure(KinEcosystemException exception) {
				showToast(SOMETHING_WENT_WRONG);
				Logger.log(new Log().withTag(TAG).put(" Submit onFailure", exception));
			}
		});
	}

	private void showToast(@Message final int msg) {
		if (view != null) {
			view.showToast(msg);
		}
	}

	private String getOrderID() {
		return openOrder != null ? openOrder.getId() : "null";
	}
}
