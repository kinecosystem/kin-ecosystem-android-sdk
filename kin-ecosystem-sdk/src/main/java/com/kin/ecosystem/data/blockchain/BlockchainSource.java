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
import java.util.concurrent.atomic.AtomicInteger;
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

    private static volatile BlockchainSource instance;

    private KinClient kinClient;
    private KinAccount account;
    private ObservableData<Integer> balance = ObservableData.create(0);
    /**
     * Listen for {@code completedPayment} in order to be notify about completed transaction sent to
     * the blockchain, it could failed or succeed.
     */
    private ObservableData<Payment> completedPayment = ObservableData.create();
    private static volatile AtomicInteger paymentObserversCount = new AtomicInteger(0);

    private PaymentWatcher paymentWatcher;
    private final MainThreadExecutor mainThread = new MainThreadExecutor();

    private String appID;
    private static final int MEMO_FORMAT_VERSION = 1;
    private static final String PASSPHRASE = "";
    private static final String MEMO_DELIMITER = "-";
    private static final String MEMO_FORMAT =
        "%d" + MEMO_DELIMITER + "%s" + MEMO_DELIMITER + "%s"; // version-appID-orderID

    private static final int APP_ID_INDEX = 1;
    private static final int ORDER_ID_INDEX = 2;
    private static final int MEMO_SPLIT_LENGTH = 3;

    private BlockchainSource(@NonNull final Context context, @NonNull final String appID)
        throws InitializeException {
        this.kinClient = new KinClient(context, StellarNetwork.NETWORK_TEST.getProvider());
        this.appID = appID;
        createKinAccountIfNeeded();
        getCurrentBalance();
    }

    public static void init(@NonNull final Context context, @NonNull final String appID) throws InitializeException {
        if (instance == null) {
            synchronized (BlockchainSource.class) {
                if (instance == null) {
                    instance = new BlockchainSource(context, appID);
                }
            }
        }
    }

    public static BlockchainSource getInstance() {
        return instance;
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

    @Override
    public void sendTransaction(@NonNull String publicAddress, @NonNull BigDecimal amount,
        @NonNull final String orderID) {
        account.sendTransaction(publicAddress, PASSPHRASE, amount, generateMemo(orderID)).run(
            new ResultCallback<TransactionId>() {
                @Override
                public void onResult(TransactionId result) {
                    Log.i(TAG, "sendTransaction onResult: " + result.id());
                }

                @Override
                public void onError(Exception e) {
                    completedPayment.setValue(new Payment(orderID, false, e.getMessage()));
                    Log.i(TAG, "sendTransaction onError: " + e.getMessage());
                }
            });
    }

    @SuppressLint("DefaultLocale")
    private String generateMemo(@NonNull final String orderID) {
        return String.format(MEMO_FORMAT, MEMO_FORMAT_VERSION, appID, orderID);
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
    public int getBalance() {
        return balance.getValue();
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
        if (paymentObserversCount.getAndIncrement() == 0) {
            startPaymentWatcher();
        }
    }

    @Override
    public void removePaymentObserver(Observer<Payment> observer) {
        completedPayment.removeObserver(observer);
        if (paymentObserversCount.decrementAndGet() == 0) {
            stopPaymentWatcher();
        }
    }

    private void startPaymentWatcher() {
        paymentWatcher = account.createPaymentWatcher();
        paymentWatcher.start(new WatcherListener<PaymentInfo>() {
            @Override
            public void onEvent(PaymentInfo data) {
                String orderID = extractOrderId(data.memo());
                if (orderID != null) {
                    completedPayment.setValue(new Payment(orderID, data.hash().id()));
                    Log.i(TAG, "completedPayment order id: " + orderID);
                }
                updateBalance(data);
            }
        });
    }

    private void stopPaymentWatcher() {
        if (paymentWatcher != null) {
            paymentWatcher.stop();
            paymentWatcher = null;
        }
    }

    private String extractOrderId(String memo) {
        String[] memoParts = memo.split(MEMO_DELIMITER);
        String orderID = null;
        if (memoParts.length == MEMO_SPLIT_LENGTH && memoParts[APP_ID_INDEX].equals(appID)) {
            orderID = memoParts[ORDER_ID_INDEX];
        }
        return orderID;
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
}
