package com.kin.ecosystem.history.view;

import static com.kin.ecosystem.util.DateUtil.getDateFormatted;
import static com.kin.ecosystem.util.StringUtil.getAmountFormatted;

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
import com.kin.ecosystem.network.model.Order;


public class OrderHistoryRecyclerAdapter extends BaseRecyclerAdapter<Order, OrderHistoryRecyclerAdapter.ViewHolder> {

    private static int colorBlue = NOT_INITIALIZED;
    private static int colorRed = NOT_INITIALIZED;
    private static int colorOrange = NOT_INITIALIZED;
    private static int colorGrayLight = NOT_INITIALIZED;

    private static int subTitleFontSize = NOT_INITIALIZED;
    private static int itemHeight = NOT_INITIALIZED;
    private static int itemHalfHeight = NOT_INITIALIZED;

    OrderHistoryRecyclerAdapter() {
        super(R.layout.order_history_recycler_item);
        openLoadAnimation(SLIDEIN_TOP);
    }

    private void initColors(Context context) {
        if (colorBlue == NOT_INITIALIZED) {
            colorBlue = ContextCompat.getColor(context, R.color.bluePrimary);
        }
        if (colorOrange == NOT_INITIALIZED) {
            colorOrange = ContextCompat.getColor(context, R.color.orange);
        }
        if (colorRed == NOT_INITIALIZED) {
            colorRed = ContextCompat.getColor(context, R.color.red);
        }
        if (colorGrayLight == NOT_INITIALIZED) {
            colorGrayLight = ContextCompat.getColor(context, R.color.gray_light);
        }
    }

    private void initSizes(Context context) {
        Resources resources = context.getResources();
        if (subTitleFontSize == NOT_INITIALIZED) {
            subTitleFontSize = resources.getDimensionPixelSize(R.dimen.sub_title_size);
        }
        if (itemHeight == NOT_INITIALIZED) {
            itemHeight = resources.getDimensionPixelOffset(R.dimen.order_history_item_height);
            itemHalfHeight = itemHeight / 2;
        }
    }

    @Override
    protected void convert(ViewHolder holder, Order item) {
        holder.bindObject(item);
    }

    class ViewHolder extends AbstractBaseViewHolder<Order> {

        private static final String PLUS_SIGN = "+";
        private static final String MINUS_SIGN = "-";

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
        protected void bindObject(Order item) {
            setOrderTitle(item);
            setSubtitle(item);
            setAmountAndIcon(item);
            updateTimeLine(item);
        }

        private void setAmountAndIcon(Order item) {
            if (item.getStatus() == Order.StatusEnum.COMPLETED) {
                String amount = getAmountFormatted(item.getAmount());
                if (item.getOfferType() == Order.OfferTypeEnum.SPEND) {
                    setImageResource(R.id.amount_ico, R.drawable.invoice);
                    setText(R.id.amount_text, MINUS_SIGN + amount);
                } else {
                    setImageResource(R.id.amount_ico, R.drawable.coins);
                    setText(R.id.amount_text, PLUS_SIGN + amount);
                }
            }
        }

        private void setSubtitle(Order item) {
            StringBuilder subTitle = new StringBuilder(item.getDescription());
            final String delimiter = " - ";
            String dateString = item.getCompletionDate();
            if (dateString != null && !TextUtils.isEmpty(dateString)) {
                dateString = getDateFormatted(dateString);
                if (!TextUtils.isEmpty(dateString)) {
                    subTitle.append(delimiter).append(dateString);
                }
            }
            setText(R.id.sub_title, subTitle);
        }

        private void setOrderTitle(Order item) {
            String brand = item.getTitle();
            String delimiter = " - ";
            String callToAction = item.getCallToAction();
            if (item.getOfferType() == Order.OfferTypeEnum.SPEND) {
                switch (item.getStatus()) {
                    case COMPLETED:
                        Spannable titleSpannable = new SpannableString(brand + delimiter);
                        titleSpannable.setSpan(new ForegroundColorSpan(colorBlue),
                            0, brand.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        setSpannableText(R.id.title, titleSpannable);

                        setText(R.id.action_text, callToAction);
                        setTextColor(R.id.action_text, colorBlue);
                        break;
                    case PENDING:
                        setText(R.id.title, brand + delimiter);

                        setText(R.id.action_text, callToAction);
                        setTextColor(R.id.action_text, colorOrange);
                        break;
                    case FAILED:
                        setText(R.id.title, brand + delimiter);

                        setText(R.id.action_text, callToAction);
                        setTextColor(R.id.action_text, colorRed);
                        break;
                    default:
                        break;
                }
            } else {
                setText(R.id.title, brand);
            }
        }

        private void updateTimeLine(Order item) {
            ImageView view = getView(R.id.dot);
            LayerDrawable layerDrawable = ((LayerDrawable) view.getDrawable());
            Drawable drawable = layerDrawable.getDrawable(1);
            // Timeline dot color
            if (item.getOfferType() == Order.OfferTypeEnum.SPEND) {
                switch (item.getStatus()) {
                    case COMPLETED:
                        drawable.setColorFilter(colorBlue, PorterDuff.Mode.SRC_ATOP);
                        break;
                    case PENDING:
                        drawable.setColorFilter(colorOrange, PorterDuff.Mode.SRC_ATOP);
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
            int itemIndex = getData().indexOf(item);
            int lastIndex = getDataCount() - 1;
            if (itemIndex == lastIndex) {
                setViewHeight(R.id.dash_line, itemHalfHeight);
            }
        }
    }
}
