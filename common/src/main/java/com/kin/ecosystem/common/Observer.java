package com.kin.ecosystem.common;

public abstract class  Observer<T> {

    public abstract void onChanged(T value);
}
