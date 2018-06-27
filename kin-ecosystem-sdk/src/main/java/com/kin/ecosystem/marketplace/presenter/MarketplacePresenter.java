package com.kin.ecosystem.marketplace.presenter;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kin.ecosystem.KinCallback;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.bi.EventLogger;
import com.kin.ecosystem.bi.events.BackButtonOnMarketplacePageTapped;
import com.kin.ecosystem.bi.events.BalanceTapped;
import com.kin.ecosystem.bi.events.EarnOfferTapped;
import com.kin.ecosystem.bi.events.MarketplacePageViewed;
import com.kin.ecosystem.bi.events.NotEnoughKinPageViewed;
import com.kin.ecosystem.bi.events.SpendOfferTapped;
import com.kin.ecosystem.data.blockchain.BlockchainSource;
import com.kin.ecosystem.data.offer.OfferDataSource;
import com.kin.ecosystem.data.order.OrderDataSource;
import com.kin.ecosystem.exception.KinEcosystemException;
import com.kin.ecosystem.marketplace.model.NativeSpendOffer;
import com.kin.ecosystem.marketplace.view.IMarketplaceView;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.Offer.ContentTypeEnum;
import com.kin.ecosystem.network.model.Offer.OfferType;
import com.kin.ecosystem.network.model.OfferInfo;
import com.kin.ecosystem.network.model.OfferList;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.poll.view.PollWebViewActivity.PollBundle;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MarketplacePresenter extends BasePresenter<IMarketplaceView> implements IMarketplacePresenter {

	private static final String TAG = "MarketplacePresenter";
	private static final int NOT_FOUND = -1;

	private static final String EMPTY_SUBTITLE = "Check back tomorrow for more great opportunities";
	private static final String EARN_SUBTITLE = "Complete tasks and earn KIN";
	private static final String SPEND_SUBTITLE = "Use your KIN to enjoy stuff you like";

	private final OfferDataSource offerRepository;
	private final OrderDataSource orderRepository;
	private final BlockchainSource blockchainSource;
	private final EventLogger eventLogger;

	private List<Offer> spendList = new ArrayList<>();
	private List<Offer> earnList = new ArrayList<>();

	private Observer<Offer> pendingOfferObserver;
	private Observer<Order> completedOrderObserver;

	private ObservableData<Boolean> isEarnEmpty = ObservableData.create();
	private ObservableData<Boolean> isSpendEmpty = ObservableData.create();
	private Observer<Boolean> isEarnEmptyObserver;
	private Observer<Boolean> isSpendEmptyObserver;

	private final Gson gson;

	public MarketplacePresenter(@NonNull final OfferDataSource offerRepository,
		@NonNull final OrderDataSource orderRepository, @Nullable final BlockchainSource blockchainSource, @NonNull
		EventLogger eventLogger) {
		this.spendList = new ArrayList<>();
		this.earnList = new ArrayList<>();
		this.offerRepository = offerRepository;
		this.orderRepository = orderRepository;
		this.blockchainSource = blockchainSource;
		this.eventLogger = eventLogger;
		this.gson = new Gson();
	}

	@Override
	public void onAttach(IMarketplaceView view) {
		super.onAttach(view);
		isEarnEmpty.addObserver(getEarnEmptyObserver());
		isSpendEmpty.addObserver(getSpendEmptyObserver());
		getCachedOffers();
		getOffers();
		listenToPendingOffers();
		listenToCompletedOrders();
		eventLogger.send(MarketplacePageViewed.create());
	}

	private Observer<Boolean> getEarnEmptyObserver() {
		if (isEarnEmptyObserver == null) {
			isEarnEmptyObserver = new Observer<Boolean>() {
				@Override
				public void onChanged(Boolean value) {
					final String subtitle = value ? EMPTY_SUBTITLE : EARN_SUBTITLE;
					if (view != null) {
						view.updateEarnSubtitle(subtitle);
						if (value) {
							Log.d(TAG, "EarnEmptyObserver onChanged:  setEarnEmptyView");
							view.setEarnEmptyView();
						}
					}
				}
			};
		}
		return isEarnEmptyObserver;

	}

	private Observer<Boolean> getSpendEmptyObserver() {
		if (isSpendEmptyObserver == null) {
			isSpendEmptyObserver = new Observer<Boolean>() {
				@Override
				public void onChanged(Boolean value) {
					final String subtitle = value ? EMPTY_SUBTITLE : SPEND_SUBTITLE;
					if (view != null) {
						view.updateSpendSubtitle(subtitle);
						if (value) {
							Log.d(TAG, "SpendEmptyObserver onChanged:  setSpendEmptyView");
							view.setSpendEmptyView();
						}
					}
				}
			};
		}
		return isSpendEmptyObserver;
	}

	private void getCachedOffers() {
		OfferList cachedOfferList = offerRepository.getCachedOfferList();
		setOfferList(cachedOfferList);
	}

	private void listenToCompletedOrders() {
		completedOrderObserver = new Observer<Order>() {
			@Override
			public void onChanged(Order order) {
				getOffers();
			}
		};
		orderRepository.addCompletedOrderObserver(completedOrderObserver);
	}

	private void listenToPendingOffers() {
		pendingOfferObserver = new Observer<Offer>() {
			@Override
			public void onChanged(Offer offer) {
				if (offer != null) {
					removeOfferFromList(offer);
				}
			}
		};
		offerRepository.getPendingOffer().addObserver(pendingOfferObserver);
	}

	private void removeOfferFromList(Offer offer) {
		int index;
		if (offer.getOfferType() == OfferType.EARN) {
			index = earnList.indexOf(offer);
			if (index != NOT_FOUND) {
				earnList.remove(index);
				notifyEarnItemRemoved(index);
				setEarnEmptyViewIfNeeded();
			}

		} else {
			index = spendList.indexOf(offer);
			if (index != NOT_FOUND) {
				spendList.remove(index);
				notifySpendItemRemoved(index);
				setSpendEmptyViewIfNeeded();
			}
		}
	}

	private void setEarnEmptyViewIfNeeded() {
		if (earnList.size() == 0) {
			isEarnEmpty.postValue(true);
		}
	}

	private void setSpendEmptyViewIfNeeded() {
		if (spendList.size() == 0) {
			isSpendEmpty.postValue(true);
		}
	}

	private void notifyEarnItemRemoved(int index) {
		if (view != null) {
			Log.d(TAG, "notifyEarnItemRemoved: " + getSafeIndexEarnEmptyState(index) + " isEmpty= " + isEarnEmpty.getValue());
			view.notifyEarnItemRemoved(index);
		}
	}

	private void notifyEarnItemInserted(int index) {
		if (view != null) {
			Log.d(TAG, "notifyEarnItemInserted: " + getSafeIndexEarnEmptyState(index) + " isEmpty= " + isEarnEmpty.getValue());
			view.notifyEarnItemInserted(getSafeIndexEarnEmptyState(index));
		}
	}

	private void notifySpendItemRemoved(int index) {
		if (view != null) {
			Log.d(TAG, "notifySpendItemRemoved: " + getSafeIndexEarnEmptyState(index) + " isEmpty= " + isSpendEmpty.getValue());
			view.notifySpendItemRemoved(getSafeIndexSpendEmptyState(index));
		}
	}

	private void notifySpendItemInserted(int index) {
		if (view != null) {
			Log.d(TAG, "notifySpendItemInserted: " + getSafeIndexSpendEmptyState(index) + " isEmpty= " + isSpendEmpty.getValue());
			view.notifySpendItemInserted(getSafeIndexSpendEmptyState(index));
		}
	}

	private int getSafeIndexEarnEmptyState(final int index) {
		return isEarnEmpty.getValue() ? 1 : index;
	}

	private int getSafeIndexSpendEmptyState(final int index) {
		return isSpendEmpty.getValue() ? 1 : index;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		release();
	}

	private void release() {
		isEarnEmpty.removeObserver(isEarnEmptyObserver);
		isSpendEmpty.removeObserver(isSpendEmptyObserver);
		offerRepository.getPendingOffer().removeObserver(pendingOfferObserver);
		orderRepository.removeCompletedOrderObserver(completedOrderObserver);
	}

	@Override
	public void getOffers() {
		Log.d(TAG, "getOffers");
		this.offerRepository.getOffers(new KinCallback<OfferList>() {
			@Override
			public void onResponse(OfferList offerList) {
				syncOffers(offerList);
			}

			@Override
			public void onFailure(KinEcosystemException exception) {
				//TODO show error msg
			}
		});
	}

	private void syncOffers(OfferList offerList) {
		if (offerList != null && offerList.getOffers() != null) {
			List<Offer> newEarnOffers = new ArrayList<>();
			List<Offer> newSpendOffers = new ArrayList<>();

			splitOffersByType(offerList.getOffers(), newEarnOffers, newSpendOffers);
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

		if (offerType == OfferType.EARN) {
			setEarnEmptyViewIfNeeded();
		} else {
			setSpendEmptyViewIfNeeded();
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
			if (isSpendEmpty.getValue() == null || isSpendEmpty.getValue()) {
				isSpendEmpty.postValue(false);
			}
		} else {
			notifyEarnItemInserted(index);
			if (isEarnEmpty.getValue() == null || isEarnEmpty.getValue()) {
				isEarnEmpty.postValue(false);
			}
		}
	}

	private boolean isSpend(OfferType offerType) {
		return offerType == OfferType.SPEND;
	}

	private void setOfferList(OfferList offerList) {
		if (offerList != null && offerList.getOffers() != null) {
			splitOffersByType(offerList.getOffers(), this.earnList, this.spendList);
		}

		if (this.view != null) {
			this.view.setEarnList(earnList);
			this.view.setSpendList(spendList);

			isEarnEmpty.postValue(earnList.size() == 0);
			isSpendEmpty.postValue(spendList.size() == 0);
		}
	}

	private void splitOffersByType(List<Offer> list, List<Offer> earnList, List<Offer> spendList) {
		for (Offer offer : list) {
			if (offer.getOfferType() == OfferType.EARN) {
				earnList.add(offer);
			} else {
				spendList.add(offer);
			}
		}
	}

	@Override
	public void onItemClicked(int position, OfferType offerType) {
		final Offer offer;
		if (offerType == OfferType.EARN) {
			offer = earnList.get(position);
			sendEranOfferTapped(offer);
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
			if (offer.getContentType() == ContentTypeEnum.EXTERNAL) {
				nativeSpendOfferClicked(offer);
				return;
			}
			int balance = blockchainSource.getBalance().getAmount().intValue();
			final BigDecimal amount = new BigDecimal(offer.getAmount());

			if (balance < amount.intValue()) {
				eventLogger.send(NotEnoughKinPageViewed.create());
				showToast("You don't have enough Kin");
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
		eventLogger.send(SpendOfferTapped.create(amount, offer.getId(), null));
	}

	private void nativeSpendOfferClicked(Offer offer) {
		offerRepository.getNativeSpendOfferObservable().postValue((NativeSpendOffer) offer);
	}

	private void showSomethingWentWrong() {
		if (view != null) {
			view.showSomethingWentWrong();
		}
	}

	@Override
	public void balanceItemClicked() {
		eventLogger.send(BalanceTapped.create());
		if (view != null) {
			view.navigateToOrderHistory();
		}
	}

	@Override
	public void showOfferActivityFailed() {
		showSomethingWentWrong();
	}

	@Override
	public void backButtonPressed() {
		eventLogger.send(BackButtonOnMarketplacePageTapped.create());
		if (view != null) {
			view.navigateBack();
		}
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

	private void showToast(String msg) {
		if (view != null) {
			view.showToast(msg);
		}
	}
}
