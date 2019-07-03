package com.kin.ecosystem.marketplace.view;

import static com.kin.ecosystem.core.util.StringUtil.getAmountFormatted;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.DiffUtil.DiffResult;
import android.view.View;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.AbstractBaseViewHolder;
import com.kin.ecosystem.base.BaseRecyclerAdapter;
import com.kin.ecosystem.core.network.model.Offer;
import com.kin.ecosystem.core.network.model.Offer.ContentTypeEnum;
import com.kin.ecosystem.core.network.model.Offer.OfferType;
import com.kin.ecosystem.core.util.DeviceUtils;
import com.kin.ecosystem.marketplace.view.OfferRecyclerAdapter.ViewHolder;
import java.util.List;

class OfferRecyclerAdapter extends BaseRecyclerAdapter<Offer, ViewHolder> {

    private static final float WIDTH_RATIO = 0.205f;
    private static final int AMOUNT_SPACE_EARN = 0;
    private static int AMOUNT_SPACE_SPEND = -1;

    OfferRecyclerAdapter() {
        super(R.layout.kinecosystem_offer_recycler_item);
    }

    @Override
    protected void convert(ViewHolder holder, Offer item) {
        holder.bindObject(item);
    }

    @Override
    protected ViewHolder createBaseViewHolder(View view) {
        return new ViewHolder(view);
    }

    public void updateList(List<Offer> newList) {
        DiffResult diffResult = DiffUtil.calculateDiff(new OffersDiffUtil(getData(), newList));
        data.clear();
        data.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    class ViewHolder extends AbstractBaseViewHolder<Offer> {

        private int imageSize;
        private int earnColor;
        private int spendColor;

        ViewHolder(View item_root) {
            super(item_root);
            getView(R.id.title);
            getView(R.id.sub_title);
            getView(R.id.amount_text);
            getView(R.id.kin_logo);
            setViewSize(R.id.image, imageSize, imageSize);
        }

        @Override
        protected void init(Context context) {
            imageSize = (int) (DeviceUtils.getScreenWidth() * WIDTH_RATIO);
            earnColor = ContextCompat.getColor(context, R.color.kinecosystem_purple);
            spendColor = ContextCompat.getColor(context, R.color.kinecosystem_green);
			if(AMOUNT_SPACE_SPEND == -1) {
				AMOUNT_SPACE_SPEND = context.getResources().getDimensionPixelSize(R.dimen.kinecosystem_offer_title_char_space);
			}
        }

        @Override
        protected void bindObject(final Offer item) {
            setImageUrlResized(R.id.image, item.getImage(), imageSize, imageSize);
            setTitle(item);
            setAmountText(item);
            setText(R.id.sub_title, item.getDescription());
            setSpaceAmount(item.getOfferType());

            if (item.getOfferType() == OfferType.EARN && item.getContentType() == ContentTypeEnum.POLL) {
                setOnItemClickListener(getOnItemClickListener());
            }
        }

        private void setSpaceAmount(OfferType offerType) {
            if (offerType == OfferType.EARN) {
				setViewLeftMargin(R.id.kin_logo, AMOUNT_SPACE_EARN);
            } else {
				setViewLeftMargin(R.id.kin_logo, AMOUNT_SPACE_SPEND);
            }
        }

        private void setTitle(final Offer item) {
            if(item.getOfferType() == OfferType.EARN) {
                setText(R.id.title, item.getTitle() + " +");
                setTextColor(R.id.title, earnColor);
            } else  {
                setText(R.id.title, item.getTitle());
                setTextColor(R.id.title, spendColor);
            }
        }

        private void setAmountText(final Offer item) {
            int amount = item.getAmount();
            if (item.getOfferType() == OfferType.EARN) {
            	setVectorDrawable(R.id.kin_logo, R.drawable.kinecosystem_ic_kin_logo_small);
                setTextColor(R.id.amount_text, earnColor);
            } else {
				setVectorDrawable(R.id.kin_logo, R.drawable.kinecosystem_ic_kin_logo_small_green);
                setTextColor(R.id.amount_text, spendColor);
            }
            setText(R.id.amount_text, getAmountFormatted(amount));
        }
    }
}
