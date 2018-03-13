package com.kin.ecosystem.base;

public interface IBaseView<T extends IBasePresenter> {

    void attachPresenter(T presenter);
}
