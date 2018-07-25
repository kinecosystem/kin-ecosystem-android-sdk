package com.kin.ecosystem.marketplace.view;

import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.marketplace.presenter.ISpendDialogPresenter;
import com.kin.ecosystem.marketplace.presenter.MarketplacePresenter;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.poll.view.PollWebViewActivity.PollBundle;
import java.util.List;

public interface IMarketplaceView extends IBaseView<MarketplacePresenter> {

	void setSpendList(List<Offer> response);

	void setEarnList(List<Offer> response);

	void showOfferActivity(PollBundle pollBundle);

	void showSpendDialog(ISpendDialogPresenter spendDialogPresenter);

	void showToast(String msg);

	void notifyEarnItemRemoved(int index);

	void notifyEarnItemInserted(int index);

	void notifySpendItemRemoved(int index);

	void notifySpendItemInserted(int index);

	void showSomethingWentWrong();

	void updateEarnSubtitle(boolean isEmpty);

	void updateSpendSubtitle(boolean isEmpty);
}
