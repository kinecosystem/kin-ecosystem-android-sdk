package com.kin.ecosystem.transfer.view;

import com.kin.ecosystem.Kin;
import com.kin.ecosystem.R;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;
import com.kin.ecosystem.core.data.auth.AuthRepository;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl;
import com.kin.ecosystem.core.data.order.OrderRepository;
import com.kin.ecosystem.core.network.model.IncomingTransfer;
import com.kin.ecosystem.core.network.model.OpenOrder;

import org.kinecosystem.transfer.receiver.manager.AccountInfoException;
import org.kinecosystem.transfer.receiver.presenter.IErrorActionClickListener;
import org.kinecosystem.transfer.receiver.view.AccountInfoActivityBase;
import kin.sdk.migration.MigrationManager;
import kin.sdk.migration.common.interfaces.IKinAccount;

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
        try {
            IncomingTransfer payload = new IncomingTransfer().appId("").description("").memo(memo).title("").walletAddress(fromAddress);
            final OpenOrder order = OrderRepository.getInstance().createIncomingTransferOrderSync(payload);
            android.util.Log.d("#####", "###### IncomingTransferService onTransactionCompleted " + payload.toString() + "  order " + order);
        } catch (KinEcosystemException exception) {

        }
    }

    private void initKin() throws ClientException, BlockchainException {
        if (AuthRepository.getInstance() == null || BlockchainSourceImpl.getInstance() == null) {
            Kin.initialize(getApplicationContext(), null);
            IKinAccount account = BlockchainSourceImpl.getInstance().getKinAccount();
            if (account == null) {
                final String kinUserId = AuthRepository.getInstance().getEcosystemUserID();
                MigrationManager migrationManager = Kin.createMigrationManager(getApplicationContext(),
                        AuthRepository.getInstance().getAppID());
                BlockchainSourceImpl.getInstance().setMigrationManager(migrationManager);
                BlockchainSourceImpl.getInstance().loadAccount(kinUserId);
            }
        }
    }

    private void throwExceptionOnDataError() throws AccountInfoException {
        String appName = getApplicationInfo().loadLabel(getPackageManager()).toString();
        throw new AccountInfoException(IErrorActionClickListener.ActionType.LaunchMainActivity, getString(R.string.kinecosystem_transfer_account_info_error_relogin_title, appName));
    }

    @Override
    public void updateTransactionInfo(String senderAppId, String senderAppName, String receiverAppId, String memo) {
        super.updateTransactionInfo(senderAppId, senderAppName, receiverAppId, memo);
        //server needs to check transaction got with that memo on the blockchain
        //if found add senderAppName to transaction history
        Logger.log(new Log().withTag(TAG).put(TAG, "AccountInfoActivity Validate memo " + memo + " on blockChain and if valid add to transaction history sender App Name " + senderAppName + " sender app id: " + senderAppId + " receiver ppp Id: " + receiverAppId));
    }
}
