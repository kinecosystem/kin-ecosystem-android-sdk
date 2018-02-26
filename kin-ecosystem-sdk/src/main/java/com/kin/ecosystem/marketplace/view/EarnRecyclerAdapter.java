package com.kin.ecosystem.marketplace.view;

import android.support.annotation.NonNull;
import com.kin.ecosystem.R;

class EarnRecyclerAdapter extends OfferRecyclerAdapter {

    EarnRecyclerAdapter(@NonNull OnItemClickListener itemClickListener) {
        super(R.layout.earn_recycler_item, itemClickListener);
    }
}
