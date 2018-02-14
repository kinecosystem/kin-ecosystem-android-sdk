package com.kin.ecosystem.history.view;

import android.content.Context;
import android.view.View;

import com.kin.ecosystem.R;
import com.kin.ecosystem.base.AbstractBaseViewHolder;
import com.kin.ecosystem.base.BaseRecyclerAdapter;
import com.kin.ecosystem.network.model.Order;

import java.text.NumberFormat;
import java.util.Locale;


public class OrderHistoryRecyclerAdapter extends BaseRecyclerAdapter<Order, OrderHistoryRecyclerAdapter.ViewHolder> {

    public OrderHistoryRecyclerAdapter() {
        super(R.layout.order_history_recycler_item);
    }

    @Override
    protected void convert(ViewHolder holder, Order item) {
        holder.bindObject(item);
    }

    class ViewHolder extends AbstractBaseViewHolder<Order> {


        public ViewHolder(View item_root) {
            super(item_root);
            getView(R.id.time_line_container);
            getView(R.id.first_dash_line);
            getView(R.id.dot);
            getView(R.id.sec_dash_line);
            getView(R.id.title);
            getView(R.id.sub_title);
            getView(R.id.amount_container);
            getView(R.id.amount_ico);
            getView(R.id.amount_text);

        }

        @Override
        protected void initSizes(Context context) {

        }

        @Override
        protected void bindObject(Order item) {
            //TODO change text colors accordingly
            setText(R.id.title, item.getTitle() + " - " + item.getCallToAction());

            //TODO add date with formatted string
            setText(R.id.sub_title, item.getDescription());

            String amount = NumberFormat.getNumberInstance(Locale.US).format(item.getAmount());
            setText(R.id.amount_text, item.getOfferType() == Order.OfferTypeEnum.SPEND ? "-" + amount : amount);

            if (item.getStatus() == Order.StatusEnum.COMPLETED) {
                if (item.getOfferType() == Order.OfferTypeEnum.SPEND) {
                    setImageResource(R.id.amount_ico, R.drawable.invoice);
                } else {
                    setImageResource(R.id.amount_ico, R.drawable.coins);
                }
            }
        }
    }
}
