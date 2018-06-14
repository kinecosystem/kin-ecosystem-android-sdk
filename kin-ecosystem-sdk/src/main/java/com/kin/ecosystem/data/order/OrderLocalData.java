package com.kin.ecosystem.data.order;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.kin.ecosystem.data.Callback;
import com.kin.ecosystem.util.ExecutorsUtil;

public class OrderLocalData implements OrderDataSource.Local {

    private static volatile OrderLocalData instance;

    private static final String ORDERS_PREF_NAME_FILE_KEY = "kinecosystem_orders_pref";

    private static final String IS_FIRST_SPEND_ORDER_KEY = "is_first_spend_order_key";

    private final SharedPreferences ordersSharedPreferences;
    private final ExecutorsUtil executorsUtil;

    private OrderLocalData(@NonNull final Context context, @NonNull ExecutorsUtil executorsUtil) {
        this.ordersSharedPreferences = context.getSharedPreferences(ORDERS_PREF_NAME_FILE_KEY, Context.MODE_PRIVATE);
        this.executorsUtil = executorsUtil;
    }

    public static OrderLocalData getInstance(@NonNull final Context context, @NonNull ExecutorsUtil executorsUtil) {
        if (instance == null) {
            synchronized (OrderLocalData.class) {
                if (instance == null) {
                    instance = new OrderLocalData(context, executorsUtil);
                }
            }
        }
        return instance;
    }

    @Override
    public void isFirstSpendOrder(@NonNull final Callback<Boolean, Void> callback) {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                final boolean isFirstSpendOrder = ordersSharedPreferences.getBoolean(IS_FIRST_SPEND_ORDER_KEY, true);
                executorsUtil.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(isFirstSpendOrder);
                    }
                });
            }
        };
        executorsUtil.diskIO().execute(command);
    }

    @Override
    public void setIsFirstSpendOrder(boolean isFirstSpendOrder) {
        ordersSharedPreferences.edit().putBoolean(IS_FIRST_SPEND_ORDER_KEY, isFirstSpendOrder).apply();
    }
}
