package com.kin.ecosystem.marketplace.view;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.DisplayMetrics;
import android.view.View;

import com.kin.ecosystem.R;
import com.kin.ecosystem.base.AbstractBaseViewHolder;
import com.kin.ecosystem.base.BaseRecyclerAdapter;
import com.kin.ecosystem.network.model.Offer;

class OfferRecyclerAdapter extends BaseRecyclerAdapter<Offer, OfferRecyclerAdapter.ViewHolder> {

    private static final float DEF_WIDTH_RATIO = 0.4f;
    private static final float DEF_HEIGHT_RATIO = 0.3f;

    protected float getImageWidthRatio() {
        return DEF_WIDTH_RATIO;
    }

    protected float getImageHeightRatio() {
        return DEF_HEIGHT_RATIO;
    }

    OfferRecyclerAdapter(@LayoutRes int layoutResID) {
        super(layoutResID);
        openLoadAnimation(SLIDEIN_RIGHT);
        isUseEmpty(true);
    }

    @Override
    protected void convert(ViewHolder holder, Offer item) {
        holder.bindObject(item);
    }

    @Override
    protected ViewHolder createBaseViewHolder(View view) {
        return new ViewHolder(view);
    }

    class ViewHolder extends AbstractBaseViewHolder<Offer> {

        private int imageWidth;
        private int imageHeight;

        public ViewHolder(View item_root) {
            super(item_root);
            getView(R.id.title);
            getView(R.id.sub_title);
            getView(R.id.amount_text);
            setViewSize(R.id.image, imageWidth, imageHeight);
        }

        @Override
        protected void initSizes(Context context) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            imageWidth = (int) (displayMetrics.widthPixels * getImageWidthRatio());
            imageHeight = (int) (displayMetrics.widthPixels * getImageHeightRatio());
        }

        @Override
        protected void bindObject(Offer item) {
            setImageUrlResized(R.id.image, item.getImage(), imageWidth, imageHeight);
            setText(R.id.title, item.getTitle());
            setText(R.id.sub_title, item.getDescription());
            setText(R.id.amount_text, item.getAmount() + " KIN");
        }
    }
}
