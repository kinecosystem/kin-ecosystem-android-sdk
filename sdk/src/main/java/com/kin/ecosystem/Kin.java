package com.kin.ecosystem;

import static com.kin.ecosystem.common.exception.ClientException.ACCOUNT_NOT_LOGGED_IN;
import static com.kin.ecosystem.common.exception.ClientException.BAD_CONFIGURATION;
import static com.kin.ecosystem.common.exception.ClientException.SDK_NOT_STARTED;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.KinEnvironment;
import com.kin.ecosystem.common.KinTheme;
import com.kin.ecosystem.common.NativeOfferClickEvent;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.common.model.Balance;
import com.kin.ecosystem.common.model.NativeOffer;
import com.kin.ecosystem.common.model.OrderConfirmation;
import com.kin.ecosystem.common.model.UserStats;
import com.kin.ecosystem.core.Log;
import com.kin.ecosystem.core.Logger;
import com.kin.ecosystem.core.accountmanager.AccountManager;
import com.kin.ecosystem.core.accountmanager.AccountManager.AccountState;
import com.kin.ecosystem.core.accountmanager.AccountManagerImpl;
import com.kin.ecosystem.core.accountmanager.AccountManagerLocal;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.bi.EventLoggerImpl;
import com.kin.ecosystem.core.bi.events.EntrypointButtonTapped;
import com.kin.ecosystem.core.bi.events.KinSdkInitiated;
import com.kin.ecosystem.core.bi.events.UserLoginFailed;
import com.kin.ecosystem.core.bi.events.UserLoginRequested;
import com.kin.ecosystem.core.bi.events.UserLoginSucceeded;
import com.kin.ecosystem.core.bi.events.UserLogoutRequested;
import com.kin.ecosystem.core.data.auth.AuthLocalData;
import com.kin.ecosystem.core.data.auth.AuthRemoteData;
import com.kin.ecosystem.core.data.auth.AuthRepository;
import com.kin.ecosystem.core.data.auth.UserLoginState;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource.MigrationProcessListener;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceLocal;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceRemote;
import com.kin.ecosystem.core.data.internal.ConfigurationImpl;
import com.kin.ecosystem.core.data.internal.ConfigurationLocalImpl;
import com.kin.ecosystem.core.data.offer.OfferRemoteData;
import com.kin.ecosystem.core.data.offer.OfferRepository;
import com.kin.ecosystem.core.data.order.OrderLocalData;
import com.kin.ecosystem.core.data.order.OrderRemoteData;
import com.kin.ecosystem.core.data.order.OrderRepository;
import com.kin.ecosystem.core.data.settings.SettingsDataSourceImpl;
import com.kin.ecosystem.core.data.settings.SettingsDataSourceLocal;
import com.kin.ecosystem.core.network.ApiException;
import com.kin.ecosystem.core.network.model.AccountInfo;
import com.kin.ecosystem.core.util.DeviceUtils;
import com.kin.ecosystem.core.util.ErrorUtil;
import com.kin.ecosystem.core.util.ExecutorsUtil;
import com.kin.ecosystem.core.util.Validator;
import com.kin.ecosystem.main.view.EcosystemActivity;
import com.kin.ecosystem.recovery.BackupAndRestore;
import com.kin.ecosystem.recovery.BackupAndRestoreImpl;
import com.kin.ecosystem.widget.util.FontUtil;
import java.util.concurrent.atomic.AtomicBoolean;
import kin.sdk.migration.MigrationManager;
import kin.sdk.migration.MigrationNetworkInfo;
import kin.sdk.migration.common.KinSdkVersion;

public class Kin {

	private static final String KIN_ECOSYSTEM_STORE_PREFIX_KEY = "kinecosystem_store";
	private static final String KIN_ECOSYSTEM_ENVIRONMENT_NAME_KEY = "com.kin.ecosystem.sdk.EnvironmentName";
	public static final String KEY_ECOSYSTEM_EXPERIENCE = "ecosystem_experience";
	private static volatile Kin instance;

	private static EventLogger eventLogger;
	private static volatile AtomicBoolean isAccountLoggedIn = new AtomicBoolean(false);
	private static volatile String environmentName;

	private final ExecutorsUtil executorsUtil;
	private final KinContext kinContext;

	private Kin(Context appContext) {
		executorsUtil = new ExecutorsUtil();
		// use application context to avoid leaks.
		kinContext = new KinContext(appContext.getApplicationContext());

	}

	private static Kin getInstance(Context appContext) {
		if (instance == null) {
			synchronized (Kin.class) {
				if (instance == null) {
					instance = new Kin(appContext);
				}
			}
		}

		return instance;

	}

	/**
	 * Initialize the sdk with all the resources to start, this function isn't doing a network calls at all.
	 * In order to recover from process restart all the activities in our side should call this method in onCreate.
	 *
	 * @param appContext application context.
	 * @throws ClientException - The sdk could not be initiated.
	 */
	public synchronized static void initialize(Context appContext, KinTheme kinTheme) throws ClientException {
		if (isInstanceNull()) {
			instance = getInstance(appContext);
			// use application context to avoid leaks.
			appContext = appContext.getApplicationContext();
			AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

			//Load data from manifest, can throw ClientException if no data available.
			loadDefaultsFromMetadata(getKinContext());

			//Set Environment
			ConfigurationImpl.init(environmentName, new ConfigurationLocalImpl(appContext));
			eventLogger = EventLoggerImpl.getInstance();

			AuthRepository
				.init(AuthLocalData.getInstance(getKinContext()), AuthRemoteData.getInstance(instance.executorsUtil));

			BlockchainSourceImpl.init(eventLogger, BlockchainSourceLocal.getInstance(getKinContext()),
				BlockchainSourceRemote.getInstance(instance.executorsUtil), AuthRepository.getInstance());

			ConfigurationImpl.getInstance().setBlockchainSource(BlockchainSourceImpl.getInstance());

			EventCommonDataUtil.setBaseData(getKinContext());

			AccountManagerImpl
				.init(AccountManagerLocal.getInstance(getKinContext()), eventLogger, AuthRepository.getInstance(),
					BlockchainSourceImpl.getInstance());

			OrderRepository.init(BlockchainSourceImpl.getInstance(),
				eventLogger,
				OrderRemoteData.getInstance(instance.executorsUtil),
				OrderLocalData.getInstance(getKinContext(), instance.executorsUtil));

			OfferRepository.init(OfferRemoteData.getInstance(instance.executorsUtil), OrderRepository.getInstance());

			DeviceUtils.init(getKinContext());
			FontUtil.Companion.init(appContext.getAssets());

			if (AuthRepository.getInstance().getAppID() != null) {
				eventLogger.send(KinSdkInitiated.create());
			}
		}

		//Instance not null, update KinTheme
		ConfigurationImpl.getInstance().setKinTheme(kinTheme);
	}

	private static Context getKinContext() {
		return instance.kinContext.getApplicationContext();
	}

	private static void loadDefaultsFromMetadata(Context context) throws ClientException {
		if (context == null) {
			return;
		}

		ApplicationInfo applicationInfo;
		try {
			applicationInfo = context.getPackageManager().getApplicationInfo(
				context.getPackageName(), PackageManager.GET_META_DATA);
		} catch (PackageManager.NameNotFoundException e) {
			throwProvideEnvironmentMetaDataException();
			return;
		}

		if (applicationInfo == null || applicationInfo.metaData == null) {
			throwProvideEnvironmentMetaDataException();
		}

		Object envNameObj = applicationInfo.metaData.get(KIN_ECOSYSTEM_ENVIRONMENT_NAME_KEY);
		if (envNameObj instanceof String) {
			final String envName = (String) envNameObj;
			if (Validator.isEnvironmentName(envName)) {
				environmentName = envName;
			} else {
				throw new ClientException(BAD_CONFIGURATION, "Environment name: " + envName + " is not valid", null);
			}
		} else {
			throwProvideEnvironmentMetaDataException();
		}
	}

	private static void throwProvideEnvironmentMetaDataException() throws ClientException {
		throw new ClientException(BAD_CONFIGURATION,
			"You must provide environment meta data element in AndroidManifest.xml as a String value", null);
	}

	public static void enableLogs(final boolean enableLogs) {
		Logger.enableLogs(enableLogs);
	}

	/**
	 * In order to use all the other features in Kin Ecosystem, you should login the user first.
	 * This option should be use in production.
	 *
	 * @param jwt data for login, please refer to <a href="README.md#generating-the-jwt-token">Generating the jwt token</a> and see more info.
	 * @param loginCallback a login callback whether login succeed or not.
	 */
	public static void login(final @NonNull String jwt, final KinCallback<Void> loginCallback) {
		try {
			checkInstanceNotNull();
			@UserLoginState final int loginState = AuthRepository.getInstance().getUserLoginState(jwt);
			switch (loginState) {
				case UserLoginState.DIFFERENT_USER:
					logout();
				case UserLoginState.FIRST:
					isAccountLoggedIn.getAndSet(false);
					eventLogger.send(UserLoginRequested.create());
					break;
				case UserLoginState.SAME_USER:
					break;
			}

			AuthRepository.getInstance().setJWT(jwt);

			BlockchainSourceImpl.getInstance().fetchBlockchainVersion(new KinCallback<KinSdkVersion>() {
				@Override
				public void onResponse(KinSdkVersion response) {
					internalLogin(loginState, loginCallback);
				}

				@Override
				public void onFailure(KinEcosystemException exception) {
					internalLogin(loginState, loginCallback);
				}
			});
		} catch (final ClientException exception) {
			sendLoginFailed(exception, loginCallback);
		}
	}

	private static void internalLogin(@NonNull final int loginState, final KinCallback<Void> loginCallback) {
		MigrationManager migrationManager = createMigrationManager(getKinContext(), AuthRepository.getInstance().getAppID());
		BlockchainSourceImpl.getInstance().setMigrationManager(migrationManager);

		AuthRepository.getInstance().getAccountInfo(new KinCallback<AccountInfo>() {
			@Override
			public void onResponse(AccountInfo accountInfo) {
				String publicAddress;
				try {
					BlockchainSourceImpl.getInstance().loadAccount(accountInfo.getAuthToken().getEcosystemUserID());
					publicAddress = BlockchainSourceImpl.getInstance().getPublicAddress();
				} catch (final BlockchainException exception) {
					sendLoginFailed(exception, loginCallback);
					return;
				}

				// check server is synced with client, if synced -> skip updateWalletAddress
				if (publicAddress != null && !publicAddress.equals(accountInfo.getUser().getCurrentWallet())) {
					AuthRepository.getInstance().updateWalletAddress(publicAddress, new KinCallback<Boolean>() {
						@Override
						public void onResponse(Boolean response) {
							migrate(loginCallback, loginState);
						}

						@Override
						public void onFailure(KinEcosystemException exception) {
							sendLoginFailed(exception, loginCallback);
						}
					});
				} else {
					migrate(loginCallback, loginState);
				}
			}

			@Override
			public void onFailure(final KinEcosystemException exception) {
				sendLoginFailed(exception, loginCallback);
			}
		});
	}

	private static void migrate(final KinCallback<Void> loginCallback, final int loginState) {
		BlockchainSourceImpl.getInstance().startMigrationProcess(new MigrationProcessListener() {
				@Override
				public void onMigrationStart() {}

				@Override
				public void onMigrationEnd() {
					onboard(loginCallback, loginState);
				}

				@Override
				public void onMigrationError(BlockchainException error) {
					sendLoginFailed(error, loginCallback);
				}
			});
	}

	private static void onboard(final KinCallback<Void> loginCallback, final int loginState) {
		AccountManagerImpl.getInstance().addAccountStateObserver(new Observer<Integer>() {
			@Override
			public void onChanged(@AccountState Integer accountState) {
				switch (accountState) {
					case AccountManager.CREATION_COMPLETED:
						sendLoginSucceed(loginCallback, loginState);
						OfferRepository.getInstance().getOffers(null);
						AccountManagerImpl.getInstance().removeAccountStateObserver(this);
						break;
					case AccountManager.ERROR:
						sendLoginFailed(AccountManagerImpl.getInstance().getError(), loginCallback);
						AccountManagerImpl.getInstance().removeAccountStateObserver(this);
						break;
				}
			}
		});
		AccountManagerImpl.getInstance().start();
	}

	private static void sendLoginSucceed(final KinCallback<Void> loginCallback, @UserLoginState int loginState) {
		if (loginState != UserLoginState.SAME_USER) {
			eventLogger.send(UserLoginSucceeded.create());
		}
		isAccountLoggedIn.getAndSet(true);
		instance.executorsUtil.mainThread().execute(new Runnable() {
			@Override
			public void run() {
				loginCallback.onResponse(null);
			}
		});
	}

	private static void sendLoginFailed(final KinEcosystemException exception, final KinCallback<Void> loginCallback) {
		eventLogger.send(UserLoginFailed.create(getMessage(exception)));
		isAccountLoggedIn.getAndSet(false);
		instance.executorsUtil.mainThread().execute(new Runnable() {
			@Override
			public void run() {
				loginCallback.onFailure(exception);
			}
		});
	}

	private static MigrationManager createMigrationManager(Context context,@NonNull String appId) {
		KinEnvironment kinEnvironment = ConfigurationImpl.getInstance().getEnvironment();

		final String oldNetworkUrl = kinEnvironment.getOldBlockchainNetworkUrl();
		final String oldNetworkId = kinEnvironment.getOldBlockchainPassphrase();

		final String newNetworkUrl = kinEnvironment.getNewBlockchainNetworkUrl();
		final String newNetworkId = kinEnvironment.getNewBlockchainPassphrase();

		final String issuer = kinEnvironment.getOldBlockchainIssuer();
		final String migrationServiceUrl = kinEnvironment.getMigrationServiceUrl();

		MigrationNetworkInfo migrationNetworkInfo = new MigrationNetworkInfo(oldNetworkUrl, oldNetworkId,
			newNetworkUrl, newNetworkId, issuer, migrationServiceUrl);

		final BlockchainSource.Local local = BlockchainSourceLocal.getInstance(context);
		final BlockchainSource.Remote remote = BlockchainSourceRemote.getInstance(getInstance(context).executorsUtil);
		MigrationManager migrationManager = new MigrationManager(context, appId, migrationNetworkInfo,
			new KinBlockchainVersionProvider(local, remote),
			new MigrationEventsListener(EventLoggerImpl.getInstance()), KIN_ECOSYSTEM_STORE_PREFIX_KEY);
		migrationManager.enableLogs(Logger.isEnabled());
		return migrationManager;
	}

	private static String getMessage(KinEcosystemException exception) {
		return exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
	}

	private static boolean isInstanceNull() {
		return instance == null;
	}

	private static void checkAccountIsLoggedIn() throws ClientException {
		if (!isAccountLoggedIn.get()) {
			throw ErrorUtil.getClientException(ACCOUNT_NOT_LOGGED_IN, null);
		}
	}

	private static void checkInstanceNotNull() throws ClientException {
		if (isInstanceNull()) {
			throw ErrorUtil.getClientException(SDK_NOT_STARTED, null);
		}
	}

	/**
	 * Logout from the current logged in user.
	 *
	 * @throws ClientException - sdk not initialized.
	 */
	public static void logout() throws ClientException {
		checkInstanceNotNull();
		if (isAccountLoggedIn.compareAndSet(true, false)) {
			eventLogger.send(UserLogoutRequested.create());
			Logger.log(new Log().withTag("Kin.java").text("logout").put("isAccountLoggedIn", isAccountLoggedIn));
			AuthRepository.getInstance().logout();
			clearCachedData();
		}
	}

	/**
	 * Clear cached old internal data
	 */
	private static void clearCachedData() {
		BlockchainSourceImpl.getInstance().logout();
		AccountManagerImpl.getInstance().logout();
		OrderRepository.getInstance().logout();
		OfferRepository.getInstance().logout();
	}

	/**
	 * Launch Kin Marketplace if the user is activated, otherwise it will launch Welcome to Kin page.
	 *
	 * @param activity the activity user can go back to.
	 * @throws ClientException - sdk not initialized or account not logged in.
	 * @deprecated Please use {@link Kin#launchEcosystem}
	 */
	@Deprecated
	public static void launchMarketplace(@NonNull final Activity activity) throws ClientException {
		checkInstanceNotNull();
		checkAccountIsLoggedIn();
		launchEcosystem(activity, EcosystemExperience.MARKETPLACE);
	}

	/**
	 * Launch a specific experience in KinEcosystem if the user is activated,
	 * otherwise it will launch Welcome to Kin page and then the experience.
	 *
	 * @param activity the activity user can go back to.
	 * @param experience should be one of {@link EcosystemExperience}
	 * @throws ClientException - sdk not initialized or account not logged in.
	 */
	public static void launchEcosystem(@NonNull final Activity activity, @EcosystemExperience final int experience)
		throws ClientException {
		checkInstanceNotNull();
		checkAccountIsLoggedIn();
		eventLogger.send(EntrypointButtonTapped.create());
		navigateToExperience(activity, experience);
	}

	private static void navigateToExperience(@NonNull final Activity activity,
		@EcosystemExperience final int experience) {
		Intent marketplaceIntent = new Intent(activity, EcosystemActivity.class);
		launchIntent(activity, marketplaceIntent, experience);
	}

	private static void launchIntent(@NonNull Activity activity, Intent intentToLaunch,
		@EcosystemExperience final int experience) {
		intentToLaunch.putExtra(KEY_ECOSYSTEM_EXPERIENCE, experience);
		activity.startActivity(intentToLaunch);
		activity.overridePendingTransition(0, 0);
	}

	/**
	 * @return The account public address
	 * @throws ClientException - sdk not initialized or account not logged in.
	 */
	public static String getPublicAddress() throws ClientException {
		checkInstanceNotNull();
		checkAccountIsLoggedIn();
		try {
			return BlockchainSourceImpl.getInstance().getPublicAddress();
		} catch (BlockchainException e) {
			throw ErrorUtil.getClientException(ACCOUNT_NOT_LOGGED_IN, e);
		}
	}

	/**
	 * Get the cached balance, can be different from the current balance on the network.
	 *
	 * @return balance amount
	 * @throws ClientException - sdk not initialized or account not logged in.
	 */
	public static Balance getCachedBalance() throws ClientException {
		checkInstanceNotNull();
		checkAccountIsLoggedIn();
		return BlockchainSourceImpl.getInstance().getBalance();
	}

	/**
	 * Get the current account balance from the network.
	 *
	 * @param callback balance amount
	 * @throws ClientException - sdk not initialized or account not logged in.
	 */
	public static void getBalance(@NonNull final KinCallback<Balance> callback) throws ClientException {
		checkInstanceNotNull();
		checkAccountIsLoggedIn();
		BlockchainSourceImpl.getInstance().getBalance(callback);
	}

	/**
	 * Add balance observer to start getting notified when the balance is changed on the blockchain network. On balance
	 * changes you will get {@link Balance} with the balance amount.
	 *
	 * Take in consideration that on adding this observer, a live network connection will be open to the blockchain
	 * network, In order to close the connection use {@link #removeBalanceObserver(Observer)} with the same observer. If
	 * no other observers on this connection, the connection will be closed.
	 *
	 * @throws ClientException - sdk not initialized.
	 */
	public static void addBalanceObserver(@NonNull final Observer<Balance> observer) throws ClientException {
		checkInstanceNotNull();
		BlockchainSourceImpl.getInstance().addBalanceObserver(observer, true);

	}

	/**
	 * Remove the balance observer, this method will close the live network connection to the blockchain network
	 * if there is no more observers.
	 *
	 * @throws ClientException - sdk not initialized.
	 */
	public static void removeBalanceObserver(@NonNull final Observer<Balance> observer) throws ClientException {
		checkInstanceNotNull();
		BlockchainSourceImpl.getInstance().removeBalanceObserver(observer, true);
	}

	/**
	 * Allowing your users to purchase virtual goods you define within your app, using KIN.
	 * This call might take time, due to transaction validation on the blockchain network.
	 *
	 * @param offerJwt Represents the offer in a JWT manner.
	 * @param callback {@link OrderConfirmation} The result will be a failure or a success with a jwt confirmation.
	 * @throws ClientException - sdk not initialized or account not logged in.
	 */
	public static void purchase(String offerJwt, @Nullable KinCallback<OrderConfirmation> callback)
		throws ClientException {
		checkInstanceNotNull();
		checkAccountIsLoggedIn();
		OrderRepository.getInstance().purchase(offerJwt, callback);
	}

	/**
	 * Allowing your users to earn Kin as a reward for native task you define.
	 * This call might take time, due to transaction validation on the blockchain network.
	 *
	 * @param offerJwt The offer details represented in a JWT manner.
	 * @param callback After validating the info and sending the payment to the user, you will receive {@link
	 * OrderConfirmation}, with the jwtConfirmation and you can validate the order when the order status is completed.
	 * @throws ClientException - sdk not initialized or account not logged in.
	 */
	public static void requestPayment(String offerJwt, @Nullable KinCallback<OrderConfirmation> callback)
		throws ClientException {
		checkInstanceNotNull();
		checkAccountIsLoggedIn();
		OrderRepository.getInstance().requestPayment(offerJwt, callback);
	}

	/**
	 * Allowing a user to pay to a different user for an offer defined within your app, using KIN.
	 * This call might take time, due to transaction validation on the blockchain network.
	 *
	 * @param offerJwt Represents a 'Pay to user' offer in a JWT manner.
	 * @param callback {@link OrderConfirmation} The result will be a failure or a success with a jwt confirmation.
	 * @throws ClientException - sdk not initialized or account not logged in.
	 */
	public static void payToUser(String offerJwt, @Nullable KinCallback<OrderConfirmation> callback)
		throws ClientException {
		checkInstanceNotNull();
		checkAccountIsLoggedIn();
		//pay to user has a similar flow like purchase (spend), the only different is the expected input JWT.
		OrderRepository.getInstance().purchase(offerJwt, callback);
	}

	/**
	 * Determine if a Kin Account is associated with the {@param userId}, on Kin Ecosystem Server.
	 * That means you can pay to the user with {@link Kin#payToUser(String userId, KinCallback)},
	 * otherwise the recipient user won't get the Kin.
	 *
	 * @param userId The user id to check
	 * @param callback The result will be a {@link Boolean}
	 * @throws ClientException - sdk not initialized.
	 */
	public static void hasAccount(@NonNull String userId, @NonNull final KinCallback<Boolean> callback)
		throws ClientException {
		checkInstanceNotNull();
		AuthRepository.getInstance().hasAccount(userId, callback);
	}

	/**
	 * Get user's stats which include history information such as number of Earn/Spend orders completed by the user or last earn/spend dates.
	 * This information could be used for re-engaging users, provide specific experience for users who never earn before etc.
	 *
	 * @param callback The result will be a {@link UserStats}
	 * @throws ClientException - sdk not initialized or account not logged in.
	 */
	public static void userStats(@NonNull KinCallback<UserStats> callback)
		throws ClientException {
		checkInstanceNotNull();
		checkAccountIsLoggedIn();
		AuthRepository.getInstance().userStats(callback);
	}

	/**
	 * Returns a {@link OrderConfirmation}, with the order status and a jwtConfirmation if the order is completed.
	 *
	 * @param offerID The offerID that this order created from
	 * @throws ClientException - sdk not initialized.
	 */
	public static void getOrderConfirmation(@NonNull String offerID, @NonNull KinCallback<OrderConfirmation> callback)
		throws ClientException {
		checkInstanceNotNull();
		OrderRepository.getInstance().getExternalOrderStatus(offerID, callback);
	}

	/**
	 * Add a native offer {@link Observer} to receive a trigger when you native offers on Kin Marketplace are clicked.
	 *
	 * @throws ClientException - sdk not initialized.
	 */
	public static void addNativeOfferClickedObserver(@NonNull Observer<NativeOfferClickEvent> observer)
		throws ClientException {
		checkInstanceNotNull();
		OfferRepository.getInstance().addNativeOfferClickedObserver(observer);
	}

	/**
	 * Remove the callback if you no longer want to get triggered when your offer on Kin marketplace are clicked.
	 *
	 * @throws ClientException - sdk not initialized.
	 */
	public static void removeNativeOfferClickedObserver(@NonNull Observer<NativeOfferClickEvent> observer)
		throws ClientException {
		checkInstanceNotNull();
		OfferRepository.getInstance().removeNativeOfferClickedObserver(observer);
	}

	/**
	 * Adds an {@link NativeOffer} to spend or earn offer list on Kin Marketplace activity.
	 * The offer will be added at index 0 in the spend list.
	 *
	 * @param nativeOffer The spend or earn offer you want to add to the spend list.
	 * @param dismissOnTap An indication if the sdk should close the marketplace when this offer tapped.
	 * @return true if the offer added successfully, the list was changed.
	 * @throws ClientException - sdk not initialized.
	 */
	public static boolean addNativeOffer(@NonNull NativeOffer nativeOffer, boolean dismissOnTap)
		throws ClientException {
		checkInstanceNotNull();
		return OfferRepository.getInstance().addNativeOffer(nativeOffer, dismissOnTap);
	}

	/**
	 * Removes a {@link NativeOffer} from the spend or earn list on Kin Marketplace activity.
	 *
	 * @param nativeOffer The spend or earn offer you want to remove from the spend list.
	 * @return true if the offer removed successfully, the list was changed.
	 * @throws ClientException - sdk not initialized.
	 */
	public static boolean removeNativeOffer(@NonNull NativeOffer nativeOffer) throws ClientException {
		checkInstanceNotNull();
		return OfferRepository.getInstance().removeNativeOffer(nativeOffer);
	}

	public static BackupAndRestore getBackupAndRestoreManager(@NonNull final Activity activity) throws ClientException {
		checkInstanceNotNull();
		return new BackupAndRestoreImpl(activity, AccountManagerImpl.getInstance(),
			eventLogger, BlockchainSourceImpl.getInstance(),
			new SettingsDataSourceImpl(new SettingsDataSourceLocal(activity.getApplicationContext())));
	}
}
