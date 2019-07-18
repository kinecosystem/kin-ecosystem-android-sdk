package com.kin.ecosystem.transfer.view;

import com.kin.ecosystem.Kin;
import com.kin.ecosystem.R;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.core.data.auth.AuthLocalData;
import com.kin.ecosystem.core.data.auth.AuthRepository;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl;
import com.kin.ecosystem.core.data.order.OrderRepository;
import com.kin.ecosystem.core.network.model.IncomingTransfer;

import org.kinecosystem.transfer.receiver.manager.AccountInfoException;
import org.kinecosystem.transfer.receiver.presenter.IErrorActionClickListener;
import org.kinecosystem.transfer.receiver.view.AccountInfoActivityBase;

public class AccountInfoActivity extends AccountInfoActivityBase {

    private static final String TAG = AccountInfoActivity.class.getSimpleName();

    @Override
    public String getData() throws AccountInfoException {
        try {
            initKin();
            final AuthRepository authRepository = AuthRepository.getInstance();
            final BlockchainSource blockchainSource = BlockchainSourceImpl.getInstance();
            if (authRepository != null && !authRepository.isCurrentAuthTokenExpired()
                    && blockchainSource != null) {
                return blockchainSource.getPublicAddress();
            } else {
                throwExceptionOnDataError();
            }
        } catch (BlockchainException e) {
            throwExceptionOnDataError();
        } catch (ClientException e) {
            throwExceptionOnDataError();
        }
        return null;
    }

    @Override
    public void updateTransactionInfo(final String senderAppId, final String senderAppName, final String receiverAppId, final String memo) {
        super.updateTransactionInfo(senderAppId, senderAppName, receiverAppId, memo);
        try {
            initKin();
            String title = "Received Kin";
            String description = "From " + senderAppName;
            final OrderRepository orderRepository = OrderRepository.getInstance();
            IncomingTransfer payload = new IncomingTransfer().appId(senderAppId).memo(memo).title(title).description(description).walletAddress("");
            if (orderRepository != null) {
                orderRepository.createIncomingTransferOrderAsync(payload);
            }
        } catch (ClientException e) {
        }
    }

    private void initKin() throws ClientException {
        if (AuthLocalData.getInstance(this).isLoggedIn() && (AuthRepository.getInstance() == null || BlockchainSourceImpl.getInstance() == null)) {
            Kin.initialize(this, null);
        }
    }

    private void throwExceptionOnDataError() throws AccountInfoException {
        String appName = getApplicationInfo().loadLabel(getPackageManager()).toString();
        throw new AccountInfoException(IErrorActionClickListener.ActionType.LaunchMainActivity, getString(R.string.kinecosystem_transfer_account_info_error_relogin_title, appName));
    }

}
