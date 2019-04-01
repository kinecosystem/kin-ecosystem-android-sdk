package com.kin.ecosystem.marketplace.view;

import android.support.annotation.IntDef;
import com.kin.ecosystem.base.IBaseView;
import com.kin.ecosystem.core.network.model.Offer;
import com.kin.ecosystem.poll.view.PollWebViewActivity.PollBundle;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public interface IMarketplaceView extends IBaseView {

	int NOT_ENOUGH_KIN = 0x00000001;
	int SOMETHING_WENT_WRONG = 0x00000002;


	@IntDef({NOT_ENOUGH_KIN, SOMETHING_WENT_WRONG})
	@Retention(RetentionPolicy.SOURCE)
	@interface Message {

	}

	void setOfferList(List<Offer> response);

	void setupEmptyItemView();

	void showOfferActivity(PollBundle pollBundle);

	void showToast(@Message final int msg);

	void notifyOfferItemRemoved(int index);

	void notifyOfferItemInserted(int index);

	void notifyOfferItemRangRemoved(int fromIndex, int size);

	void showMenuTouchIndicator(boolean isVisible);
}
