package com.kin.ecosystem.marketplace.view;

import com.kin.ecosystem.R;

class SpendRecyclerAdapter extends OfferRecyclerAdapter {

    private static final float IMAGE_WIDTH_RATIO = 0.8f;

    SpendRecyclerAdapter() {
        super(R.layout.kinecosystem_spend_recycler_item);
    }

    @Override
    protected float getImageWidthRatio() {
        return IMAGE_WIDTH_RATIO;
    }
}
