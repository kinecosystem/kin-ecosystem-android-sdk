package com.kin.ecosystem.common;


import com.kin.ecosystem.common.exception.KinEcosystemException;

public abstract class KinCallbackAdapter<T> implements KinCallback<T> {

    @Override
    public void onResponse(T response) {

    }

    @Override
    public void onFailure(KinEcosystemException exception) {

    }
}
