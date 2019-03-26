package com.kin.ecosystem.core.data.offer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.NativeOfferClickEvent;
import com.kin.ecosystem.common.ObservableData;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.Subscription;
import com.kin.ecosystem.common.model.NativeOffer;
import com.kin.ecosystem.core.data.order.OrderDataSource;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.Offer;
import com.kin.ecosystem.core.network.model.Offer.ContentTypeEnum;
import com.kin.ecosystem.core.network.model.Offer.OfferType;
import com.kin.ecosystem.core.network.model.OfferList;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.core.network.model.Order.Status;
import com.kin.ecosystem.core.util.ErrorUtil;
import com.kin.ecosystem.core.util.OfferConverter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import kotlin.Pair;

public class OfferRepository implements OfferDataSource {

	private static volatile OfferRepository instance = null;

	private final OfferDataSource.Remote remoteData;
	private final OrderDataSource orderRepository;

	//Saves offerId with a value dismissOnTap
	private HashMap<String, Boolean> nativeOfferMap = new HashMap<>();
	private OfferList nativeSpendOfferList = new OfferList();
	private OfferList nativeEarnOfferList = new OfferList();
	private OfferList cachedOfferList = new OfferList();

	private ObservableData<NativeOfferClickEvent> nativeSpendOfferObservable = ObservableData.create();
	private ObservableData<Offer> nativeOfferRemoved = ObservableData.create();

	private OfferRepository(@NonNull OfferDataSource.Remote remoteData, @NonNull OrderDataSource orderRepository) {
		this.remoteData = remoteData;
		this.orderRepository = orderRepository;
		listenToPendingOrders();
	}

	public static void init(@NonNull OfferDataSource.Remote remoteData, @NonNull OrderDataSource orderRepository) {
		if (instance == null) {
			synchronized (OfferRepository.class) {
				if (instance == null) {
					instance = new OfferRepository(remoteData, orderRepository);
				}
			}
		}
	}

	public static OfferRepository getInstance() {
		return instance;
	}

	private void listenToPendingOrders() {
		orderRepository.addOrderObserver(new Observer<Order>() {
			@Override
			public void onChanged(Order order) {
				if (order.getStatus() == Status.PENDING) {
					removeFromCachedOfferList(order.getOfferId());
				}
			}
		});
	}

	@Override
	public OfferList getCachedOfferList() {
		return getList();
	}

	@Override
	public void getOffers(@Nullable final KinCallback<OfferList> callback) {
		remoteData.getOffers(new Callback<OfferList, ApiException>() {
			@Override
			public void onResponse(OfferList response) {
				cachedOfferList = response;
				if (callback != null) {
					callback.onResponse(getList());
				}
			}

			@Override
			public void onFailure(ApiException e) {
				if (callback != null) {
					callback.onFailure(ErrorUtil.fromApiException(e));
				}
			}

		});
	}

	private OfferList getList() {
		OfferList masterList = new OfferList();

		Offer tutorial = getTutorialOffer(cachedOfferList);
		// Tutorial offer should be first
		if (tutorial != null) {
			masterList.add(tutorial);
		}

		Iterator<Offer> nativeSpendItr = nativeSpendOfferList.getOffers().iterator();
		Iterator<Offer> nativeEarnItr = nativeEarnOfferList.getOffers().iterator();
		Iterator<Offer> earnMPItr = cachedOfferList.getOffers().iterator();

		while (nativeSpendItr.hasNext() || nativeEarnItr.hasNext() || earnMPItr.hasNext()) {
			// 1st nativeSpendItr, 2ns nativeEarnItr
			if(nativeSpendItr.hasNext()) {
				masterList.add(nativeSpendItr.next());
				if(nativeEarnItr.hasNext()) {
					masterList.add(nativeEarnItr.next());
				}
			} else {
				// 1st nativeEarnItr, 2nd earnMPItr
				if(nativeEarnItr.hasNext()) {
					masterList.add(nativeEarnItr.next());
				} else {
					if(earnMPItr.hasNext()) {
						Offer offer = earnMPItr.next();
						if((tutorial != null && !tutorial.equals(offer)) || (tutorial == null)) {
							masterList.add(offer);
						}
					}
				}
			}
		}

		masterList.setPaging(cachedOfferList.getPaging());
		return masterList;
	}

	private Offer getTutorialOffer(OfferList offerList) {
		if (offerList != null && offerList.getOffers() != null) {
			List<Offer> offers = offerList.getOffers();
			for (Offer offer: offers) {
				if (offer.getContentType() == ContentTypeEnum.TUTORIAL) {
					return offer;
				}
			}
		}
		return null;
	}

	private void removeFromCachedOfferList(String offerID) {
		if (cachedOfferList == null) {
			return;
		}

		Offer offer = cachedOfferList.getOfferByID(offerID);
		cachedOfferList.remove(offer);
	}

	@Override
	public void addNativeOfferClickedObserver(@NonNull Observer<NativeOfferClickEvent> observer) {
		nativeSpendOfferObservable.addObserver(observer);
	}

	@Override
	public void removeNativeOfferClickedObserver(@NonNull Observer<NativeOfferClickEvent> observer) {
		nativeSpendOfferObservable.removeObserver(observer);
	}

	@Override
	public ObservableData<NativeOfferClickEvent> getNativeSpendOfferObservable() {
		return nativeSpendOfferObservable;
	}

	@Override
	public Subscription<Offer> addNativeOfferRemovedObserver(@NonNull Observer<Offer> observer) {
		return nativeOfferRemoved.subscribe(observer);
	}

	@Override
	public boolean addNativeOffer(@NonNull NativeOffer nativeOffer, boolean dismissOnTap) {
		String offerId = nativeOffer.getId();
		if (offerId != null) {
			Offer offer = OfferConverter.toOffer(nativeOffer);
			if (offer != null) {
				nativeOfferMap.put(offerId, dismissOnTap);

				if (offer.getOfferType() == OfferType.EARN) {
					addOrUpdate(nativeEarnOfferList, offer);
				} else {
					addOrUpdate(nativeSpendOfferList, offer);
				}

				return true;
			}
			return false;
		}
		return false;
	}

	private void addOrUpdate(OfferList offerList, Offer offer) {
		int index = offerList.getOffers().indexOf(offer);
		if (index >= 0) {
			// Update existing
			offerList.getOffers().set(index, offer);
		} else {
			// Add new
			offerList.addAtIndex(0, offer);
		}
	}

	@Override
	public boolean removeNativeOffer(@NonNull NativeOffer nativeOffer) {
		String offerId = nativeOffer.getId();
		if (offerId != null) {
			Offer offer = OfferConverter.toOffer(nativeOffer);
			if (offer != null) {
				nativeOfferRemoved.postValue(offer);
				nativeOfferMap.remove(offerId);
				if (offer.getOfferType() == OfferType.EARN) {
					return nativeEarnOfferList.remove(offer);
				} else {
					return nativeSpendOfferList.remove(offer);
				}
			}
			return false;

		} else {
			return false;
		}
	}

	@Override
	public boolean shouldDismissOnTap(@NonNull String offerId) {
		final Boolean shouldDismissOnTap = nativeOfferMap.get(offerId);
		return shouldDismissOnTap == null ? false : shouldDismissOnTap;
	}

	@Override
	public void logout() {
		cachedOfferList.removeAll();
	}
}
