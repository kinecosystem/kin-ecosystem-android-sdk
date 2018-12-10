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
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.KinEnvironment;
import com.kin.ecosystem.common.NativeOfferClickEvent;
import com.kin.ecosystem.common.ObservableData;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.common.model.Balance;
import com.kin.ecosystem.common.model.NativeOffer;
import com.kin.ecosystem.common.model.OrderConfirmation;
import com.kin.ecosystem.common.model.UserStats;
import com.kin.ecosystem.common.model.WhitelistData;
import com.kin.ecosystem.core.Logger;
import com.kin.ecosystem.core.accountmanager.AccountManagerImpl;
import com.kin.ecosystem.core.accountmanager.AccountManagerLocal;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.bi.EventLoggerImpl;
import com.kin.ecosystem.core.bi.events.EntrypointButtonTapped;
import com.kin.ecosystem.core.bi.events.KinSdkInitiated;
import com.kin.ecosystem.core.data.auth.AuthLocalData;
import com.kin.ecosystem.core.data.auth.AuthRemoteData;
import com.kin.ecosystem.core.data.auth.AuthRepository;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceLocal;
import com.kin.ecosystem.core.data.internal.ConfigurationImpl;
import com.kin.ecosystem.core.data.internal.ConfigurationLocal;
import com.kin.ecosystem.core.data.offer.OfferRemoteData;
import com.kin.ecosystem.core.data.offer.OfferRepository;
import com.kin.ecosystem.core.data.order.OrderLocalData;
import com.kin.ecosystem.core.data.order.OrderRemoteData;
import com.kin.ecosystem.core.data.order.OrderRepository;
import com.kin.ecosystem.core.network.model.AuthToken;
import com.kin.ecosystem.core.network.model.SignInData;
import com.kin.ecosystem.core.network.model.SignInData.SignInTypeEnum;
import com.kin.ecosystem.core.util.DeviceUtils;
import com.kin.ecosystem.core.util.ErrorUtil;
import com.kin.ecosystem.core.util.ExecutorsUtil;
import com.kin.ecosystem.core.util.Validator;
import com.kin.ecosystem.main.view.EcosystemActivity;
import com.kin.ecosystem.splash.view.SplashActivity;
import java.util.UUID;
import kin.core.KinClient;
import kin.core.ServiceProvider;


public class Kin {

	private static final String KIN_ECOSYSTEM_STORE_PREFIX_KEY = "kinecosystem_store";
	private static final String KIN_ECOSYSTEM_ENVIRONMENT_NAME_KEY = "com.kin.ecosystem.sdk.EnvironmentName";
	private static volatile Kin instance;

	private static EventLogger eventLogger;
	private static volatile boolean isAccountLoggedIn = false;
	private static volatile String environmentName;

	private final ExecutorsUtil executorsUtil;

	private Kin() {
		executorsUtil = new ExecutorsUtil();
	}

	private static Kin getInstance() {
		if (instance == null) {
			synchronized (Kin.class) {
				if (instance == null) {
					instance = new Kin();
				}
			}
		}

		return instance;
	}

	public synchronized static void initialize(Context appContext) throws ClientException {
		if (isInstanceNull()) {
			instance = getInstance();
			// use application context to avoid leaks.
			appContext = appContext.getApplicationContext();

			//Load data from manifest, can throw ClientException if no data available.
			loadDefaultsFromMetadata(appContext);

			//Set Environment
			ConfigurationImpl.init(ConfigurationLocal.getInstance(appContext));
			ConfigurationImpl.getInstance().setEnvironment(environmentName);
			KinEnvironment kinEnvironment = ConfigurationImpl.getInstance().getEnvironment();
			eventLogger = EventLoggerImpl.getInstance();
			final String networkUrl = kinEnvironment.getBlockchainNetworkUrl();
			final String networkId = kinEnvironment.getBlockchainPassphrase();
			final String issuer = kinEnvironment.getIssuer();

			KinClient kinClient = new KinClient(appContext, new ServiceProvider(networkUrl, networkId) {
				@Override
				protected String getIssuerAccountId() {
					return issuer;
				}
			}, KIN_ECOSYSTEM_STORE_PREFIX_KEY);
			BlockchainSourceImpl.init(eventLogger, kinClient, BlockchainSourceLocal.getInstance(appContext));

			AuthRepository
				.init(AuthLocalData.getInstance(appContext), AuthRemoteData.getInstance(instance.executorsUtil));

			EventCommonDataUtil.setBaseData(appContext);

			AccountManagerImpl
				.init(AccountManagerLocal.getInstance(appContext), eventLogger, AuthRepository.getInstance(),
					BlockchainSourceImpl.getInstance());

			OrderRepository.init(BlockchainSourceImpl.getInstance(),
				eventLogger,
				OrderRemoteData.getInstance(instance.executorsUtil),
				OrderLocalData.getInstance(appContext, instance.executorsUtil));

			OfferRepository.init(OfferRemoteData.getInstance(instance.executorsUtil), OrderRepository.getInstance());

			DeviceUtils.init(appContext);

			eventLogger.send(KinSdkInitiated.create());
		}
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

	public static void login(@NonNull WhitelistData whitelistData, KinCallback<Void> loginCallback) {
		SignInData signInData = getWhiteListSignInData(whitelistData);
		internalLogin(signInData, loginCallback);

	}

	public static void login(@NonNull String jwt, KinCallback<Void> loginCallback) {
		SignInData signInData = getJwtSignInData(jwt);
		internalLogin(signInData, loginCallback);
	}

	private static SignInData getWhiteListSignInData(@NonNull final WhitelistData whitelistData) {
		return new SignInData()
			.signInType(SignInTypeEnum.WHITELIST)
			.userId(whitelistData.getUserID())
			.appId(whitelistData.getAppID())
			.apiKey(whitelistData.getApiKey());
	}

	private static SignInData getJwtSignInData(@NonNull final String jwt) {
		return new SignInData()
			.signInType(SignInTypeEnum.JWT)
			.jwt(jwt);
	}

	private static void internalLogin(@NonNull SignInData signInData, final KinCallback<Void> loginCallback) {
		String publicAddress = null;
		try {
			checkInstanceNotNull();
			try {
				BlockchainSourceImpl.getInstance().createAccount();
				publicAddress = getPublicAddress();
			} catch (final BlockchainException exception) {
				instance.executorsUtil.mainThread().execute(new Runnable() {
					@Override
					public void run() {
						loginCallback.onFailure(exception);
					}
				});
			}
		} catch (final ClientException exception) {
			instance.executorsUtil.mainThread().execute(new Runnable() {
				@Override
				public void run() {
					loginCallback.onFailure(exception);
				}
			});
		}

		String deviceID = AuthRepository.getInstance().getDeviceID();
		signInData.setDeviceId(deviceID != null ? deviceID : UUID.randomUUID().toString());
		signInData.setWalletAddress(publicAddress);
		AuthRepository.getInstance().setSignInData(signInData);

		ObservableData<String> observableData = AuthRepository.getInstance().getAppID();
		String appID = observableData.getValue();
		if (appID == null) {
			observableData.addObserver(new Observer<String>() {
				@Override
				public void onChanged(String appID) {
					BlockchainSourceImpl.getInstance().setAppID(appID);
				}
			});
		}
		BlockchainSourceImpl.getInstance().setAppID(appID);

		AuthRepository.getInstance().getAuthToken(new KinCallback<AuthToken>() {
			@Override
			public void onResponse(AuthToken response) {
				isAccountLoggedIn = true;
				instance.executorsUtil.mainThread().execute(new Runnable() {
					@Override
					public void run() {
						loginCallback.onResponse(null);
					}
				});

				if (!AccountManagerImpl.getInstance().isAccountCreated()) {
					AccountManagerImpl.getInstance().start();
				}
			}

			@Override
			public void onFailure(final KinEcosystemException exception) {
				isAccountLoggedIn = false;
				instance.executorsUtil.mainThread().execute(new Runnable() {
					@Override
					public void run() {
						loginCallback.onFailure(exception);
					}
				});
			}
		});
	}

	private static boolean isInstanceNull() {
		return instance == null;
	}

	private static void checkAccountIsLoggedIn() throws ClientException {
		if (!isAccountLoggedIn) {
			throw ErrorUtil.getClientException(ACCOUNT_NOT_LOGGED_IN, null);
		}
	}

	private static void checkInstanceNotNull() throws ClientException {
		if (isInstanceNull()) {
			throw ErrorUtil.getClientException(SDK_NOT_STARTED, null);
		}
	}

	/**
	 * Launch Kin Marketplace if the user is activated, otherwise it will launch Welcome to Kin page.
	 *
	 * @param activity the activity user can go back to.
	 * @throws ClientException - sdk not initialized or account not logged in.
	 */
	public static void launchMarketplace(@NonNull final Activity activity) throws ClientException {
		checkInstanceNotNull();
		checkAccountIsLoggedIn();
		eventLogger.send(EntrypointButtonTapped.create());
		boolean isAccountCreated = AccountManagerImpl.getInstance().isAccountCreated();
		if (isAccountCreated) {
			navigateToMarketplace(activity);
		} else {
			navigateToSplash(activity);
		}
	}

	private static void navigateToSplash(@NonNull final Activity activity) {
		activity.startActivity(new Intent(activity, SplashActivity.class));
		activity.overridePendingTransition(R.anim.kinecosystem_slide_in_right, R.anim.kinecosystem_slide_out_left);
	}

	private static void navigateToMarketplace(@NonNull final Activity activity) {
		activity.startActivity(new Intent(activity, EcosystemActivity.class));
		activity.overridePendingTransition(R.anim.kinecosystem_slide_in_right, R.anim.kinecosystem_slide_out_left);
	}

	/**
	 * @return The account public address
	 * @throws ClientException - sdk not initialized or account not found.
	 */
	public static String getPublicAddress() throws BlockchainException, ClientException {
		checkInstanceNotNull();
		return BlockchainSourceImpl.getInstance().getPublicAddress();
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
}
