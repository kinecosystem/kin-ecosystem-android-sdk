package com.kin.ecosystem.poll.presenter;

import android.support.annotation.NonNull;
import com.kin.ecosystem.KinCallback;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.bi.EventLogger;
import com.kin.ecosystem.bi.events.CloseButtonOnOfferPageTapped;
import com.kin.ecosystem.bi.events.EarnOrderCancelled;
import com.kin.ecosystem.bi.events.EarnOrderCompleted;
import com.kin.ecosystem.bi.events.EarnOrderCompleted.OfferType;
import com.kin.ecosystem.bi.events.EarnOrderCompletionSubmitted;
import com.kin.ecosystem.bi.events.EarnOrderCreationFailed;
import com.kin.ecosystem.bi.events.EarnOrderCreationReceived;
import com.kin.ecosystem.bi.events.EarnOrderCreationRequested;
import com.kin.ecosystem.bi.events.EarnOrderFailed;
import com.kin.ecosystem.bi.events.EarnPageLoaded;
import com.kin.ecosystem.data.order.OrderDataSource;
import com.kin.ecosystem.exception.KinEcosystemException;
import com.kin.ecosystem.network.model.OpenOrder;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.poll.view.IPollWebView;


public class PollWebViewPresenter extends BasePresenter<IPollWebView> implements IPollWebViewPresenter {

	private final OrderDataSource orderRepository;
	private final EventLogger eventLogger;

	private final String pollJsonString;
	private final String offerID;
	private final String contentType;
	private final int amount;
	private final String title;

	private Observer<OpenOrder> openOrderObserver;
	private OpenOrder openOrder;
	private boolean isOrderSubmitted = false;

	public PollWebViewPresenter(@NonNull final String pollJsonString, @NonNull final String offerID,
		@NonNull final String contentType, final int amount,
		String title, @NonNull final OrderDataSource orderRepository, @NonNull EventLogger eventLogger) {
		this.pollJsonString = pollJsonString;
		this.offerID = offerID;
		this.contentType = contentType;
		this.amount = amount;
		this.title = title;
		this.orderRepository = orderRepository;
		this.eventLogger = eventLogger;
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
		try {
			eventLogger.send(EarnOrderCreationRequested
				.create(EarnOrderCreationRequested.OfferType.fromValue(contentType), (double) amount, offerID));
		} catch (IllegalArgumentException ex) {
			//TODO: add general error event
		}
		orderRepository.createOrder(offerID, new KinCallback<OpenOrder>() {
			@Override
			public void onResponse(OpenOrder response) {
				eventLogger.send(EarnOrderCreationReceived.create(offerID, response != null ? response.getId() : null));
				// we are listening to open orders.
			}

			@Override
			public void onFailure(KinEcosystemException exception) {
				eventLogger.send(EarnOrderCreationFailed.create(exception.getCause().getMessage(), offerID));
				if (view != null) {
					showToast(exception.getMessage());
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
		try {
			eventLogger.send(EarnPageLoaded.create(EarnPageLoaded.OfferType.fromValue(contentType)));

		} catch (IllegalArgumentException ex) {
			//TODO: add general error event
		}
		if (view != null) {
			view.renderJson(pollJsonString);
		}
	}


	@Override
	public void closeClicked() {
		eventLogger.send(CloseButtonOnOfferPageTapped.create(offerID, getOrderId()));
		cancelOrderAndClose();
	}

	@Override
	public void onPageCancel() {
		cancelOrderAndClose();
	}

	private void cancelOrderAndClose() {
		if (openOrder != null && !isOrderSubmitted) {
			String orderID = openOrder.getId();
			orderRepository.cancelOrder(offerID, orderID, null);
			eventLogger.send(EarnOrderCancelled.create(offerID, orderID));
		}
		closeView();
	}

	@Override
	public void onPageResult(String result) {
		sendEarnOrderCompleted();
		if (openOrder != null) {
			isOrderSubmitted = true;
			final String orderId = openOrder.getId();
			eventLogger.send(EarnOrderCompletionSubmitted.create(offerID, orderId));
			orderRepository.submitOrder(offerID, result, orderId, new KinCallback<Order>() {
				@Override
				public void onResponse(Order response) {

				}

				@Override
				public void onFailure(KinEcosystemException exception) {
					EarnOrderFailed.create(exception.getCause().getMessage(), offerID, orderId);
					showToast("Order submission failed");
				}
			});
		}
	}

	private void sendEarnOrderCompleted() {
		try {
			eventLogger.send(
				EarnOrderCompleted.create(OfferType.fromValue(contentType), (double) amount, offerID, getOrderId()));
		} catch (IllegalArgumentException e) {
			//TODO: add general error event
		}
	}

	private String getOrderId() {
		return openOrder != null ? openOrder.getId() : "null";
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
