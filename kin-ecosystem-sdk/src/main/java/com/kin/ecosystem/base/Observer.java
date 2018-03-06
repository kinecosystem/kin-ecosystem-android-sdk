package com.kin.ecosystem.base;

public abstract class  Observer<T> {

    public abstract void onChanged(T value);
}
