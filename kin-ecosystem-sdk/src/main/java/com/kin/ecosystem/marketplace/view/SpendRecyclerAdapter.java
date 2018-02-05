package com.kin.ecosystem.marketplace.view;

import android.support.annotation.Nullable;

import com.kin.ecosystem.R;
import com.kin.ecosystem.network.model.Offer;

import java.util.List;

class SpendRecyclerAdapter extends OfferRecyclerAdapter {

    private static final float IMAGE_WIDTH_RATIO = 0.8f;

    SpendRecyclerAdapter() {
        super(R.layout.spend_recycler_item);
    }

    @Override
    protected float getImageWidthRatio() {
        return IMAGE_WIDTH_RATIO;
    }
}
