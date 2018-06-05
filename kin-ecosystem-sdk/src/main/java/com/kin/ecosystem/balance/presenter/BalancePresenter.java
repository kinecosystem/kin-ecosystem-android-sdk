package com.kin.ecosystem.balance.presenter;

import android.support.annotation.NonNull;
import com.kin.ecosystem.balance.view.IBalanceView;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.blockchain.BlockchainSource;
import com.kin.ecosystem.util.StringUtil;

public class BalancePresenter extends BasePresenter<IBalanceView> implements IBasePresenter<IBalanceView> {

    private Observer<Integer> balanceObserver;
    private final BlockchainSource blockchainSource;

    private static final String BALANCE_ZERO_TEXT = "0.00";

    public BalancePresenter(@NonNull final BlockchainSource blockchainSource) {
        this.blockchainSource = blockchainSource;
        createBalanceObserver();
    }

    private void createBalanceObserver() {
        balanceObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer balance) {
                updateBalance(balance);
            }
        };
    }

    private void updateBalance(Integer balance) {
        String balanceString;
        if (balance == 0) {
            balanceString = BALANCE_ZERO_TEXT;
        } else {
            balanceString = StringUtil.getAmountFormatted(balance);
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
