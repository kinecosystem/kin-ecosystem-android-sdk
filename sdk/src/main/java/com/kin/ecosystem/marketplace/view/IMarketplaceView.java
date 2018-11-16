package com.kin.ecosystem.marketplace.view;

import android.support.annotation.IntDef;
import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.core.network.model.Offer;
import com.kin.ecosystem.marketplace.presenter.IMarketplacePresenter;
import com.kin.ecosystem.marketplace.presenter.ISpendDialogPresenter;
import com.kin.ecosystem.marketplace.presenter.MarketplacePresenter;
import com.kin.ecosystem.poll.view.PollWebViewActivity.PollBundle;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public interface IMarketplaceView extends IBaseView<IMarketplacePresenter> {

	int NOT_ENOUGH_KIN = 0x00000001;
	int SOMETHING_WENT_WRONG = 0x00000002;

	@IntDef({NOT_ENOUGH_KIN, SOMETHING_WENT_WRONG})
	@Retention(RetentionPolicy.SOURCE)
	@interface Message {

	}

	void setSpendList(List<Offer> response);

	void setEarnList(List<Offer> response);

	void showOfferActivity(PollBundle pollBundle);

	void showSpendDialog(ISpendDialogPresenter spendDialogPresenter);

	void showToast(@Message final int msg);

	void notifyEarnItemRemoved(int index);

	void notifyEarnItemInserted(int index);

	void notifySpendItemRemoved(int index);

	void notifySpendItemInserted(int index);

	void updateEarnSubtitle(boolean isEmpty);

	void updateSpendSubtitle(boolean isEmpty);
}
