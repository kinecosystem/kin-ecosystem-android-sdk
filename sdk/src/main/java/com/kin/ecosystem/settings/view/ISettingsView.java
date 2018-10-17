package com.kin.ecosystem.settings.view;

import android.support.annotation.IntDef;
import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.settings.presenter.ISettingsPresenter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface ISettingsView extends IBaseView<ISettingsPresenter> {

	int ITEM_BACKUP = 0x00000001;
	int ITEM_RESTORE = 0x00000002;

	@IntDef({ITEM_BACKUP, ITEM_RESTORE})
	@Retention(RetentionPolicy.SOURCE)
	@interface Item {

	}

	int BLUE = 0x00000001;
	int GREY = 0x00000002;

	@IntDef({BLUE, GREY})
	@Retention(RetentionPolicy.SOURCE)
	@interface IconColor {

	}


	void navigateBack();

	void setIconColor(@Item final int item, @IconColor final int color);

	void changeTouchIndicatorVisibility(@Item final int item, boolean isVisible);
}
