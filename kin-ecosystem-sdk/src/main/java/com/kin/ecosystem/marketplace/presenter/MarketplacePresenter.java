package com.kin.ecosystem.marketplace.presenter;


import android.support.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.blockchain.BlockchainSource;
import com.kin.ecosystem.data.offer.OfferDataSource;
import com.kin.ecosystem.data.order.OrderDataSource;
import com.kin.ecosystem.data.order.OrderRepository;
import com.kin.ecosystem.marketplace.view.IMarketplaceView;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.Offer.OfferTypeEnum;
import com.kin.ecosystem.network.model.OfferInfo;
import com.kin.ecosystem.network.model.OfferList;
import com.kin.ecosystem.network.model.Order;
import java.util.ArrayList;
import java.util.List;

public class MarketplacePresenter extends BasePresenter<IMarketplaceView> implements IMarketplacePresenter {

    private final OfferDataSource offerRepository;
    private final OrderDataSource orderRepository;

    private List<Offer> spendList;
    private List<Offer> earnList;

    private Observer<Offer> pendingOfferObserver;
    private Observer<Order> completedOrderObserver;
    private final Gson gson;

    public MarketplacePresenter(@NonNull final OfferDataSource offerRepository,
        @NonNull final OrderDataSource orderRepository) {
        this.spendList = new ArrayList<>();
        this.earnList = new ArrayList<>();
        this.offerRepository = offerRepository;
        this.orderRepository = orderRepository;
        this.gson = new Gson();
    }

    private void splitOffersByType(List<Offer> list) {
        for (Offer offer : list) {
            if (offer.getOfferType() == Offer.OfferTypeEnum.EARN) {
                this.earnList.add(offer);
            } else {
                this.spendList.add(offer);
            }
        }

        if (this.view != null) {
            this.view.updateEarnList(earnList);
            this.view.updateSpendList(spendList);
        }
    }

    @Override
    public void onAttach(IMarketplaceView view) {
        super.onAttach(view);
        listenToPendingOffers();
        listenToCompletedOrders();
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
                if(offer == null) {
                    getOffers();
                } else {
                    removeOfferFromList(offer);
                }
            }
        };
        offerRepository.getPendingOffer().addObserver(pendingOfferObserver);
    }

    private void removeOfferFromList(Offer offer) {
        int index = -1;
        if (offer.getOfferType() == OfferTypeEnum.EARN) {
            index = earnList.indexOf(offer);
            if (index != -1) {
                earnList.remove(index);
                notifyEarnItemRemoved(index);
            }

        } else {
            index = spendList.indexOf(offer);
            if (index != -1) {
                spendList.remove(index);
                notifySpendItemRemoved(index);
            }
        }
    }

    private void notifyEarnItemRemoved(int index) {
        if(view != null) {
            view.notifyEarnItemRemoved(index);
        }
    }

    private void notifySpendItemRemoved(int index) {
        if(view != null) {
            view.notifySpendItemRemoved(index);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        release();
    }

    private void release() {
        offerRepository.getPendingOffer().removeObserver(pendingOfferObserver);
        orderRepository.removeCompletedOrderObserver(completedOrderObserver);
        spendList = null;
        earnList = null;
    }

    @Override
    public void getOffers() {
        OfferList cachedOfferList = offerRepository.getCachedOfferList();
        setOfferList(cachedOfferList);

        this.offerRepository.getOffers(new Callback<OfferList>() {
            @Override
            public void onResponse(OfferList offerList) {
                setOfferList(offerList);
            }

            @Override
            public void onFailure(Throwable t) {
                //TODO show error msg
            }
        });
    }

    private void setOfferList(OfferList offerList) {
        if (offerList != null && offerList.getOffers() != null) {
            splitOffersByType(offerList.getOffers());
        }
    }

    @Override
    public void onItemClicked(int position, OfferTypeEnum offerType) {
        final Offer offer;
        if (offerType == OfferTypeEnum.EARN) {
            offer = earnList.get(position);
            if (this.view != null) {
                this.view.showOfferActivity(offer.getContent(), offer.getId());
            }
        } else {
            offer = spendList.get(position);
            OfferInfo offerInfo = deserializeOfferInfo(offer.getContent());
            if (offerInfo != null) {
                showSpendDialog(offerInfo, offer);
            } else {
                showToast("Oops something went wrong...");
            }
        }
    }

    private void showSpendDialog(@NonNull final OfferInfo offerInfo, @NonNull final Offer offer) {
        if (this.view != null) {
            this.view.showSpendDialog(createSpendDialogPresenter(offerInfo, offer));
        }
    }

    private ISpendDialogPresenter createSpendDialogPresenter(@NonNull final OfferInfo offerInfo,
        @NonNull final Offer offer) {
        return new SpendDialogPresenter(offerInfo, offer, BlockchainSource.getInstance(),
            OrderRepository.getInstance());
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
