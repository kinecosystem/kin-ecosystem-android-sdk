package com.kin.ecosystem.core.data.blockchain;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import com.kin.ecosystem.backup.BackupManager;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.KinCallbackAdapter;
import com.kin.ecosystem.common.ObservableData;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.model.Balance;
import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.bi.events.KinBalanceUpdated;
import com.kin.ecosystem.core.bi.events.SpendTransactionBroadcastToBlockchainFailed;
import com.kin.ecosystem.core.bi.events.SpendTransactionBroadcastToBlockchainSubmitted;
import com.kin.ecosystem.core.bi.events.SpendTransactionBroadcastToBlockchainSucceeded;
import com.kin.ecosystem.core.bi.events.StellarKinTrustlineSetupFailed;
import com.kin.ecosystem.core.bi.events.StellarKinTrustlineSetupSucceeded;
import com.kin.ecosystem.core.data.blockchain.CreateTrustLineCall.TrustlineCallback;
import com.kin.ecosystem.core.util.ErrorUtil;
import com.kin.ecosystem.core.util.ExecutorsUtil.MainThreadExecutor;
import java.math.BigDecimal;
import kin.core.EventListener;
import kin.core.KinAccount;
import kin.core.KinClient;
import kin.core.ListenerRegistration;
import kin.core.PaymentInfo;
import kin.core.ResultCallback;
import kin.core.TransactionId;
import kin.core.exception.CreateAccountException;
import kin.core.exception.OperationFailedException;

public class BlockchainSourceImpl implements BlockchainSource {

	private static final String TAG = BlockchainSourceImpl.class.getSimpleName();

	private static volatile BlockchainSourceImpl instance;
	private final BlockchainSource.Local local;

	private final EventLogger eventLogger;

	private final KinClient kinClient;
	private KinAccount account;
	private ObservableData<Balance> balance = ObservableData.create(new Balance());
	/**
	 * Listen for {@code completedPayment} in order to be notify about completed transaction sent to
	 * the blockchain, it could failed or succeed.
	 */
	private ObservableData<Payment> completedPayment = ObservableData.create();
	private final Object paymentObserversLock = new Object();
	private final Object balanceObserversLock = new Object();
	private int paymentObserversCount;
	private int balanceObserversCount;

	private ListenerRegistration paymentRegistration;
	private ListenerRegistration balanceRegistration;

	private final MainThreadExecutor mainThread = new MainThreadExecutor();

	private String appID;
	private static final int MEMO_FORMAT_VERSION = 1;
	private static final String MEMO_DELIMITER = "-";
	private static final String MEMO_FORMAT =
		"%d" + MEMO_DELIMITER + "%s" + MEMO_DELIMITER + "%s"; // version-appID-orderID

	private static final int APP_ID_INDEX = 1;
	private static final int ORDER_ID_INDEX = 2;
	private static final int MEMO_SPLIT_LENGTH = 3;

	private BlockchainSourceImpl(@NonNull EventLogger eventLogger, @NonNull final KinClient kinClient,
		@NonNull BlockchainSource.Local local, @NonNull BackupManager backupManager)
		throws BlockchainException {
		this.eventLogger = eventLogger;
		this.kinClient = kinClient;
		this.local = local;
		createKinAccountIfNeeded();
		initBalance();
	}

	public static void init(@NonNull EventLogger eventLogger, @NonNull final KinClient kinClient,
		@NonNull BlockchainSource.Local local, @NonNull BackupManager backupManager)
		throws BlockchainException {
		if (instance == null) {
			synchronized (BlockchainSourceImpl.class) {
				if (instance == null) {
					instance = new BlockchainSourceImpl(eventLogger, kinClient, local, backupManager);
				}
			}
		}
	}

	public static BlockchainSourceImpl getInstance() {
		return instance;
	}

	private void createKinAccountIfNeeded() throws BlockchainException {
		account = kinClient.getAccount(0);
		if (account == null) {
			try {
				account = kinClient.addAccount();
			} catch (CreateAccountException e) {
				throw ErrorUtil.getBlockchainException(e);
			}
		}
	}


	@Override
	@Nullable
	public KinAccount getKinAccount() {
		return account;
	}

	@Override
	public void setAppID(String appID) {
		if (!TextUtils.isEmpty(appID)) {
			this.appID = appID;
		}
	}

	@Override
	public void sendTransaction(@NonNull final String publicAddress, @NonNull final BigDecimal amount,
		@NonNull final String orderID, @NonNull final String offerID) {
		eventLogger.send(SpendTransactionBroadcastToBlockchainSubmitted.create(offerID, orderID));
		account.sendTransaction(publicAddress, amount, generateMemo(orderID)).run(
			new ResultCallback<TransactionId>() {
				@Override
				public void onResult(TransactionId result) {
					eventLogger
						.send(SpendTransactionBroadcastToBlockchainSucceeded.create(result.id(), offerID, orderID));
					Logger.log(new Log().withTag(TAG).put("sendTransaction onResult", result.id()));
				}

				@Override
				public void onError(Exception e) {
					eventLogger
						.send(SpendTransactionBroadcastToBlockchainFailed.create(e.getMessage(), offerID, orderID));
					completedPayment.postValue(new Payment(orderID, false, e));
					Logger.log(new Log().withTag(TAG).put("sendTransaction onError", e.getMessage()));
				}
			});
	}

	@SuppressLint("DefaultLocale")
	@VisibleForTesting
	String generateMemo(@NonNull final String orderID) {
		return String.format(MEMO_FORMAT, MEMO_FORMAT_VERSION, appID, orderID);
	}


	private void initBalance() {
		balance.postValue(getBalance());
		getBalance(new KinCallbackAdapter<Balance>() {
		});
	}

	@Override
	public Balance getBalance() {
		Balance balance = new Balance();
		balance.setAmount(new BigDecimal(local.getBalance()));
		return balance;
	}

	@Override
	public void getBalance(@Nullable final KinCallback<Balance> callback) {
		account.getBalance().run(new ResultCallback<kin.core.Balance>() {
			@Override
			public void onResult(final kin.core.Balance balanceObj) {
				setBalance(balanceObj);
				if(callback != null) {
					mainThread.execute(new Runnable() {
						@Override
						public void run() {
							callback.onResponse(balance.getValue());
						}
					});
				}
				Logger.log(new Log().withTag(TAG).put("getBalance onResult", balanceObj.value().intValue()));
			}

			@Override
			public void onError(final Exception e) {
				if(callback != null) {
					mainThread.execute(new Runnable() {
						@Override
						public void run() {
							callback.onFailure(ErrorUtil.getBlockchainException(e));
						}
					});
				}
				Logger.log(new Log().withTag(TAG).priority(Log.ERROR).put("getBalance onError", e));
			}
		});
	}

	@VisibleForTesting
	void setBalance(final kin.core.Balance balanceObj) {
		Balance balanceTemp = balance.getValue();
		// if the values are not equals so we need to update,
		// no need to update for equal values.
		if (balanceTemp.getAmount().compareTo(balanceObj.value()) != 0) {
			Logger.log(new Log().withTag(TAG).text("setBalance: Balance changed, should get update"));
			balanceTemp.setAmount(balanceObj.value());
			balance.postValue(balanceTemp);
			local.setBalance(balanceObj.value().intValue());
		}
	}

	@Override
	public void addBalanceObserver(@NonNull Observer<Balance> observer) {
		balance.addObserver(observer);
		observer.onChanged(balance.getValue());
	}

	@Override
	public void addBalanceObserverAndStartListen(@NonNull Observer<Balance> observer) {
		addBalanceObserver(observer);
		Logger.log(new Log().withTag(TAG).put("addBalanceObserverAndStartListen count", balanceObserversCount));
		incrementBalanceCount();
	}

	private void incrementBalanceCount() {
		synchronized (balanceObserversLock) {
			if (balanceObserversCount == 0) {
				startBalanceListener();
			}
			balanceObserversCount++;
		}
	}

	private void startBalanceListener() {
		Logger.log(new Log().withTag(TAG).text("startBalanceListener"));
		balanceRegistration = account.blockchainEvents()
			.addBalanceListener(new EventListener<kin.core.Balance>() {
				@Override
				public void onEvent(kin.core.Balance data) {
					final double prevBalance = balance.getValue().getAmount().doubleValue();
					setBalance(data);
					eventLogger.send(KinBalanceUpdated.create(prevBalance));
				}
			});
	}

	@Override
	public void removeBalanceObserver(@NonNull Observer<Balance> observer) {
		Logger.log(new Log().withTag(TAG).text("removeBalanceObserver"));
		balance.removeObserver(observer);
	}


	public void removeBalanceObserverAndStopListen(@NonNull Observer<Balance> observer) {
		removeBalanceObserver(observer);
		decrementBalanceCount();
	}

	private void decrementBalanceCount() {
		synchronized (balanceObserversLock) {
			if (balanceObserversCount > 0) {
				balanceObserversCount--;
			}

			if (balanceObserversCount == 0) {
				removeRegistration(balanceRegistration);
				Logger.log(new Log().withTag(TAG).text("decrementBalanceCount: removeRegistration"));
			}
		}
		Logger.log(new Log().withTag(TAG).put("decrementBalanceCount: count", balanceObserversCount));
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
		incrementPaymentCount();
	}

	private void incrementPaymentCount() {
		synchronized (paymentObserversLock) {
			if (paymentObserversCount == 0) {
				startPaymentListener();
			}
			paymentObserversCount++;
		}
	}

	private void startPaymentListener() {
		paymentRegistration = account.blockchainEvents()
			.addPaymentListener(new EventListener<PaymentInfo>() {
				@Override
				public void onEvent(PaymentInfo data) {
					final String orderID = extractOrderId(data.memo());
					Logger.log(new Log().withTag(TAG).put("startPaymentListener onEvent: the orderId", orderID)
						.put("with memo", data.memo()));
					final String accountPublicAddress = getPublicAddress();
					if (orderID != null && accountPublicAddress != null) {
						completedPayment.postValue(PaymentConverter.toPayment(data, orderID, accountPublicAddress));
						Logger.log(new Log().withTag(TAG).put("completedPayment order id", orderID));
					}
					// UpdateBalance if there is no balance sse open connection.
					if (balanceObserversCount == 0) {
						final double prevBalance = balance.getValue().getAmount().doubleValue();
						getBalance(new KinCallbackAdapter<Balance>() {
							@Override
							public void onResponse(Balance response) {
								eventLogger.send(KinBalanceUpdated.create(prevBalance));
							}
						});
					}
				}
			});
	}

	@Override
	public void removePaymentObserver(Observer<Payment> observer) {
		completedPayment.removeObserver(observer);
		decrementPaymentCount();
	}

	@Override
	public void createTrustLine(@NonNull final KinCallback<Void> callback) {
		new CreateTrustLineCall(account, new TrustlineCallback() {
			@Override
			public void onSuccess() {
				eventLogger.send(StellarKinTrustlineSetupSucceeded.create());
				mainThread.execute(new Runnable() {
					@Override
					public void run() {
						callback.onResponse(null);
					}
				});
			}

			@Override
			public void onFailure(final OperationFailedException e) {
				eventLogger.send(StellarKinTrustlineSetupFailed.create(e.getMessage()));
				mainThread.execute(new Runnable() {
					@Override
					public void run() {
						callback.onFailure(ErrorUtil.getBlockchainException(e));
					}
				});
			}
		}).start();
	}

	private void decrementPaymentCount() {
		synchronized (paymentObserversLock) {
			if (paymentObserversCount > 0) {
				paymentObserversCount--;
			}

			if (paymentObserversCount == 0) {
				removeRegistration(paymentRegistration);
			}
		}
	}

	private void removeRegistration(ListenerRegistration listenerRegistration) {
		if (listenerRegistration != null) {
			listenerRegistration.remove();
		}
	}


	@VisibleForTesting
	String extractOrderId(String memo) {
		String[] memoParts = memo.split(MEMO_DELIMITER);
		String orderID = null;
		if (memoParts.length == MEMO_SPLIT_LENGTH && memoParts[APP_ID_INDEX].equals(appID)) {
			orderID = memoParts[ORDER_ID_INDEX];
		}
		return orderID;
	}
}
