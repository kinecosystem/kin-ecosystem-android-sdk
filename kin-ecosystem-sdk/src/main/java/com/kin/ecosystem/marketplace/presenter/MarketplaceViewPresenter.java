package com.kin.ecosystem.marketplace.presenter;


import android.support.annotation.NonNull;
import android.view.View;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.base.BaseRecyclerAdapter;
import com.kin.ecosystem.base.BaseRecyclerAdapter.OnItemClickListener;
import com.kin.ecosystem.data.offer.OfferRepository;
import com.kin.ecosystem.data.order.OrderRepository;
import com.kin.ecosystem.marketplace.view.IMarketplaceView;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.OfferList;
import java.util.ArrayList;
import java.util.List;

public class MarketplaceViewPresenter extends BasePresenter<IMarketplaceView> implements OnItemClickListener {

    private final OfferRepository offerRepository;
    private final OrderRepository orderRepository;

    private List<Offer> spendList;
    private List<Offer> earnList;

    public MarketplaceViewPresenter(@NonNull final OfferRepository offerRepository,
        @NonNull final OrderRepository orderRepository) {
        this.spendList = new ArrayList<>();
        this.earnList = new ArrayList<>();
        this.offerRepository = offerRepository;
        this.orderRepository = orderRepository;
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
        getOffers();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        release();
    }

    private void release() {
        spendList = null;
        earnList = null;
    }

    private void getOffers() {
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
    public void onItemClick(BaseRecyclerAdapter adapter, View view, int position) {
        final Offer offer = (Offer) adapter.getData().get(position);
        if (this.view != null) {
            this.view.showOfferActivity(offer);
        }
    }
}
