package com.kin.ecosystem.base;

public class BaseDialogPresenter<T extends IBottomDialog> extends BasePresenter<T> {

    private boolean isDismissed;

    @Override
    public void onAttach(T view) {
        super.onAttach(view);
        isDismissed = false;
    }

    @Override
    public void onDetach() {
        closeDialog();
        super.onDetach();
    }

    protected void closeDialog() {
        if (getView() != null && !isDismissed) {
            isDismissed = true;
            getView().closeDialog();
        }
    }
}
