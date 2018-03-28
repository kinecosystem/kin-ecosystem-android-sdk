package com.kin.ecosystem.marketplace.presenter;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.blockchain.BlockchainSource;
import com.kin.ecosystem.data.blockchain.IBlockchainSource;
import com.kin.ecosystem.data.offer.OfferDataSource;
import com.kin.ecosystem.data.order.OrderDataSource;
import com.kin.ecosystem.data.order.OrderRepository;
import com.kin.ecosystem.marketplace.view.IMarketplaceView;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.Offer.OfferTypeEnum;
import com.kin.ecosystem.network.model.OfferInfo;
import com.kin.ecosystem.network.model.OfferList;
import com.kin.ecosystem.network.model.Order;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MarketplacePresenter extends BasePresenter<IMarketplaceView> implements IMarketplacePresenter {

    private static final int NOT_FOUND = -1;

    private final OfferDataSource offerRepository;
    private final OrderDataSource orderRepository;
    private final IBlockchainSource blockchainSource;

    private List<Offer> spendList;
    private List<Offer> earnList;

    private Observer<Offer> pendingOfferObserver;
    private Observer<Order> completedOrderObserver;
    private final Gson gson;

    public MarketplacePresenter(@NonNull final OfferDataSource offerRepository,
        @NonNull final OrderDataSource orderRepository, @Nullable final IBlockchainSource blockchainSource) {
        this.spendList = new ArrayList<>();
        this.earnList = new ArrayList<>();
        this.offerRepository = offerRepository;
        this.orderRepository = orderRepository;
        this.blockchainSource = blockchainSource;
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
        int index;
        if (offer.getOfferType() == OfferTypeEnum.EARN) {
            index = earnList.indexOf(offer);
            if (index != NOT_FOUND) {
                earnList.remove(index);
                notifyEarnItemRemoved(index);
            }

        } else {
            index = spendList.indexOf(offer);
            if (index != NOT_FOUND) {
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
            List<Offer> newEarnOffers = new ArrayList<>();
            List<Offer> newSpendOffers = new ArrayList<>();

            splitOffersByType(offerList.getOffers(), newEarnOffers, newSpendOffers);
            syncList(newEarnOffers, earnList, OfferTypeEnum.EARN);
            syncList(newSpendOffers, spendList, OfferTypeEnum.SPEND);
        }
    }

    private void syncList(List<Offer> newList, List<Offer> oldList, OfferTypeEnum offerType) {
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
    }

    private void notifyItemRemoved(int index, OfferTypeEnum offerType) {
        if (isSpend(offerType)) {
            notifySpendItemRemoved(index);
        } else {
            notifyEarnItemRemoved(index);
        }
    }

    private void notifyItemInserted(int index, OfferTypeEnum offerType) {
        if (isSpend(offerType)) {
            notifySpendItemInserted(index);
        } else {
            notifyEarnItemInserted(index);
        }
    }

    private boolean isSpend(OfferTypeEnum offerType) {
        return offerType == OfferTypeEnum.SPEND;
    }

    private void setOfferList(OfferList offerList) {
        if (offerList != null && offerList.getOffers() != null) {
            splitOffersByType(offerList.getOffers(), this.earnList, this.spendList);

            if (this.view != null) {
                this.view.updateEarnList(earnList);
                this.view.updateSpendList(spendList);
            }
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
            int balance = blockchainSource.getBalance();
            final BigDecimal amount = new BigDecimal(offer.getAmount());

            if (balance < amount.intValue()) {
                showToast("You don't have enough Kin");
                return;
            }

            OfferInfo offerInfo = deserializeOfferInfo(offer.getContent());
            if (offerInfo != null) {
                showSpendDialog(offerInfo, offer);
            } else {
                showToast("Oops something went wrong...");
            }
        }
    }

    @Override
    public void balanceItemClicked() {
        if(view != null) {
            view.navigateToOrderHistory();
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
