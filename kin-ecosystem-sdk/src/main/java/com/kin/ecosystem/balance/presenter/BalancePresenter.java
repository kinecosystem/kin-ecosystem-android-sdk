package com.kin.ecosystem.balance.presenter;

import android.support.annotation.NonNull;
import com.kin.ecosystem.balance.view.IBalanceView;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.blockchain.BlockchainSource;
import com.kin.ecosystem.data.model.Balance;
import kin.ecosystem.core.util.StringUtil;

public class BalancePresenter extends BasePresenter<IBalanceView> implements IBasePresenter<IBalanceView> {

	private Observer<Balance> balanceObserver;
	private final BlockchainSource blockchainSource;

	private static final String BALANCE_ZERO_TEXT = "0.00";

	public BalancePresenter(@NonNull final BlockchainSource blockchainSource) {
		this.blockchainSource = blockchainSource;
		createBalanceObserver();
	}

	private void createBalanceObserver() {
		balanceObserver = new Observer<Balance>() {
			@Override
			public void onChanged(Balance balance) {
				updateBalance(balance);
			}
		};
	}

	private void updateBalance(Balance balance) {
		int balanceValue = balance.getAmount().intValue();
		String balanceString;
		if (balanceValue == 0) {
			balanceString = BALANCE_ZERO_TEXT;
		} else {
			balanceString = StringUtil.getAmountFormatted(balanceValue);
		}
		if (view != null) {
			view.updateBalance(balanceString);
		}
	}

	@Override
	public void onAttach(IBalanceView view) {
		super.onAttach(view);
		blockchainSource.addBalanceObserver(balanceObserver);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		blockchainSource.removeBalanceObserver(balanceObserver);
	}
}
