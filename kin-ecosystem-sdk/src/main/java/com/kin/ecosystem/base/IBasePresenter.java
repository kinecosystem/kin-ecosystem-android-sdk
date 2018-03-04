package com.kin.ecosystem.base;

public interface IBasePresenter<T> {

    void onAttach(T view);

    void onDetach();
}
