package com.kin.ecosystem.marketplace.presenter;


import static com.kin.ecosystem.marketplace.view.IMarketplaceView.NOT_ENOUGH_KIN;
import static com.kin.ecosystem.marketplace.view.IMarketplaceView.SOMETHING_WENT_WRONG;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.NativeOfferClickEvent;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.common.model.NativeOffer;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.bi.events.BackButtonOnMarketplacePageTapped;
import com.kin.ecosystem.core.bi.events.EarnOfferTapped;
import com.kin.ecosystem.core.bi.events.MarketplacePageViewed;
import com.kin.ecosystem.core.bi.events.NotEnoughKinPageViewed;
import com.kin.ecosystem.core.bi.events.SpendOfferTapped;
import com.kin.ecosystem.core.bi.events.SpendOfferTapped.Origin;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.offer.OfferDataSource;
import com.kin.ecosystem.core.data.offer.OfferListUtil;
import com.kin.ecosystem.core.data.order.OrderDataSource;
import com.kin.ecosystem.core.network.model.Offer;
import com.kin.ecosystem.core.network.model.Offer.ContentTypeEnum;
import com.kin.ecosystem.core.network.model.Offer.OfferType;
import com.kin.ecosystem.core.network.model.OfferInfo;
import com.kin.ecosystem.core.network.model.OfferList;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.core.util.OfferConverter;
import com.kin.ecosystem.main.INavigator;
import com.kin.ecosystem.marketplace.view.IMarketplaceView;
import com.kin.ecosystem.marketplace.view.IMarketplaceView.Message;
import com.kin.ecosystem.poll.view.PollWebViewActivity.PollBundle;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MarketplacePresenter extends BasePresenter<IMarketplaceView> implements IMarketplacePresenter {

	private static final int NOT_FOUND = -1;
	private static final long CLICK_TIME_INTERVAL = 350;

	private final OfferDataSource offerRepository;
	private final OrderDataSource orderRepository;
	private final BlockchainSource blockchainSource;
	private final INavigator navigator;
	private final EventLogger eventLogger;

	private List<Offer> spendList;
	private List<Offer> earnList;

	private Observer<Order> orderObserver;


	private long lastClickTime = NOT_FOUND;
	private final Gson gson;

	public MarketplacePresenter(@NonNull final IMarketplaceView view, @NonNull final OfferDataSource offerRepository,
		@NonNull final OrderDataSource orderRepository, @Nullable final BlockchainSource blockchainSource,
		@NonNull INavigator navigator, @NonNull EventLogger eventLogger) {
		this.view = view;
		this.offerRepository = offerRepository;
		this.orderRepository = orderRepository;
		this.blockchainSource = blockchainSource;
		this.navigator = navigator;
		this.eventLogger = eventLogger;
		this.gson = new Gson();

		this.view.attachPresenter(this);
	}

	@Override
	public void onAttach(IMarketplaceView view) {
		super.onAttach(view);
		getCachedOffers();
		getOffers();
		listenToOrders();
		eventLogger.send(MarketplacePageViewed.create());
	}

	private void getCachedOffers() {
		OfferList cachedOfferList = offerRepository.getCachedOfferList();
		setCachedOfferLists(cachedOfferList);
	}

	private boolean hasOffers(OfferList offerList) {
		return offerList != null && offerList.getOffers() != null;
	}

	private void setCachedOfferLists(OfferList cachedOfferList) {
		if (earnList == null && spendList == null) {
			earnList = new ArrayList<>();
			spendList = new ArrayList<>();
		}

		if (hasOffers(cachedOfferList)) {
			OfferListUtil.splitOffersByType(cachedOfferList.getOffers(), earnList, spendList);
		}

		if (this.view != null) {
			this.view.setEarnList(earnList);
			this.view.setSpendList(spendList);

			if (!earnList.isEmpty()) {
				updateEarnTitle();
			}
			if (!spendList.isEmpty()) {
				updateSpendTitle();
			}
		}
	}

	private void listenToOrders() {
		orderObserver = new Observer<Order>() {
			@Override
			public void onChanged(Order order) {
				switch (order.getStatus()) {
					case PENDING:
						removeOfferFromList(order.getOfferId(), order.getOfferType());
						break;
					case FAILED:
					case COMPLETED:
						getOffers();
						break;
				}

			}
		};
		orderRepository.addOrderObserver(orderObserver);
	}

	private void removeOfferFromList(String offerId, OfferType offerType) {
		if (offerType == OfferType.EARN) {
			for (int i = 0; i < earnList.size(); i++) {
				Offer offer = earnList.get(i);
				if (offer.getId().equals(offerId)) {
					earnList.remove(i);
					notifyEarnItemRemoved(i);
					updateEarnTitle();
					return;
				}
			}
		} else {
			for (int i = 0; i < spendList.size(); i++) {
				Offer offer = spendList.get(i);
				if (offer.getId().equals(offerId)) {
					spendList.remove(i);
					notifySpendItemRemoved(i);
					updateSpendTitle();
					return;
				}
			}
		}
	}

	private void updateEarnTitle() {
		boolean isEarnListEmpty = earnList.isEmpty();
		if (view != null) {
			view.updateEarnSubtitle(isEarnListEmpty);
		}
	}

	private void updateSpendTitle() {
		boolean isSpendListEmpty = spendList.isEmpty();
		if (view != null) {
			view.updateSpendSubtitle(isSpendListEmpty);
		}
	}

	private void notifyEarnItemRemoved(int index) {
		if (view != null) {
			view.notifyEarnItemRemoved(index);
		}
	}

	private void notifyEarnItemInserted(int index) {
		if (view != null) {
			view.notifyEarnItemInserted(index);
		}
	}

	private void notifySpendItemRemoved(int index) {
		if (view != null) {
			view.notifySpendItemRemoved(index);
		}
	}

	private void notifySpendItemInserted(int index) {
		if (view != null) {
			view.notifySpendItemInserted(index);
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		release();
	}

	private void release() {
		earnList = null;
		spendList = null;
		orderRepository.removeOrderObserver(orderObserver);
	}

	@Override
	public void getOffers() {
		this.offerRepository.getOffers(new KinCallback<OfferList>() {
			@Override
			public void onResponse(OfferList offerList) {
				setupEmptyItemView();
				syncOffers(offerList);
			}

			@Override
			public void onFailure(KinEcosystemException exception) {
				setupEmptyItemView();
				updateEarnTitle();
				updateSpendTitle();
			}
		});
	}

	private void setupEmptyItemView() {
		if (view != null) {
			view.setupEmptyItemView();
		}
	}

	private void syncOffers(OfferList offerList) {
		if (hasOffers(offerList)) {
			List<Offer> newEarnOffers = new ArrayList<>();
			List<Offer> newSpendOffers = new ArrayList<>();

			OfferListUtil.splitOffersByType(offerList.getOffers(), newEarnOffers, newSpendOffers);
			syncList(newEarnOffers, earnList, OfferType.EARN);
			syncList(newSpendOffers, spendList, OfferType.SPEND);
		}
	}

	private void syncList(List<Offer> newList, List<Offer> oldList, OfferType offerType) {
		// check if offer should be removed (index changed / removed from list).
		if (newList.size() > 0) {
			for (int i = 0; i < oldList.size(); i++) {
				Offer offer = oldList.get(i);
				int index = newList.indexOf(offer);
				if (index == NOT_FOUND || index != i) {
					oldList.remove(i);
					notifyItemRemoved(i, offerType);
				}
			}

			// Add missing offers, the order matters
			for (int i = 0; i < newList.size(); i++) {
				Offer offer = newList.get(i);
				if (i < oldList.size()) {
					if (!oldList.get(i).equals(offer)) {
						oldList.add(i, offer);
						notifyItemInserted(i, offerType);
					}
				} else {
					oldList.add(offer);
					notifyItemInserted(i, offerType);
				}
			}
		} else {
			final int size = oldList.size();
			if (size > 0) {
				oldList.clear();
				notifyItemRangRemoved(0, size, offerType);
			}
		}

		if (offerType == OfferType.EARN) {
			updateEarnTitle();
		} else {
			updateSpendTitle();
		}
	}

	private void notifyItemRangRemoved(int fromIndex, int size, OfferType offerType) {
		if (view != null) {
			if (isSpend(offerType)) {
				view.notifySpendItemRangRemoved(fromIndex, size);
			} else {
				view.notifyEarnItemRangRemoved(fromIndex, size);
			}
		}
	}

	private void notifyItemRemoved(int index, OfferType offerType) {
		if (isSpend(offerType)) {
			notifySpendItemRemoved(index);
		} else {
			notifyEarnItemRemoved(index);
		}
	}

	private void notifyItemInserted(int index, OfferType offerType) {
		if (isSpend(offerType)) {
			notifySpendItemInserted(index);
			updateSpendTitle();
		} else {
			notifyEarnItemInserted(index);
			updateEarnTitle();
		}
	}

	private boolean isSpend(OfferType offerType) {
		return offerType == OfferType.SPEND;
	}

	@Override
	public void onItemClicked(int position, OfferType offerType) {
		if (isFastClicks()) {
			return;
		}

		if (position == NOT_FOUND) {
			return;
		}

		final Offer offer;
		if (offerType == OfferType.EARN) {
			offer = earnList.get(position);
			sendEranOfferTapped(offer);
			if (onExternalItemClicked(offer)) {
				return;
			}
			if (this.view != null) {
				PollBundle pollBundle = new PollBundle()
					.setJsonData(offer.getContent())
					.setOfferID(offer.getId())
					.setContentType(offer.getContentType().getValue())
					.setAmount(offer.getAmount())
					.setTitle(offer.getTitle());
				this.view.showOfferActivity(pollBundle);
			}
		} else {
			offer = spendList.get(position);
			sendSpendOfferTapped(offer);
			if (onExternalItemClicked(offer)) {
				return;
			}
			int balance = blockchainSource.getBalance().getAmount().intValue();
			final BigDecimal amount = new BigDecimal(offer.getAmount());

			if (balance < amount.intValue()) {
				eventLogger.send(NotEnoughKinPageViewed.create());
				showToast(NOT_ENOUGH_KIN);
				return;
			}

			OfferInfo offerInfo = deserializeOfferInfo(offer.getContent());
			if (offerInfo != null) {
				showSpendDialog(offerInfo, offer);
			} else {
				showSomethingWentWrong();
			}
		}
	}

	private boolean isFastClicks() {
		if(lastClickTime == NOT_FOUND) {
			return false;
		}

		long now = System.currentTimeMillis();
		if (now - lastClickTime < CLICK_TIME_INTERVAL) {
			return true;
		}
		lastClickTime = now;
		return false;
	}

	private boolean onExternalItemClicked(Offer offer) {
		if (offer != null && offer.getContentType() == ContentTypeEnum.EXTERNAL) {
			final boolean dismissOnTap = offerRepository.shouldDismissOnTap(offer.getId());
			if (dismissOnTap) {
				closeMarketplace();
			}
			nativeSpendOfferClicked(offer, dismissOnTap);
			return true;
		}
		return false;
	}

	private void closeMarketplace() {
		navigator.close();
	}

	private void sendEranOfferTapped(Offer offer) {
		EarnOfferTapped.OfferType offerType;
		try {
			offerType = EarnOfferTapped.OfferType.fromValue(offer.getContentType().getValue());
			double amount = (double) offer.getAmount();
			eventLogger.send(EarnOfferTapped.create(offerType, amount, offer.getId()));
		} catch (IllegalArgumentException | NullPointerException ex) {
			//TODO: add general error event
		}
	}

	private void sendSpendOfferTapped(Offer offer) {
		double amount = (double) offer.getAmount();
		ContentTypeEnum contentType = offer.getContentType();
		eventLogger.send(SpendOfferTapped.create(amount, offer.getId(),
			contentType == ContentTypeEnum.EXTERNAL ? Origin.EXTERNAL : Origin.MARKETPLACE));
	}

	private void nativeSpendOfferClicked(Offer offer, boolean dismissMarketplace) {
		NativeOffer nativeOffer = OfferConverter.toNativeOffer(offer);
		offerRepository.getNativeSpendOfferObservable().postValue(
			new NativeOfferClickEvent.Builder()
				.nativeOffer(nativeOffer)
				.isDismissed(dismissMarketplace)
				.build());
	}

	private void showSomethingWentWrong() {
		showToast(SOMETHING_WENT_WRONG);
	}

	@Override
	public void showOfferActivityFailed() {
		showSomethingWentWrong();
	}

	@Override
	public void backButtonPressed() {
		eventLogger.send(BackButtonOnMarketplacePageTapped.create());
	}

	@Override
	public INavigator getNavigator() {
		return navigator;
	}

	private void showSpendDialog(@NonNull final OfferInfo offerInfo, @NonNull final Offer offer) {
		if (this.view != null) {
			this.view.showSpendDialog(createSpendDialogPresenter(offerInfo, offer));
		}
	}

	private ISpendDialogPresenter createSpendDialogPresenter(@NonNull final OfferInfo offerInfo,
		@NonNull final Offer offer) {
		return new SpendDialogPresenter(offerInfo, offer, blockchainSource, orderRepository, eventLogger);
	}

	private OfferInfo deserializeOfferInfo(final String content) {
		try {
			return gson.fromJson(content, OfferInfo.class);
		} catch (JsonSyntaxException t) {
			return null;
		}
	}

	private void showToast(@Message final int msg) {
		if (view != null) {
			view.showToast(msg);
		}
	}
}
