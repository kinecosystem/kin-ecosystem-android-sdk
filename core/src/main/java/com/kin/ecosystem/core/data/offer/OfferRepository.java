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
import com.kin.ecosystem.core.network.model.OfferList;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.core.network.model.Order.Status;
import com.kin.ecosystem.core.util.ErrorUtil;
import com.kin.ecosystem.core.util.OfferConverter;
import java.util.List;

public class OfferRepository implements OfferDataSource {

	private static volatile OfferRepository instance = null;

	private final OfferDataSource.Remote remoteData;
	private final OrderDataSource orderRepository;

	private OfferList nativeOfferList = new OfferList();
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
		masterList.addAll(nativeOfferList);
		masterList.addAll(cachedOfferList);
		masterList.setPaging(cachedOfferList.getPaging());
		return masterList;
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
	public boolean addNativeOffer(@NonNull NativeOffer nativeOffer) {
		String offerId = nativeOffer.getId();
		if (offerId != null) {
			Offer offer = OfferConverter.toOffer(nativeOffer);
			if (offer != null) {
				addOrUpdate(nativeOfferList, offer);
				return true;
			}
			return false;
		}
		return false;
	}

	@Override
	public boolean addAllNativeOffers(List<NativeOffer> nativeOfferList) {
		boolean result = true;
		for (int i = nativeOfferList.size() - 1; i >= 0 ; i--) {
			if (!addNativeOffer(nativeOfferList.get(i))) {
				result = false;
			}
		}
		return result;
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
				return nativeOfferList.remove(offer);
			}
			return false;

		} else {
			return false;
		}
	}

	@Override
	public void logout() {
		cachedOfferList.removeAll();
	}
}
