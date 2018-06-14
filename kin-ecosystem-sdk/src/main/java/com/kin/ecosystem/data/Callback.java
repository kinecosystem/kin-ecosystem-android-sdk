package com.kin.ecosystem.data;

public interface Callback<T, E> {
    void onResponse(T response);

    void onFailure(E error);
}
