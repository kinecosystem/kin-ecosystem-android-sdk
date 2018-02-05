package com.kin.ecosystem.base;

import android.content.Context;
import android.view.View;

public abstract class AbstractBaseViewHolder<T> extends BaseViewHolder<T> {

    public AbstractBaseViewHolder(View view) {
        super(view);
        initSizes(view.getContext());
    }

    protected abstract void initSizes(Context context);

    protected abstract void bindObject(T item);
}
