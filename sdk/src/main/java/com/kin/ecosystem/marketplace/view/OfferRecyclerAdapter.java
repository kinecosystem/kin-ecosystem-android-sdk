package com.kin.ecosystem.marketplace.view;

import static com.kin.ecosystem.core.util.StringUtil.getAmountFormatted;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.AbstractBaseViewHolder;
import com.kin.ecosystem.base.BaseRecyclerAdapter;
import com.kin.ecosystem.core.network.model.Offer;
import com.kin.ecosystem.core.network.model.Offer.ContentTypeEnum;
import com.kin.ecosystem.core.network.model.Offer.OfferType;
import com.kin.ecosystem.core.util.DeviceUtils;
import com.kin.ecosystem.marketplace.view.OfferRecyclerAdapter.ViewHolder;

abstract class OfferRecyclerAdapter extends BaseRecyclerAdapter<Offer, ViewHolder> {

    private static final String KIN = " Kin";

    abstract float getImageWidthToScreenRatio();

    abstract float getImageHeightRatio();

    OfferRecyclerAdapter(@LayoutRes int layoutResID) {
        super(layoutResID);
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

        ViewHolder(View item_root) {
            super(item_root);
            getView(R.id.title);
            getView(R.id.sub_title);
            getView(R.id.amount_text);
            setViewSize(R.id.image, imageWidth, imageHeight);
        }

        @Override
        protected void init(Context context) {
            imageWidth = (int) (DeviceUtils.getScreenWidth() * getImageWidthToScreenRatio());
            imageHeight = (int) (imageWidth * getImageHeightRatio());
        }

        @Override
        protected void bindObject(final Offer item) {
            setImageUrlResized(R.id.image, item.getImage(), imageWidth, imageHeight);
            setText(R.id.title, item.getTitle());
            setText(R.id.sub_title, item.getDescription());
            setAmountText(item);

            if (item.getOfferType() == OfferType.EARN && item.getContentType() == ContentTypeEnum.POLL) {
                setOnItemClickListener(getOnItemClickListener());
            }
        }

        private void setAmountText(final Offer item) {
            int amount = item.getAmount();
            if (item.getOfferType() == OfferType.EARN) {
                setText(R.id.amount_text, "+" + getAmountFormatted(amount) + KIN);
            } else {
                setText(R.id.amount_text, getAmountFormatted(amount) + KIN);
            }
        }
    }
}
