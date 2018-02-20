package com.kin.ecosystem.base;

import android.content.Context;
import android.view.View;

public abstract class AbstractBaseViewHolder<T> extends BaseViewHolder<T> {

    public AbstractBaseViewHolder(View view) {
        super(view);
        init(view.getContext());
    }

    protected abstract void init(Context context);

    protected abstract void bindObject(T item);
}
