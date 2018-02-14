package com.kin.ecosystem.marketplace.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.view.View;

import com.kin.ecosystem.R;
import com.kin.ecosystem.base.AbstractBaseViewHolder;
import com.kin.ecosystem.base.BaseRecyclerAdapter;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.poll.view.PollActivity;
import com.kin.ecosystem.util.DeviceUtils;

import static com.kin.ecosystem.util.DeviceUtils.DensityDpi.XXHDPI;

class OfferRecyclerAdapter extends BaseRecyclerAdapter<Offer, OfferRecyclerAdapter.ViewHolder> {

    private static final float NORMAL_WIDTH_RATIO = 0.38f;
    private static final float NORMAL_HEIGHT_RATIO = 0.25f;
    private static final float HIGH_RES_HEIGHT_RATIO = 0.28f;

    protected float getImageWidthRatio() {
        return NORMAL_WIDTH_RATIO;
    }
    private final Context context;

    protected float getImageHeightRatio() {
        return DeviceUtils.isDensity(XXHDPI) ? HIGH_RES_HEIGHT_RATIO : NORMAL_HEIGHT_RATIO;
    }

    OfferRecyclerAdapter(Context context, @LayoutRes int layoutResID) {
        super(layoutResID);

        this.context = context;
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
            imageWidth = (int) (DeviceUtils.getScreenWidth() * getImageWidthRatio());
            imageHeight = (int) (DeviceUtils.getScreenWidth() * getImageHeightRatio());
        }

        @Override
        protected void bindObject(final Offer item) {
            setImageUrlResized(R.id.image, item.getImage(), imageWidth, imageHeight);
            setText(R.id.title, item.getTitle());
            setText(R.id.sub_title, item.getDescription());
            setText(R.id.amount_text, item.getAmount() + " Kin");

            if (item.getOfferType() == Offer.OfferTypeEnum.EARN && item.hasPollJsonContent()) {
                getView(R.id.image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent = new Intent(context, PollActivity.class);
                        intent.putExtra("jsondata", item.getContentAsJsonString());
                        context.startActivity(intent);
                    }
                });
            }
        }
    }
}
