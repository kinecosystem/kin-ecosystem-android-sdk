package com.kin.ecosystem.transfer.view;

import com.kin.ecosystem.Kin;
import com.kin.ecosystem.R;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;
import com.kin.ecosystem.core.data.auth.AuthRepository;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl;

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
            }
        } catch (BlockchainException e) {
            throwExceptionOnDataError();
        } catch (ClientException e) {
            throwExceptionOnDataError();
        }
        return null;
    }

    @Override
    public void updateTransactionInfo(String senderAppId, String senderAppName, String receiverAppId, String memo) {
        super.updateTransactionInfo(senderAppId, senderAppName, receiverAppId, memo);
        //server needs to check transaction got with that memo on the blockchain
        //if found add senderAppName to transaction history
        Logger.log(new Log().withTag(TAG).put(TAG, "AccountInfoActivity Validate memo " + memo + " on blockChain and if valid add to transaction history sender App Name " + senderAppName + " sender app id: " + senderAppId + " receiver ppp Id: " + receiverAppId));
    }

    private void initKin() throws ClientException {
        if (AuthRepository.getInstance() == null || BlockchainSourceImpl.getInstance() == null) {
            Kin.initialize(this, null);
        }
    }

    private void throwExceptionOnDataError() throws AccountInfoException {
        String appName = getApplicationInfo().loadLabel(getPackageManager()).toString();
        throw new AccountInfoException(IErrorActionClickListener.ActionType.LaunchMainActivity, getString(R.string.kinecosystem_transfer_account_info_error_relogin_title, appName));
    }

}
