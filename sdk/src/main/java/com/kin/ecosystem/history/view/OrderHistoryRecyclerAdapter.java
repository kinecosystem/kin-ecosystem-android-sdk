package com.kin.ecosystem.history.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.kin.ecosystem.core.util.DateUtil.getDateFormatted;
import static com.kin.ecosystem.core.util.StringUtil.getAmountFormatted;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.AbstractBaseViewHolder;
import com.kin.ecosystem.base.BaseRecyclerAdapter;
import com.kin.ecosystem.core.network.model.Offer.OfferType;
import com.kin.ecosystem.core.network.model.Order;
import com.kin.ecosystem.core.network.model.Order.Status;
import com.kin.ecosystem.history.view.OrderHistoryRecyclerAdapter.ViewHolder;
import com.kin.ecosystem.widget.util.ThemeUtil;


public class OrderHistoryRecyclerAdapter extends BaseRecyclerAdapter<Order, ViewHolder> {

    private static final int NOT_INITIALIZED = -1;

    private static int colorEarn = NOT_INITIALIZED;
    private static int colorSpend = NOT_INITIALIZED;
    private static int primaryTextColor = NOT_INITIALIZED;
    private static int colorFailed = NOT_INITIALIZED;

    private static int subTitleFontSize = NOT_INITIALIZED;
    private static int itemHeight = NOT_INITIALIZED;
    private static int itemHalfHeight = NOT_INITIALIZED;

    private static String TRANSACTION_FAILED_MSG = "";

    OrderHistoryRecyclerAdapter() {
        super(R.layout.kinecosystem_order_history_recycler_item);
    }

    private void initColors(Context context) {
        if (colorEarn == NOT_INITIALIZED) {
            colorEarn = ContextCompat.getColor(context, R.color.kinecosystem_earn);
        }
        if (colorSpend == NOT_INITIALIZED) {
            colorSpend = ContextCompat.getColor(context, R.color.kinecosystem_spend);
        }
        if (primaryTextColor == NOT_INITIALIZED) {
            primaryTextColor = ThemeUtil.Companion.themeAttributeToColor(context, R.attr.primaryTextColor, R.color.kinecosystem_subtitle_order_history);
        }
        if (colorFailed == NOT_INITIALIZED) {
            colorFailed = ContextCompat.getColor(context, R.color.kinecosystem_failed);
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

    private void initStrings(Context context) {
        if(TextUtils.isEmpty(TRANSACTION_FAILED_MSG)) {
            TRANSACTION_FAILED_MSG = context.getString(R.string.kinecosystem_failed);
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

        private static final String PLUS_SIGN = "+";;
        private static final String DASH_DELIMITER = " - ";

        public ViewHolder(View item_root) {
            super(item_root);
            getView(R.id.dash_line);
            getView(R.id.kin_logo);
            getView(R.id.title);
            getView(R.id.sub_title);
            getView(R.id.amount_text);
        }

        @Override
        protected void init(Context context) {
            initColors(context);
            initSizes(context);
            initStrings(context);
        }

        @Override
        protected void bindObject(final Order item) {
            setOrderTitle(item);
            setSubtitle(item);
            setAmount(item);
            updateTimeLine(item);
        }

        private void setAmount(Order item) {
            final int itemIndex = getLayoutPosition();
            if (item.getStatus() == Status.COMPLETED) {
                String amount = getAmountFormatted(item.getAmount());
                if (isSpendOffer(item)) {
                    setTextColor(R.id.amount_text, itemIndex == 0 ? colorSpend : primaryTextColor);
                    setText(R.id.amount_text, amount);
                } else {
                    setTextColor(R.id.amount_text, itemIndex == 0 ? colorEarn : primaryTextColor);
                    setText(R.id.amount_text, PLUS_SIGN + amount);
                }
            }
        }

        private void setSubtitle(Order item) {
            String subTitle = "";
            if (!TextUtils.isEmpty(item.getDescription())) {
                subTitle = item.getDescription();
            }
            setText(R.id.sub_title, subTitle);

            StringBuilder dateString = new StringBuilder();
            if (!TextUtils.isEmpty(item.getCompletionDate())) {
                dateString.append(DASH_DELIMITER).append(getDateFormatted(item.getCompletionDate()));
            }
            setText(R.id.date, dateString);
        }

        private void setOrderTitle(Order item) {
            String title = item.getTitle();
            if(isOrderFailed(item.getStatus())) {
                setText(R.id.title, title);
                setMaxEMs(R.id.title, 9);
                setVisibility(R.id.delimiter, VISIBLE);
            } else  {
                setText(R.id.title, title);
                setMaxEMs(R.id.title, 12);
                setVisibility(R.id.delimiter, GONE);
            }
            setActionText(item);
        }

        private boolean isOrderFailed(Status status) {
            return status == Status.FAILED;
        }

        private void setActionText(Order item) {
            String actionText = "";
            if (isOrderFailed(item.getStatus())) {
                actionText = TRANSACTION_FAILED_MSG;
                if(item.getError() != null) {
                    actionText = TextUtils.isEmpty(item.getError().getMessage()) ? TRANSACTION_FAILED_MSG : item.getError().getMessage();
                }
				setTextColor(R.id.action_text, colorFailed);
            }
            setText(R.id.action_text, actionText);
        }

        private void updateTimeLine(Order item) {
            // Timeline dot color
			final int itemIndex = getLayoutPosition();
			final int lastIndex = getDataCount() - 1;
            if(itemIndex == 0) {
            	setVectorDrawable(R.id.kin_logo, isSpendOffer(item) ? R.drawable.kinecosystem_kin_spend_icon_active_small
                    : R.drawable.kinecosystem_kin_earn_icon_active_small);
			} else {
				setVectorDrawable(R.id.kin_logo, R.drawable.kinecosystem_kin_icon_inactive_small);
			}


			// Timeline path size
            if (itemIndex == 0 || itemIndex == lastIndex) {
				if (getDataCount() > 1) {
					setVisibility(R.id.dash_line, VISIBLE);
                	setViewHeight(R.id.dash_line, itemHalfHeight);
					if (itemIndex == 0) {
						setViewTopMargin(R.id.dash_line, itemHalfHeight);
					} else  {
						setViewTopMargin(R.id.dash_line, 0);
					}
				} else {
					setVisibility(R.id.dash_line, GONE);
				}

            } else {
            	setVisibility(R.id.dash_line, VISIBLE);
                setViewHeight(R.id.dash_line, itemHeight);
				setViewTopMargin(R.id.dash_line, 0);
            }
        }

		private boolean isSpendOffer(Order item) {
			return item.getOfferType() == OfferType.SPEND;
		}
	}
}
