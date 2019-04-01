package com.kin.ecosystem.core.data.blockchain;

import static com.kin.ecosystem.core.data.blockchain.BlockchainSourceLocal.NOT_EXIST;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.ObservableData;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.ClientException;
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
import com.kin.ecosystem.core.data.auth.AuthDataSource;
import com.kin.ecosystem.core.data.blockchain.CreateTrustLineCall.TrustlineCallback;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.MigrationInfo;
import com.kin.ecosystem.core.util.ErrorUtil;
import com.kin.ecosystem.core.util.ExecutorsUtil.MainThreadExecutor;
import com.kin.ecosystem.core.util.StringUtil;
import com.kin.ecosystem.recovery.KeyStoreProvider;
import java.math.BigDecimal;
import kin.sdk.migration.MigrationManager;
import kin.sdk.migration.common.KinSdkVersion;
import kin.sdk.migration.common.WhitelistResult;
import kin.sdk.migration.common.exception.CreateAccountException;
import kin.sdk.migration.common.exception.MigrationInProcessException;
import kin.sdk.migration.common.exception.OperationFailedException;
import kin.sdk.migration.common.exception.WhitelistTransactionFailedException;
import kin.sdk.migration.common.interfaces.IBalance;
import kin.sdk.migration.common.interfaces.IEventListener;
import kin.sdk.migration.common.interfaces.IKinAccount;
import kin.sdk.migration.common.interfaces.IKinClient;
import kin.sdk.migration.common.interfaces.IListenerRegistration;
import kin.sdk.migration.common.interfaces.IMigrationManagerCallbacks;
import kin.sdk.migration.common.interfaces.IPaymentInfo;
import kin.sdk.migration.common.interfaces.ITransactionId;
import kin.sdk.migration.common.interfaces.IWhitelistService;
import kin.sdk.migration.common.interfaces.IWhitelistableTransaction;
import kin.utils.ResultCallback;

public class BlockchainSourceImpl implements BlockchainSource {
	private static final String TAG = BlockchainSourceImpl.class.getSimpleName();

	private static volatile BlockchainSourceImpl instance;
	private final BlockchainSource.Local local;
	private final BlockchainSource.Remote remote;

	private final EventLogger eventLogger;
	private final AuthDataSource authRepository;

	private MigrationManager migrationManager;
	private IKinClient kinClient;
	private IKinAccount account;
	private String currentUserId;

	private ObservableData<Balance> balance = ObservableData.create(new Balance());
	/**
	 * Listen for {@code completedPayment} in order to be notify about completed transaction sent to the blockchain, it
	 * could failed or succeed.
	 */
	private ObservableData<Payment> completedPayment = ObservableData.create();
	private final Object paymentObserversLock = new Object();
	private final Object balanceObserversLock = new Object();
	private int paymentObserversCount;
	private int balanceObserversCount;

	private AccountCreationRequest accountCreationRequest;
	private IListenerRegistration paymentRegistration;
	private IListenerRegistration balanceRegistration;

	private final MainThreadExecutor mainThread = new MainThreadExecutor();

	private String appID;

	// Memo format version-appID-orderID
	private static final String MEMO_DELIMITER = "-";
	private static final int APP_ID_INDEX = 1;
	private static final int ORDER_ID_INDEX = 2;
	private static final int MEMO_SPLIT_LENGTH = 3;

	private BlockchainSourceImpl(@NonNull EventLogger eventLogger, @NonNull Local local, @NonNull Remote remote,
		@NonNull AuthDataSource authRepository) {
		this.eventLogger = eventLogger;
		this.authRepository = authRepository;
		this.local = local;
		this.remote = remote;
		Logger.log(new Log().withTag(TAG)
			.put("BlockchainSourceImpl authRepository.getEcosystemUserID()", authRepository.getEcosystemUserID()));
		this.currentUserId = authRepository.getEcosystemUserID();
		this.appID = authRepository.getAppID();
	}

	public static void init(@NonNull EventLogger eventLogger, @NonNull Local local, @NonNull Remote remote,
		@NonNull AuthDataSource authDataSource) {
		if (instance == null) {
			synchronized (BlockchainSourceImpl.class) {
				if (instance == null) {
					instance = new BlockchainSourceImpl(eventLogger, local, remote, authDataSource);
				}
			}
		}
	}

	public static BlockchainSourceImpl getInstance() {
		return instance;
	}

	@Override
	public void setMigrationManager(@NonNull final MigrationManager migrationManager) {
		this.migrationManager = migrationManager;
		// If no account has been found then  it doesn't really matter which version because we only need it to check
		// if there are any accounts. Later on it will be updated according to the version that we got from the server.
		KinSdkVersion sdkVersion = KinSdkVersion.OLD_KIN_SDK;
		if (kinClient != null && kinClient.hasAccount() && account != null) {
			sdkVersion = account.getKinSdkVersion();
		}
		updateKinClient(migrationManager.getKinClient(sdkVersion));
	}

	private void updateKinClient(IKinClient kinClient) {
		this.kinClient = kinClient;
	}

	public void startMigrationProcess(final MigrationProcessListener listener) {
		// Check if we have an account, if yes then get the migration info and check if this account should migrate.
		// If the account should migrate then migrate it, if not update the kinClient to run on this version.
		// If we don't have an account then check the server for the current version and update the kinClient to run on this version.
		if (kinClient.hasAccount()) {
//			try {
//				final String publicAddress = getPublicAddress();
			// TODO: 01/04/2019 when login will happen before then change it back to be  getPublicAddress().
				final String publicAddress = kinClient.getAccount(0).getPublicAddress();
				remote.getMigrationInfo(publicAddress,
					new Callback<MigrationInfo, ApiException>() {
						@Override
						public void onResponse(MigrationInfo migrationInfo) {
							KinSdkVersion kinSdkVersion = KinSdkVersion.get(migrationInfo.getBlockchainVersion());
							local.setBlockchainVersion(kinSdkVersion); // In any case update the version locally.
							if (migrationInfo.shouldMigrate()) {
								startMigration(publicAddress, listener);
							} else {
								updateKinClient(
									migrationManager.getKinClient(kinSdkVersion));
								listener.onMigrationEnd();
							}

						}

						@Override
						public void onFailure(ApiException exception) {
							// TODO: 31/03/2019 handle error like in any other place in the app, find what kind of error...
							// TODO: 01/04/2019 and if got account not found, which probably means that the account is only created locally then handle it.
						}
					});
//			} catch (BlockchainException e) {
//				// TODO: 31/03/2019 handle error like in any other place in the app in this stage - this can happen if, for example, account is not found or something like that.
//			}
		} else {
			remote.getBlockchainVersion(new Callback<KinSdkVersion, ApiException>() {
				@Override
				public void onResponse(KinSdkVersion sdkVersion) {
					updateKinClient(migrationManager.getKinClient(sdkVersion));
					listener.onMigrationEnd();
				}

				@Override
				public void onFailure(ApiException exception) {
					// TODO: 31/03/2019 handle error like in any other place in the app, find what kind of error...
				}
			});
		}
	}

	private void startMigration(String publicAddress, final MigrationProcessListener listener) {
		try {
			migrationManager.start(publicAddress, new IMigrationManagerCallbacks() {
				@Override
				public void onMigrationStart() {
					if (listener != null) {
						listener.onMigrationStart();
					}
				}

				@Override
				public void onReady(IKinClient kinClient) {
					updateKinClient(kinClient);
					if (listener != null) {
						listener.onMigrationEnd();
					}
				}

				@Override
				public void onError(Exception e) {
					if (listener != null) {
						listener.onMigrationError(new BlockchainException(BlockchainException.MIGRATION_FAILED, "Migration Failed", e));
					}
				}
			});
		} catch (MigrationInProcessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadAccount(String kinUserId) throws BlockchainException {
		migrateToMultipleUsers(kinUserId);
		createOrLoadAccount(kinUserId);
		initBalance();
	}

	/**
	 * Support backward compatibility,
	 * load the old account and migrate to current multiple users implementation.
	 */
	private void migrateToMultipleUsers(String kinUserId) {
		if (!local.getIsMigrated()) {
			local.setDidMigrate();
			if (kinClient.hasAccount()) {
				final int accountIndex = local.getAccountIndex();
				IKinAccount account;
				if (accountIndex == NOT_EXIST) {
					account = kinClient.getAccount(0);
					Logger.log(new Log().withTag(TAG)
						.put("migrateToMultipleUsers accountIndex == NOT_EXIST, kinUserId", kinUserId));
				} else {
					Logger.log(new Log().withTag(TAG).put("migrateToMultipleUsers accountIndex", accountIndex)
						.put("kinUserId", kinUserId));
					account = kinClient.getAccount(accountIndex);
					local.removeAccountIndexKey();
				}
				local.setActiveUserWallet(kinUserId, account.getPublicAddress());
			}
		}
	}

	private void createOrLoadAccount(String kinUserId) throws BlockchainException {
		final String lastWalletAddress = local.getLastWalletAddress(kinUserId);
		if (kinClient.hasAccount() && !StringUtil.isEmpty(lastWalletAddress)) {
			Logger.log(new Log().withTag(TAG).text("createOrLoadAccount").put("currentUserId", currentUserId)
				.put("kinUserId", kinUserId));

			// Match between last wallet address to wallets on device.
			for (int i = 0; i < kinClient.getAccountCount(); i++) {
				IKinAccount account = kinClient.getAccount(i);
				if (lastWalletAddress.equals(account.getPublicAddress())) {
					this.account = account;
					break;
				}
			}

			// No matching found
			if (account == null) {
				Logger.log(new Log().withTag(TAG).text("createAccount1"));
				account = createAccount();
			}

		} else {
			Logger.log(new Log().withTag(TAG).text("createAccount2"));
			account = createAccount();
		}

		Logger.log(new Log().withTag(TAG).text("setActiveUserWallet").put("kinUserId", kinUserId)
			.put("pubAdd", account.getPublicAddress()));
		currentUserId = kinUserId;
		local.setActiveUserWallet(kinUserId, account.getPublicAddress());
	}

	private IKinAccount createAccount() throws BlockchainException {
		try {
			// Create new account
			return kinClient.addAccount();
		} catch (CreateAccountException e) {
			throw ErrorUtil.getBlockchainException(e);
		}
	}

	@Override
	@Nullable
	public IKinAccount getKinAccount() {
		return account;
	}

	@Override
	public void isAccountCreated(KinCallback<Void> callback) {
		if (accountCreationRequest != null) {
			accountCreationRequest.cancel();
		}

		accountCreationRequest = new AccountCreationRequest(this);
		accountCreationRequest.run(callback);
	}

	@Override
	public void signTransaction(@NonNull final String publicAddress, @NonNull final BigDecimal amount,
		@NonNull final String orderID, @NonNull final String offerID, @NonNull final SignTransactionListener listener) throws OperationFailedException {
		if (account != null) {
			eventLogger.send(SpendTransactionBroadcastToBlockchainSubmitted.create(offerID, orderID));
			account.sendTransactionSync(publicAddress, amount, new IWhitelistService() {
				@Override
				public WhitelistResult onWhitelistableTransactionReady(IWhitelistableTransaction transaction) {
					listener.onTransactionSigned(transaction.getTransactionPayload());
					return new WhitelistResult(transaction.getTransactionPayload(), false);
				}
			}, orderID);
		}
	}

	@Override
	public void sendTransaction(@NonNull final String publicAddress, @NonNull final BigDecimal amount,
		@NonNull final String orderID, @NonNull final String offerID) {
		if (account != null) {
			eventLogger.send(SpendTransactionBroadcastToBlockchainSubmitted.create(offerID, orderID));
			account.sendTransaction(publicAddress, amount, new IWhitelistService() {
				@Override
				public WhitelistResult onWhitelistableTransactionReady(IWhitelistableTransaction transaction) throws WhitelistTransactionFailedException {
					return new WhitelistResult(transaction.getTransactionPayload(), true);
				}
			}, orderID).run(new ResultCallback<ITransactionId>() {
					@Override
					public void onResult(ITransactionId result) {
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
	}

	private String getAppID() {
		if (TextUtils.isEmpty(appID)) {
			appID = authRepository.getAppID();
		}
		return appID;
	}

	@Override
	public KinSdkVersion getBlockchainVersion() {
		return local.getBlockchainVersion();
	}

	private void initBalance() {
		reconnectBalanceConnection();
		balance.postValue(getBalance());
		getBalance(null);
	}

	@Override
	public Balance getBalance() {
		Balance balance = new Balance();
		balance.setAmount(new BigDecimal(local.getBalance()));
		return balance;
	}

	@Override
	public void getBalance(@Nullable final KinCallback<Balance> callback) {
		if (account == null) {
			if (callback != null) {
				mainThread.execute(new Runnable() {
					@Override
					public void run() {
						callback.onFailure(ErrorUtil.getClientException(ClientException.ACCOUNT_NOT_LOGGED_IN, null));
					}
				});
			}
			return;
		}
		account.getBalance().run(new ResultCallback<IBalance>() {
			@Override
			public void onResult(final IBalance balanceObj) {
				setBalance(balanceObj);
				if (callback != null) {
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
				if (callback != null) {
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

	@Override
	public Balance getBalanceSync() throws ClientException, BlockchainException {
		if (account == null) {
			throw ErrorUtil.getClientException(ClientException.ACCOUNT_NOT_LOGGED_IN, null);
		}
		try {
			setBalance(account.getBalanceSync());
			return balance.getValue();
		} catch (OperationFailedException e) {
			throw ErrorUtil.getBlockchainException(e);
		}
	}

	@Override
	public void reconnectBalanceConnection() {
		synchronized (balanceObserversLock) {
			if (balanceObserversCount > 0) {
				removeRegistration(balanceRegistration);
				startBalanceListener();
			}
		}
	}

	@VisibleForTesting
	void setBalance(final kin.sdk.migration.common.interfaces.IBalance balanceObj) {
		Balance balanceTemp = balance.getValue();
		// if the values are not equals so we need to update,
		// no need to update for equal values.
		if (balanceTemp.getAmount().compareTo(balanceObj.value()) != 0) {
			eventLogger.send(KinBalanceUpdated.create(balanceTemp.getAmount().doubleValue()));
			Logger.log(new Log().withTag(TAG).text("setBalance: Balance changed, should get update"));
			balanceTemp.setAmount(balanceObj.value());
			balance.postValue(balanceTemp);
			local.setBalance(balanceObj.value().intValue());
		}
	}

	@Override
	public void addBalanceObserver(@NonNull Observer<Balance> observer, boolean startSSE) {
		balance.addObserver(observer);
		observer.onChanged(balance.getValue());

		if (startSSE) {
			incrementBalanceSSECount();
		}
	}

	private void incrementBalanceSSECount() {
		synchronized (balanceObserversLock) {
			if (balanceObserversCount == 0) {
				startBalanceListener();
			}
			balanceObserversCount++;
			Logger.log(new Log().withTag(TAG).put("incrementBalanceSSECount count", balanceObserversCount));
		}
	}

	private void startBalanceListener() {
		if (account != null) {
			Logger.log(new Log().withTag(TAG).text("startBalanceListener"));
			balanceRegistration = account.addBalanceListener(new IEventListener<IBalance>() {
				@Override
				public void onEvent(IBalance data) {
					setBalance(data);
				}
			});
		}
	}

	@Override
	public void removeBalanceObserver(@NonNull Observer<Balance> observer, boolean stopSSE) {
		Logger.log(new Log().withTag(TAG).text("removeBalanceObserver"));
		balance.removeObserver(observer);
		if (stopSSE) {
			decrementBalanceSSECount();
		}
	}

	private void decrementBalanceSSECount() {
		synchronized (balanceObserversLock) {
			if (balanceObserversCount > 0) {
				balanceObserversCount--;
			}
			Logger.log(new Log().withTag(TAG).put("decrementBalanceSSECount: count", balanceObserversCount));

			if (balanceObserversCount == 0) {
				removeRegistration(balanceRegistration);
			}
		}
	}

	@Override
	public String getPublicAddress() throws BlockchainException {
		if (account == null) {
			throw new BlockchainException(BlockchainException.ACCOUNT_NOT_FOUND, "The Account could not be found",
				null);
		}
		return account.getPublicAddress();
	}

	@Override
	@Nullable
	public String getPublicAddress(final int accountIndex) {
		IKinAccount account = kinClient.getAccount(accountIndex);
		return account != null ? account.getPublicAddress() : null;
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
		if (account != null) {
			paymentRegistration = account.addPaymentListener(new IEventListener<IPaymentInfo>() {
				@Override
				public void onEvent(IPaymentInfo data) {
					final String orderID = extractOrderId(data.memo());
					Logger.log(new Log().withTag(TAG).put("startPaymentListener onEvent: the orderId", orderID)
						.put("with memo", data.memo()));
					final String accountPublicAddress = account.getPublicAddress();
					if (orderID != null && accountPublicAddress != null) {
						completedPayment.postValue(PaymentConverter.toPayment(data, orderID, accountPublicAddress));
						Logger.log(new Log().withTag(TAG).put("completedPayment order id", orderID));
					}

					// UpdateBalance
					getBalance(null);
				}
			});
		}
	}

	@Override
	public void removePaymentObserver(Observer<Payment> observer) {
		completedPayment.removeObserver(observer);
		decrementPaymentCount();
	}

	@Override
	public void createTrustLine(@NonNull final KinCallback<Void> callback) {
		if (account != null) {
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
	}

	@Override
	public KeyStoreProvider getKeyStoreProvider() {
		return new KeyStoreProviderImpl(kinClient, account);
	}

	@Override
	public boolean updateActiveAccount(int accountIndex) {
		if (accountIndex != -1 && accountIndex < kinClient.getAccountCount()) {
			account = kinClient.getAccount(accountIndex);
			local.setActiveUserWallet(currentUserId, account.getPublicAddress());
			reconnectBalanceConnection();
			//trigger balance update
			getBalance(null);
			return true;
		}
		return false;
	}

	@Override
	public void logout() {
		removeRegistration(paymentRegistration);
		removeRegistration(balanceRegistration);
		paymentRegistration = null;
		balanceRegistration = null;
		completedPayment.removeAllObservers();
		account = null;
		local.logout();
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

	private void removeRegistration(IListenerRegistration listenerRegistration) {
		Logger.log(new Log().withTag(TAG).text("removeRegistration"));
		if (listenerRegistration != null) {
			listenerRegistration.remove();
		}
	}

	@VisibleForTesting
	String extractOrderId(String memo) {
		String[] memoParts = memo.split(MEMO_DELIMITER);
		String orderID = null;
		if (memoParts.length == MEMO_SPLIT_LENGTH && memoParts[APP_ID_INDEX].equals(getAppID())) {
			orderID = memoParts[ORDER_ID_INDEX];
		}
		return orderID;
	}
}
