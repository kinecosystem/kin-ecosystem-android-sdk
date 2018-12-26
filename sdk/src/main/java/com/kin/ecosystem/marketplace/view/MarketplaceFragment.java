package com.kin.ecosystem.marketplace.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.BaseRecyclerAdapter;
import com.kin.ecosystem.base.BaseRecyclerAdapter.OnItemClickListener;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.core.network.model.Offer;
import com.kin.ecosystem.core.network.model.Offer.OfferType;
import com.kin.ecosystem.marketplace.presenter.IMarketplacePresenter;
import com.kin.ecosystem.marketplace.presenter.ISpendDialogPresenter;
import com.kin.ecosystem.poll.view.PollWebViewActivity;
import com.kin.ecosystem.poll.view.PollWebViewActivity.PollBundle;
import java.util.List;


public class MarketplaceFragment extends Fragment implements IMarketplaceView {


	public static MarketplaceFragment newInstance() {
		return new MarketplaceFragment();
	}

	private IMarketplacePresenter marketplacePresenter;

	private TextView spendSubTitle;
	private TextView earnSubTitle;
	private SpendRecyclerAdapter spendRecyclerAdapter;
	private EarnRecyclerAdapter earnRecyclerAdapter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
		@Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.kinecosystem_fragment_marketplce, container, false);
		initViews(root);
		marketplacePresenter.onAttach(this);
		return root;
	}

	@Override
	public void onStart() {
		super.onStart();
		marketplacePresenter.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
		marketplacePresenter.onStop();
	}

	@Override
	public void onDestroyView() {
		if(marketplacePresenter != null) {
			marketplacePresenter.onDetach();
		}
		super.onDestroyView();
	}

	@Override
	public void attachPresenter(IMarketplacePresenter presenter) {
		marketplacePresenter = presenter;
	}

	protected void initViews(View root) {
		spendSubTitle = root.findViewById(R.id.spend_subtitle);
		earnSubTitle = root.findViewById(R.id.earn_subtitle);

		//Space item decoration for both of the recyclers
		int margin = getResources().getDimensionPixelOffset(R.dimen.kinecosystem_main_margin);
		int space = getResources().getDimensionPixelOffset(R.dimen.kinecosystem_offer_item_list_space);
		SpaceItemDecoration itemDecoration = new SpaceItemDecoration(margin, space);

		//Spend Recycler
		RecyclerView spendRecycler = root.findViewById(R.id.spend_recycler);
		spendRecycler.setLayoutManager(new HorizontalLayoutManager(getContext()));
		spendRecycler.addItemDecoration(itemDecoration);
		spendRecyclerAdapter = new SpendRecyclerAdapter();
		spendRecyclerAdapter.bindToRecyclerView(spendRecycler);
		spendRecyclerAdapter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(BaseRecyclerAdapter adapter, View view, int position) {
				marketplacePresenter.onItemClicked(position, OfferType.SPEND);
			}
		});

		//Earn Recycler
		RecyclerView earnRecycler = root.findViewById(R.id.earn_recycler);
		earnRecycler.setLayoutManager(new HorizontalLayoutManager(getContext()));
		earnRecycler.addItemDecoration(itemDecoration);
		earnRecyclerAdapter = new EarnRecyclerAdapter();
		earnRecyclerAdapter.bindToRecyclerView(earnRecycler);
		earnRecyclerAdapter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(BaseRecyclerAdapter adapter, View view, int position) {
				marketplacePresenter.onItemClicked(position, OfferType.EARN);
			}
		});

	}


	@Override
	public void setSpendList(List<Offer> spendList) {
		spendRecyclerAdapter.setNewData(spendList);
	}

	@Override
	public void setEarnList(List<Offer> earnList) {
		earnRecyclerAdapter.setNewData(earnList);
	}

	@Override
	public void setupEmptyItemView() {
		spendRecyclerAdapter.setEmptyView(new OffersEmptyView(getContext()));
		earnRecyclerAdapter.setEmptyView(new OffersEmptyView(getContext()));
	}

	@Override
	public void showOfferActivity(PollBundle pollBundle) {
		try {
			Intent intent = PollWebViewActivity.createIntent(getContext(), pollBundle);
			startActivity(intent);
			getActivity()
				.overridePendingTransition(R.anim.kinecosystem_slide_in_right, R.anim.kinecosystem_slide_out_left);
		} catch (ClientException e) {
			marketplacePresenter.showOfferActivityFailed();
		}

	}

	@Override
	public void showSpendDialog(ISpendDialogPresenter spendDialogPresenter) {
		SpendDialog spendDialog = new SpendDialog(getActivity(), marketplacePresenter.getNavigator(),
			spendDialogPresenter);
		spendDialog.show();
	}

	@Override
	public void showToast(@Message final int msg) {
		Toast.makeText(getContext(), getMessageResId(msg), Toast.LENGTH_SHORT).show();
	}

	private @StringRes int getMessageResId(@Message final int msg) {
		switch (msg) {
			case NOT_ENOUGH_KIN:
				return R.string.kinecosystem_you_dont_have_enough_kin;
			case SOMETHING_WENT_WRONG:
			default:
				return R.string.kinecosystem_something_went_wrong;
		}
	}

	@Override
	public void notifyEarnItemRemoved(int index) {
		earnRecyclerAdapter.notifyItemRemoved(index);
	}

	@Override
	public void notifyEarnItemInserted(int index) {
		earnRecyclerAdapter.notifyItemInserted(index);
	}

	@Override
	public void notifySpendItemRemoved(int index) {
		spendRecyclerAdapter.notifyItemRemoved(index);
	}

	@Override
	public void notifySpendItemInserted(int index) {
		spendRecyclerAdapter.notifyItemInserted(index);
	}

	@Override
	public void notifySpendItemRangRemoved(int fromIndex, int size) {
		spendRecyclerAdapter.notifyItemRangeRemoved(fromIndex, size);
	}

	@Override
	public void notifyEarnItemRangRemoved(int fromIndex, int size) {
		earnRecyclerAdapter.notifyItemRangeRemoved(fromIndex, size);
	}

	@Override
	public void updateEarnSubtitle(boolean isEmpty) {
		earnSubTitle.setText(isEmpty ? R.string.kinecosystem_empty_tomorrow_more_opportunities
			: R.string.kinecosystem_complete_tasks_and_earn_kin);
	}

	@Override
	public void updateSpendSubtitle(boolean isEmpty) {
		spendSubTitle.setText(isEmpty ? R.string.kinecosystem_empty_tomorrow_more_opportunities
			: R.string.kinecosystem_use_your_kin_to_enjoy_stuff_you_like);
	}
}
