package com.kin.ecosystem.data;

import com.kin.ecosystem.KinCallback;
import com.kin.ecosystem.exception.KinEcosystemException;

public abstract class KinCallbackAdapter<T> implements KinCallback<T> {

    @Override
    public void onResponse(T response) {

    }

    @Override
    public void onFailure(KinEcosystemException exception) {

    }
}
