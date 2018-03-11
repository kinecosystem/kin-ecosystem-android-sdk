package com.kin.ecosystem.base;

public interface IBasePresenter<T extends IBaseView> {

    void onAttach(T view);

    void onDetach();
}
