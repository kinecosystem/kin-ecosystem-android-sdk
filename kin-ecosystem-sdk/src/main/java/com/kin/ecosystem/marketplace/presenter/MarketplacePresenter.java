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

    private static final int NOT_FOUND = -1;

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

    @Override
    public void onAttach(IMarketplaceView view) {
        super.onAttach(view);
        getCachedOffers();
        listenToPendingOffers();
        listenToCompletedOrders();
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
        offerRepository.getPendingOffer().removeObserver(pendingOfferObserver);
        orderRepository.removeCompletedOrderObserver(completedOrderObserver);
        spendList = null;
        earnList = null;
    }

    @Override
    public void getOffers() {
        this.offerRepository.getOffers(new Callback<OfferList>() {
            @Override
            public void onResponse(OfferList offerList) {
                syncOffers(offerList);
            }

            @Override
            public void onFailure(Throwable t) {
                //TODO show error msg
            }
        });
    }

    private void syncOffers(OfferList offerList) {
        if (offerList != null && offerList.getOffers() != null) {
            List<Offer> earnOffers = new ArrayList<>();
            List<Offer> spendOffers = new ArrayList<>();

            splitOffersByType(offerList.getOffers(), earnOffers, spendOffers);

            // check if next [Earn] offer should be removed
            if (earnOffers.size() > 0) {
                for (int i = 0; i < earnList.size(); i++) {
                    Offer offer = earnList.get(i);
                    int index = earnOffers.indexOf(offer);
                    if (index == NOT_FOUND) {
                        earnList.remove(i);
                        notifyEarnItemRemoved(i);
                    }
                }
            }

            // Add missing [Earn] offers, the order matters
            for (int i = 0; i < earnOffers.size(); i++) {
                Offer offer = earnOffers.get(i);
                if (i < earnList.size()) {
                    if (!earnList.get(i).equals(offer)) {
                        earnList.add(i, offer);
                        notifyEarnItemInserted(i);
                    }
                } else {
                    earnList.add(offer);
                    notifyEarnItemInserted(i);
                }
            }

            // check if next [Spend] offer should be removed
            if (spendOffers.size() > 0) {
                for (int i = 0; i < spendList.size(); i++) {
                    Offer offer = spendList.get(i);
                    int index = spendOffers.indexOf(offer);
                    if (index == NOT_FOUND) {
                        spendList.remove(i);
                        notifySpendItemRemoved(i);
                    }
                }
            }

            // Add missing [Spend] offers, the order matters
            for (int i = 0; i < spendOffers.size(); i++) {
                Offer offer = spendOffers.get(i);
                if (i < spendList.size()) {
                    if (!spendList.get(i).equals(offer)) {
                        spendList.add(i, offer);
                        notifySpendItemInserted(i);
                    }
                } else {
                    spendList.add(offer);
                    notifySpendItemInserted(i);
                }
            }
        }
    }

    private void setOfferList(OfferList offerList) {
        if (offerList != null && offerList.getOffers() != null) {
            splitOffersByType(offerList.getOffers(), this.earnList, this.spendList);
            updateLists();
        }
    }

    private void splitOffersByType(List<Offer> list, List<Offer> earnList, List<Offer> spendList) {
        for (Offer offer : list) {
            if (offer.getOfferType() == Offer.OfferTypeEnum.EARN) {
                earnList.add(offer);
            } else {
                spendList.add(offer);
            }
        }
    }

    private void updateLists() {
        if (this.view != null) {
            this.view.updateEarnList(earnList);
            this.view.updateSpendList(spendList);
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
