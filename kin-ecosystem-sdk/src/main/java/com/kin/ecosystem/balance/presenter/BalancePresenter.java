package com.kin.ecosystem.balance.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.kin.ecosystem.balance.view.IBalanceView;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.base.Observer;


import com.kin.ecosystem.data.blockchain.BlockchainSource;
import com.kin.ecosystem.data.model.Balance;
import com.kin.ecosystem.data.offer.OfferDataSource;
import com.kin.ecosystem.data.order.OrderDataSource;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.network.model.Order.Status;
import kin.ecosystem.core.util.StringUtil;

public class BalancePresenter extends BasePresenter<IBalanceView> implements IBasePresenter<IBalanceView> {

	private static final String TAG = "BalancePresenter";

	private static final String BALANCE_ZERO_TEXT = "0.00";
	private static final String EARN_PENDING_TEXT_FORMAT = "Thanks! %s Kin are on the way";
	private static final String EARN_APPROVED_TEXT_FORMAT = "Done! %s Kin earned";

	private static final String EARN_TIMEOUT_TEXT = "Sorry - this may take some time";
	private static final String FAILED_TEXT = "Oops! Something went wrong";


	private final BlockchainSource blockchainSource;
	private final OfferDataSource offerRepository;
	private final OrderDataSource orderRepository;


	private Observer<Balance> balanceObserver;
	private Observer<Offer> pendingOfferObserver;
	private Observer<Order> completedOrderObserver;

	private String currentPendingOfferID;

	public BalancePresenter(@Nullable final BlockchainSource blockchainSource,
		@NonNull final OfferDataSource offerRepository,
		@NonNull final OrderDataSource orderRepository) {
		this.blockchainSource = blockchainSource;
		this.offerRepository = offerRepository;
		this.orderRepository = orderRepository;
		createBalanceObserver();
		createPendingOfferObserver();
		createCompletedOrderObserver();
	}

	private void createBalanceObserver() {
		balanceObserver = new Observer<Balance>() {
			@Override
			public void onChanged(Balance balance) {
				updateBalance(balance);
			}
		};
	}

	private void updateBalance(Balance balance) {
		int balanceValue = balance.getAmount().intValue();
		String balanceString;
		if (balanceValue == 0) {
			balanceString = BALANCE_ZERO_TEXT;
		} else {
			balanceString = StringUtil.getAmountFormatted(balanceValue);
		}
		if (view != null) {
			view.updateBalance(balanceString);
		}
	}

	private void createPendingOfferObserver() {
		Offer offer  = offerRepository.getPendingOffer().getValue();
		updatePendingOffer(offer);
		Log.d(TAG, "createPendingOfferObserver: offerNull? = " + (offer == null));
		pendingOfferObserver = new Observer<Offer>() {
			@Override
			public void onChanged(Offer offer) {
				updatePendingOffer(offer);
			}
		};
	}

	private void updatePendingOffer(Offer offer) {
		if (offer != null) {
			currentPendingOfferID = offer.getId();
			String subTitle = generateSubTitleFromOffer(offer);
			updateSubTitle(subTitle, null);
		}
	}

	private void createCompletedOrderObserver() {
		completedOrderObserver = new Observer<Order>() {
			@Override
			public void onChanged(Order order) {
				if (order != null && currentPendingOfferID.equals(order.getOfferId())) {
					String subTitle = generateSubTitleFromOrder(order);
					updateSubTitle(subTitle, order.getStatus());
				}
			}
		};
	}

	private String generateSubTitleFromOffer(Offer offer) {
		String text = "";
		switch (offer.getOfferType()) {
			case EARN:
				text = String.format(EARN_PENDING_TEXT_FORMAT, StringUtil.getAmountFormatted(offer.getAmount()));
				break;
			case SPEND:
				break;
			default:
				break;
		}
		return text;
	}

	private String generateSubTitleFromOrder(Order order) {
		String text = "";
		if (order.getStatus() == Status.COMPLETED) {
			switch (order.getOfferType()) {
				case EARN:
					text = String.format(EARN_APPROVED_TEXT_FORMAT, StringUtil.getAmountFormatted(order.getAmount()));
					break;
				case SPEND:
					break;
				default:
					break;
			}
		} else {
			text = FAILED_TEXT;
		}
		return text;
	}

	private void updateSubTitle(String text, Status status) {
		if (view != null) {
			Log.d(TAG, "updateSubTitle: text= " + text + " Status= " + (status != null ? status.toString() : "null"));
			view.updateSubTitle(text, status);
		}
	}

	@Override
	public void onAttach(IBalanceView view) {
		super.onAttach(view);
		addObservers();
	}

	private void addObservers() {
		offerRepository.getPendingOffer().addObserver(pendingOfferObserver);
		orderRepository.addCompletedOrderObserver(completedOrderObserver);
		blockchainSource.addBalanceObserver(balanceObserver);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		removeObservers();
	}

	private void removeObservers() {
		offerRepository.getPendingOffer().removeObserver(pendingOfferObserver);
		orderRepository.removeCompletedOrderObserver(completedOrderObserver);
		blockchainSource.removeBalanceObserver(balanceObserver);
	}
}
