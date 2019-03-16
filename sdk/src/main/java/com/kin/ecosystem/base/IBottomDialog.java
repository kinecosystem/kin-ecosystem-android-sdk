package com.kin.ecosystem.base;

import android.support.annotation.StringRes;

public interface IBottomDialog extends IBaseView {

    void closeDialog();

    void setupImage(String image);

    void setupTitle(String titleText);

    void setupTitle(String titleText, int amount);

    void setupDescription(String descriptionText);

    void setUpButtonText(@StringRes final int stringRes);
}
