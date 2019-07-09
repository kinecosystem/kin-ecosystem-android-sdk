package com.kin.ecosystem.transfer.view;

import android.util.Log;

import com.kin.ecosystem.Kin;
import com.kin.ecosystem.R;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.core.data.auth.AuthRepository;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl;
import com.kin.ecosystem.core.data.order.OrderRepository;
import com.kin.ecosystem.core.network.model.IncomingTransfer;
import com.kin.ecosystem.core.network.model.OpenOrder;

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
    public void updateTransactionInfo(final String senderAddress, final String senderAppId, final String senderAppName, final String receiverAppId, final String memo) {
        //on main thread
        try {
            initKin();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    final OrderRepository orderRepository = OrderRepository.getInstance();
                    IncomingTransfer payload = new IncomingTransfer().appId(senderAppId).description("some new descriotion2").memo(memo).title("some new title2").walletAddress("");
                    Log.d("####", "#### update senderAddress " + senderAddress);
                    Log.d("####", "#### updateTransactionInfo memo " + memo);

                    Log.d("####", "#### update data " + orderRepository);
                    if (orderRepository != null) {
                        try {
                            OpenOrder order = orderRepository.createIncomingTransferOrderSync(payload);
                            android.util.Log.d("#####", "###### updateTransactionInfo OpenOrder order " + order);
                        } catch (KinEcosystemException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();

        } catch (KinEcosystemException exception) {
            android.util.Log.d("#####", "###### KinEcosystemException 5 "+ exception.getMessage());

        }
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
