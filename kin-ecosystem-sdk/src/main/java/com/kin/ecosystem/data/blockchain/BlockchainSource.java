package com.kin.ecosystem.data.blockchain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.base.ObservableData;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.model.Payment;
import com.kin.ecosystem.exception.InitializeException;
import com.kin.ecosystem.util.ExecutorsUtil.MainThreadExecutor;
import java.math.BigDecimal;
import kin.core.Balance;
import kin.core.KinAccount;
import kin.core.KinClient;
import kin.core.PaymentInfo;
import kin.core.PaymentWatcher;
import kin.core.ResultCallback;
import kin.core.TransactionId;
import kin.core.WatcherListener;
import kin.core.exception.CreateAccountException;

public class BlockchainSource implements IBlockchainSource {

    private static final String TAG = BlockchainSource.class.getSimpleName();

    private static BlockchainSource instance;

    private KinClient kinClient;
    private KinAccount account;
    private ObservableData<Integer> balance = ObservableData.create(0);
    /**
     * Listen for {@code completedPayment} in order to be notify about completed transaction sent to
     * the blockchain, it could failed or succeed.
     */
    private ObservableData<Payment> completedPayment = ObservableData.create();

    private final MainThreadExecutor mainThread = new MainThreadExecutor();

    private String appID;
    private static final int VERSION = 1;
    private static final String PASSPHRASE = "";
    private static final String MEMO_FORMAT = "%d-%s-%s"; // version-appID-orderID

    private BlockchainSource(@NonNull final Context context, @NonNull final String appID)
        throws InitializeException {
        this.kinClient = new KinClient(context, StellarNetwork.NETWORK_TEST.getProvider());
        this.appID = appID;
        createKinAccountIfNeeded();
        listenForBalanceUpdates();
        getCurrentBalance();
    }

    private void createKinAccountIfNeeded() throws InitializeException {
        account = kinClient.getAccount(0);
        if (account == null) {
            try {
                account = kinClient.addAccount(""); // blockchain-sdk should generate and take care of that passphrase.
            } catch (CreateAccountException e) {
                throw new InitializeException(e.getMessage());
            }
        }
    }

    private void listenForBalanceUpdates() {
        PaymentWatcher paymentWatcher = account.createPaymentWatcher();
        paymentWatcher.start(new WatcherListener<PaymentInfo>() {
            @Override
            public void onEvent(PaymentInfo data) {
                updateBalance(data);
            }
        });
    }

    private void updateBalance(PaymentInfo data) {
        int balanceAmount = balance.getValue();
        if (data.sourcePublicKey().equals(account.getPublicAddress())) {
            int spendAmount = data.amount().intValue();
            balanceAmount -= spendAmount;
            Log.i(TAG, "updateBalance: Spend " + spendAmount);
        } else {
            int earnAmount = data.amount().intValue();
            balanceAmount += earnAmount;
            Log.i(TAG, "updateBalance: Earn " + earnAmount);
        }
        balance.setValue(balanceAmount);
    }

    public static void init(@NonNull final Context context, @NonNull final String appID) throws InitializeException {
        if (instance == null) {
            synchronized (BlockchainSource.class) {
                instance = new BlockchainSource(context, appID);
            }
        }
    }

    public static BlockchainSource getInstance() {
        return instance;
    }

    @Override
    public void sendTransaction(@NonNull String publicAddress, @NonNull BigDecimal amount,
        @NonNull final String orderID) {
        account.sendTransaction(publicAddress, PASSPHRASE, amount, generateMemo(orderID)).run(
            new ResultCallback<TransactionId>() {
                @Override
                public void onResult(TransactionId result) {
                    completedPayment.setValue(new Payment(orderID, result.id()));
                    Log.i(TAG, "onResult: " + result.id());
                }

                @Override
                public void onError(Exception e) {
                    completedPayment.setValue(new Payment(orderID, false, e.getMessage()));
                    Log.i(TAG, "onError: " + e.getMessage());
                }
            });
    }

    @SuppressLint("DefaultLocale")
    private String generateMemo(@NonNull final String orderID) {
        return String.format(MEMO_FORMAT, VERSION, appID, orderID);
    }


    private void getCurrentBalance() {
        getBalance(new Callback<Integer>() {
            @Override
            public void onResponse(Integer response) {
                Log.i(TAG, "getCurrentBalance onResponse: " + response);
                balance.setValue(response);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(TAG, "getCurrentBalance onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public void getBalance(@NonNull final Callback<Integer> callback) {
        account.getBalance().run(new ResultCallback<Balance>() {
            @Override
            public void onResult(final Balance balanceObj) {
                mainThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(balanceObj.value().intValue());
                    }
                });
                Log.i(TAG, "getCurrentBalance onResult: " + balanceObj.value().intValue());
            }

            @Override
            public void onError(final Exception e) {
                mainThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(e);
                    }
                });
                Log.i(TAG, "getCurrentBalance onError: " + e.getMessage());
            }
        });
    }

    @Override
    public void addBalanceObserver(@NonNull Observer<Integer> observer) {
        balance.addObserver(observer);
        observer.onChanged(balance.getValue());
    }

    @Override
    public void removeBalanceObserver(@NonNull Observer<Integer> observer) {
        balance.removeObserver(observer);
    }

    @Override
    public void createTrustLine() {
        account.activate(PASSPHRASE).run(new ResultCallback<Void>() {
            @Override
            public void onResult(Void result) {
                System.out.println("ACTIVATE >>> createTrustLine");
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    @Override
    public String getPublicAddress() {
        if (account == null) {
            return null;
        }
        return account.getPublicAddress();
    }

    @Override
    public void addPaymentObservable(Observer<Payment> observer) {
        completedPayment.addObserver(observer);
    }

    @Override
    public void removePaymentObserver(Observer<Payment> observer) {
        completedPayment.removeObserver(observer);
    }
}
