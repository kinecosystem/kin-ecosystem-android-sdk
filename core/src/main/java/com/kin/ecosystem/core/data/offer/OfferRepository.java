package com.kin.ecosystem.core.data.offer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.ObservableData;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.model.NativeOffer;
import com.kin.ecosystem.common.model.NativeSpendOffer;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.Offer;
import com.kin.ecosystem.core.network.model.OfferList;
import com.kin.ecosystem.core.util.OfferConverter;
import com.kin.ecosystem.core.util.ErrorUtil;
import com.kin.ecosystem.core.data.order.OrderDataSource;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.core.network.model.Order.Status;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class OfferRepository implements OfferDataSource {

    private static OfferRepository instance = null;

    private final OfferDataSource.Remote remoteData;
    private final OrderDataSource orderRepository;

    private HashMap<Offer, Boolean> nativeOfferMap = new HashMap<>();
    private OfferList cachedOfferList = new OfferList();

    private ObservableData<NativeSpendOffer> nativeSpendOfferObservable = ObservableData.create();

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
                if(order.getStatus() == Status.PENDING) {
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
        List<Offer> list = new ArrayList<>(nativeOfferMap.keySet());
        OfferList nativeList = new OfferList();
        nativeList.setOffers(list);
        masterList.addAll(nativeList);
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
    public void addNativeOfferClickedObserver(@NonNull Observer<NativeSpendOffer> observer) {
        nativeSpendOfferObservable.addObserver(observer);
    }

    @Override
    public void removeNativeOfferClickedObserver(@NonNull Observer<NativeSpendOffer> observer) {
        nativeSpendOfferObservable.removeObserver(observer);
    }

    @Override
    public ObservableData<NativeSpendOffer> getNativeSpendOfferObservable() {
        return nativeSpendOfferObservable;
    }

    @Override
    public void addNativeOffer(@NonNull NativeOffer nativeOffer, boolean dismissOnTap) {
        Offer offer2 = OfferConverter.toOffer(nativeOffer);
        nativeOfferMap.put(offer2, dismissOnTap);
	}

    @Override
    public void removeNativeOffer(@NonNull NativeOffer nativeOffer) {
        Offer offer = OfferConverter.toOffer(nativeOffer);
        nativeOfferMap.remove(offer);
    }

    @Override
    public boolean shouldCloseOnTap(Offer offer) {
        return nativeOfferMap.get(offer);
    }
}
