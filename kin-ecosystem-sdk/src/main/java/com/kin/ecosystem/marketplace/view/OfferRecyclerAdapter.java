package com.kin.ecosystem.marketplace.view;

import static com.kin.ecosystem.util.DeviceUtils.DensityDpi.XXHDPI;
import static com.kin.ecosystem.util.StringUtil.getAmountFormatted;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.AbstractBaseViewHolder;
import com.kin.ecosystem.base.BaseRecyclerAdapter;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.Offer.ContentTypeEnum;
import com.kin.ecosystem.network.model.Offer.OfferTypeEnum;
import com.kin.ecosystem.util.DeviceUtils;

class OfferRecyclerAdapter extends BaseRecyclerAdapter<Offer, OfferRecyclerAdapter.ViewHolder> {

    private static final float NORMAL_WIDTH_RATIO = 0.38f;
    private static final float NORMAL_HEIGHT_RATIO = 0.25f;
    private static final float HIGH_RES_HEIGHT_RATIO = 0.28f;

    private static final String KIN = " Kin";

    protected float getImageWidthRatio() {
        return NORMAL_WIDTH_RATIO;
    }

    protected float getImageHeightRatio() {
        return DeviceUtils.isDensity(XXHDPI) ? HIGH_RES_HEIGHT_RATIO : NORMAL_HEIGHT_RATIO;
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

        ViewHolder(View item_root) {
            super(item_root);
            getView(R.id.title);
            getView(R.id.sub_title);
            getView(R.id.amount_text);
            setViewSize(R.id.image, imageWidth, imageHeight);
        }

        @Override
        protected void init(Context context) {
            imageWidth = (int) (DeviceUtils.getScreenWidth() * getImageWidthRatio());
            imageHeight = (int) (DeviceUtils.getScreenWidth() * getImageHeightRatio());
        }

        @Override
        protected void bindObject(final Offer item) {
            setImageUrlResized(R.id.image, item.getImage(), imageWidth, imageHeight);
            setText(R.id.title, item.getTitle());
            setText(R.id.sub_title, item.getDescription());
            setAmountText(item);

            if (item.getOfferType() == OfferTypeEnum.EARN && item.getContentType() == ContentTypeEnum.POLL) {
                setOnItemClickListener(getOnItemClickListener());
            }
        }

        private void setAmountText(final Offer item) {
            int amount = item.getAmount();
            if (item.getOfferType() == OfferTypeEnum.EARN) {
                setText(R.id.amount_text, "+" + getAmountFormatted(amount) + KIN);
            } else {
                setText(R.id.amount_text, getAmountFormatted(amount) + KIN);
            }
        }
    }
}
