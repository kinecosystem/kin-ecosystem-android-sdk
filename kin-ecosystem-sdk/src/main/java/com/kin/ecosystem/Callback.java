package com.kin.ecosystem;

public interface Callback<T> {
    void onResponse(T response);

    void onFailure(Throwable t);
}
