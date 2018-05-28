package com.kin.ecosystem;

public abstract class CallbackAdapter<T> implements Callback<T> {

    @Override
    public void onResponse(T response) {

    }

    @Override
    public void onFailure(Throwable t) {

    }
}
