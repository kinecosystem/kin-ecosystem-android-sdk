package kin.ecosystem.core.data.offer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import kin.ecosystem.common.Callback;
import kin.ecosystem.common.KinCallback;
import kin.ecosystem.common.ObservableData;
import kin.ecosystem.common.Observer;
import kin.ecosystem.common.model.NativeOffer;
import kin.ecosystem.common.model.NativeSpendOffer;
import kin.ecosystem.core.data.order.OrderDataSource;
import kin.ecosystem.core.network.ApiException;
import kin.ecosystem.core.network.model.Offer;
import kin.ecosystem.core.network.model.OfferList;
import kin.ecosystem.core.network.model.Order;
import kin.ecosystem.core.network.model.Order.Status;
import kin.ecosystem.core.util.Converter;
import kin.ecosystem.core.util.ErrorUtil;

public class OfferRepository implements OfferDataSource {

    private static OfferRepository instance = null;

    private final OfferDataSource.Remote remoteData;
    private final OrderDataSource orderRepository;

    private OfferList nativeOfferList = new OfferList();
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
    public boolean addNativeOffer(@NonNull NativeOffer nativeOffer) {
		Offer offer = nativeOfferList.getOfferByID(nativeOffer.getId());
		if(offer == null) {
            offer = Converter.toOffer(nativeOffer);
            return offer != null && nativeOfferList.addAtIndex(0, offer);
        }
		return false;
	}

    @Override
    public boolean removeNativeOffer(@NonNull NativeOffer nativeOffer) {
        Offer offer = Converter.toOffer(nativeOffer);
        return nativeOfferList.remove(offer);
    }
}
