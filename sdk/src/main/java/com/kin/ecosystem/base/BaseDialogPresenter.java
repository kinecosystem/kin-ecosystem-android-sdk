package com.kin.ecosystem.base;

public class BaseDialogPresenter<T extends IBottomDialog> extends BasePresenter<T> {

    protected boolean isDismissed;

    @Override
    public void onAttach(T view) {
        super.onAttach(view);
        isDismissed = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isDismissed = true;
    }

    protected void closeDialog() {
        if (view != null && !isDismissed) {
            isDismissed = true;
            view.closeDialog();
        }
    }
}
