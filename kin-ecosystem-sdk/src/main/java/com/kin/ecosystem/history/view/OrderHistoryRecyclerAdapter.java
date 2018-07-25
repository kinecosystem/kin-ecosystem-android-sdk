package com.kin.ecosystem.history.view;

import static kin.ecosystem.core.util.DateUtil.getDateFormatted;
import static kin.ecosystem.core.util.StringUtil.getAmountFormatted;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.AbstractBaseViewHolder;
import com.kin.ecosystem.base.BaseRecyclerAdapter;
import com.kin.ecosystem.history.view.OrderHistoryRecyclerAdapter.ViewHolder;
import com.kin.ecosystem.network.model.Offer.OfferType;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.network.model.Order.Status;


public class OrderHistoryRecyclerAdapter extends BaseRecyclerAdapter<Order, ViewHolder> {

    private static final int NOT_INITIALIZED = -1;

    private static int colorBlue = NOT_INITIALIZED;
    private static int colorRed = NOT_INITIALIZED;
    private static int colorGrayLight = NOT_INITIALIZED;

    private static int subTitleFontSize = NOT_INITIALIZED;
    private static int itemHeight = NOT_INITIALIZED;
    private static int itemHalfHeight = NOT_INITIALIZED;

    private static final String TRANSACTION_FAILED_MSG = "Transaction failed";

    OrderHistoryRecyclerAdapter() {
        super(R.layout.kinecosystem_order_history_recycler_item);
    }

    private void initColors(Context context) {
        if (colorBlue == NOT_INITIALIZED) {
            colorBlue = ContextCompat.getColor(context, R.color.kinecosystem_bluePrimary);
        }
        if (colorRed == NOT_INITIALIZED) {
            colorRed = ContextCompat.getColor(context, R.color.kinecosystem_red);
        }
        if (colorGrayLight == NOT_INITIALIZED) {
            colorGrayLight = ContextCompat.getColor(context, R.color.kinecosystem_gray_light);
        }
    }

    private void initSizes(Context context) {
        Resources resources = context.getResources();
        if (subTitleFontSize == NOT_INITIALIZED) {
            subTitleFontSize = resources.getDimensionPixelSize(R.dimen.kinecosystem_sub_title_size);
        }
        if (itemHeight == NOT_INITIALIZED) {
            itemHeight = resources.getDimensionPixelOffset(R.dimen.kinecosystem_order_history_item_height);
            itemHalfHeight = itemHeight / 2;
        }
    }

    @Override
    protected void convert(ViewHolder holder, final Order item) {
        holder.bindObject(item);
    }

    @Override
    protected ViewHolder createBaseViewHolder(View view) {
        return new ViewHolder(view);
    }

    class ViewHolder extends AbstractBaseViewHolder<Order> {

        private static final String PLUS_SIGN = "+";
        private static final String MINUS_SIGN = "-";
        private static final String DASH_DELIMITER = " - ";

        public ViewHolder(View item_root) {
            super(item_root);
            getView(R.id.dash_line);
            getView(R.id.dot);
            getView(R.id.title);
            getView(R.id.sub_title);
            getView(R.id.amount_ico);
            getView(R.id.amount_text);
        }

        @Override
        protected void init(Context context) {
            initColors(context);
            initSizes(context);
        }

        @Override
        protected void bindObject(final Order item) {
            setOrderTitle(item);
            setSubtitle(item);
            setAmountAndIcon(item);
            updateTimeLine(item);
        }

        private void setAmountAndIcon(Order item) {
            if (item.getStatus() == Status.COMPLETED) {
                String amount = getAmountFormatted(item.getAmount());
                if (item.getOfferType() == OfferType.SPEND) {
                    setImageResource(R.id.amount_ico, R.drawable.kinecosystem_invoice);
                    setText(R.id.amount_text, MINUS_SIGN + amount);
                } else {
                    setImageResource(R.id.amount_ico, R.drawable.kinecosystem_coins);
                    setText(R.id.amount_text, PLUS_SIGN + amount);
                }
            }
        }

        private void setSubtitle(Order item) {
            StringBuilder subTitle = new StringBuilder(item.getDescription());
            String dateString = item.getCompletionDate();
            if (dateString != null && !TextUtils.isEmpty(dateString)) {
                dateString = getDateFormatted(dateString);
                if (!TextUtils.isEmpty(dateString)) {
                    subTitle.append(DASH_DELIMITER).append(dateString);
                }
            }
            setText(R.id.sub_title, subTitle);
        }

        private void setOrderTitle(Order item) {
            String brand = item.getTitle();
            String delimiter =  TextUtils.isEmpty(item.getCallToAction()) && !isFailed(item.getStatus())? "" : DASH_DELIMITER;
            String actionText = getActionText(item);
            setText(R.id.action_text, actionText);
            switch (item.getStatus()) {
                case COMPLETED:
                    if (item.getOfferType() == OfferType.SPEND) {
                        Spannable titleSpannable = new SpannableString(brand + delimiter);
                        titleSpannable.setSpan(new ForegroundColorSpan(colorBlue),
                            0, brand.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        setSpannableText(R.id.title, titleSpannable);
                        setTextColor(R.id.action_text, colorBlue);
                    } else {
                        setText(R.id.title, brand);
                    }
                    break;
                case FAILED:
                    setText(R.id.title, brand + delimiter);

                    setTextColor(R.id.action_text, colorRed);
                    break;
                default:
                    break;
            }
        }

        private boolean isFailed(Status status) {
            return status == Status.FAILED;
        }

        private String getActionText(Order item) {
            String actionText = "";
            switch (item.getStatus()) {
                case COMPLETED:
                    actionText = TextUtils.isEmpty(item.getCallToAction()) ? "" : item.getCallToAction();
                    break;
                case FAILED:
                    actionText = TRANSACTION_FAILED_MSG;
                    if(item.getError() != null) {
                        actionText = TextUtils.isEmpty(item.getError().getMessage()) ?
                            TRANSACTION_FAILED_MSG : item.getError().getMessage();
                    }
                    break;
                default:
                    break;
            }
            return actionText;
        }

        private void updateTimeLine(Order item) {
            ImageView view = getView(R.id.dot);
            LayerDrawable layerDrawable = ((LayerDrawable) view.getDrawable());
            Drawable drawable = layerDrawable.getDrawable(1);
            // Timeline dot color
            if (item.getOfferType() == OfferType.SPEND) {
                switch (item.getStatus()) {
                    case COMPLETED:
                        drawable.setColorFilter(colorBlue, PorterDuff.Mode.SRC_ATOP);
                        break;
                    case FAILED:
                        drawable.setColorFilter(colorRed, PorterDuff.Mode.SRC_ATOP);
                        break;
                    default:
                        break;
                }
            } else {
                drawable.setColorFilter(colorGrayLight, PorterDuff.Mode.SRC_ATOP);
            }

            // Timeline path size
            int itemIndex = getLayoutPosition();
            int lastIndex = getDataCount() - 1;
            if (itemIndex == lastIndex) {
                setViewHeight(R.id.dash_line, itemHalfHeight);
            } else {
                setViewHeight(R.id.dash_line, itemHeight);
            }
        }
    }
}
