package com.kin.ecosystem.base;

public interface IBaseView<T extends BasePresenter> {

    void attachPresenter(T presenter);
}
