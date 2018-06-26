package com.kin.ecosystem.marketplace.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.chad.library.adapter.base.BaseRecyclerAdapter;
import com.chad.library.adapter.base.BaseRecyclerAdapter.OnItemClickListener;
import com.kin.ecosystem.R;
import com.kin.ecosystem.exception.ClientException;
import com.kin.ecosystem.marketplace.presenter.IMarketplacePresenter;
import com.kin.ecosystem.marketplace.presenter.ISpendDialogPresenter;
import com.kin.ecosystem.marketplace.presenter.MarketplacePresenter;
import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.Offer.OfferType;
import com.kin.ecosystem.poll.view.PollWebViewActivity;
import com.kin.ecosystem.poll.view.PollWebViewActivity.PollBundle;
import java.util.List;


public class MarketplaceFragment extends Fragment implements IMarketplaceView {

	public static MarketplaceFragment newInstance() {
		return new MarketplaceFragment();
	}

	private IMarketplacePresenter marketplacePresenter;

	private SpendRecyclerAdapter spendRecyclerAdapter;
	private EarnRecyclerAdapter earnRecyclerAdapter;
	private OffersEmptyView spendEmptyView;
	private OffersEmptyView earnEmptyView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
		@Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.kinecosystem_fragment_marketplce, container, false);
		initViews(root);
		return root;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		marketplacePresenter.onAttach(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		marketplacePresenter.getOffers();

	}

	@Override
	public void attachPresenter(MarketplacePresenter presenter) {
		marketplacePresenter = presenter;
	}

	protected void initViews(View root) {
		//Space item decoration for both of the recyclers
		int margin = getResources().getDimensionPixelOffset(R.dimen.kinecosystem_main_margin);
		int space = getResources().getDimensionPixelOffset(R.dimen.kinecosystem_offer_item_list_space);
		SpaceItemDecoration itemDecoration = new SpaceItemDecoration(margin, space);

		//Spend Recycler
		RecyclerView spendRecycler = root.findViewById(R.id.spend_recycler);
		spendRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
		spendRecycler.addItemDecoration(itemDecoration);
		spendRecyclerAdapter = new SpendRecyclerAdapter(getContext());
		spendRecyclerAdapter.bindToRecyclerView(spendRecycler);
		spendRecyclerAdapter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(BaseRecyclerAdapter adapter, View view, int position) {
				marketplacePresenter.onItemClicked(position, OfferType.SPEND);
			}
		});

		//Earn Recycler
		RecyclerView earnRecycler = root.findViewById(R.id.earn_recycler);
		earnRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
		earnRecycler.addItemDecoration(itemDecoration);
		earnRecyclerAdapter = new EarnRecyclerAdapter(getContext());
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

//	@Override
//	public void navigateToOrderHistory() {
//		marketplacePresenter.
//		Intent orderHistory = new Intent(this, OrderHistoryActivity.class);
//		navigateToActivity(orderHistory);
//	}

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
	public void showToast(String msg) {
		Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
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
	public void showSomethingWentWrong() {
		showToast(getString(R.string.kinecosystem_something_went_wrong));
	}

	@Override
	public void setEarnEmptyView() {
		earnEmptyView = createEmptyView(earnEmptyView);
		earnRecyclerAdapter.setEmptyView(earnEmptyView);
	}


	@Override
	public void setSpendEmptyView() {
		spendEmptyView = createEmptyView(spendEmptyView);
		spendRecyclerAdapter.setEmptyView(spendEmptyView);
	}

	private OffersEmptyView createEmptyView(OffersEmptyView emptyView) {
		if (emptyView == null) {
			emptyView = new OffersEmptyView(getContext());
		}
		return emptyView;
	}
}
