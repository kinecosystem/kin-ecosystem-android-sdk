package com.kin.ecosystem.common;

public interface Callback<T, E> {
    void onResponse(T response);

    void onFailure(E error);
}
