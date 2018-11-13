package com.kin.ecosystem;

import static com.kin.ecosystem.common.exception.ClientException.SDK_NOT_STARTED;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.KinEnvironment;
import com.kin.ecosystem.common.NativeOfferClickEvent;
import com.kin.ecosystem.common.ObservableData;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.common.model.Balance;
import com.kin.ecosystem.common.model.NativeOffer;
import com.kin.ecosystem.common.model.OrderConfirmation;
import com.kin.ecosystem.common.model.UserStats;
import com.kin.ecosystem.common.model.WhitelistData;
import com.kin.ecosystem.core.Configuration;
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
import com.kin.ecosystem.core.data.offer.OfferRemoteData;
import com.kin.ecosystem.core.data.offer.OfferRepository;
import com.kin.ecosystem.core.data.order.OrderLocalData;
import com.kin.ecosystem.core.data.order.OrderRemoteData;
import com.kin.ecosystem.core.data.order.OrderRepository;
import com.kin.ecosystem.core.network.model.SignInData;
import com.kin.ecosystem.core.network.model.SignInData.SignInTypeEnum;
import com.kin.ecosystem.core.network.model.UserProfile;
import com.kin.ecosystem.core.util.DeviceUtils;
import com.kin.ecosystem.core.util.ErrorUtil;
import com.kin.ecosystem.core.util.ExecutorsUtil;
import com.kin.ecosystem.main.view.EcosystemActivity;
import com.kin.ecosystem.splash.view.SplashActivity;
import java.util.UUID;
import kin.core.KinClient;
import kin.core.ServiceProvider;


public class Kin {

	private static final String KIN_ECOSYSTEM_STORE_PREFIX_KEY = "kinecosystem_store";
	private static volatile Kin instance;

	private final ExecutorsUtil executorsUtil;
	private final EventLogger eventLogger;

	private Kin() {
		executorsUtil = new ExecutorsUtil();
		eventLogger = EventLoggerImpl.getInstance();
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

	public static void start(@NonNull Context appContext, @NonNull WhitelistData whitelistData,
		@NonNull KinEnvironment environment)
		throws ClientException, BlockchainException {
		if (isInstanceNull()) {
			SignInData signInData = getWhiteListSignInData(whitelistData);
			init(appContext, signInData, environment);
		}
	}

	public static void start(@NonNull Context appContext, @NonNull String jwt, @NonNull KinEnvironment environment)
		throws ClientException, BlockchainException {
		if (isInstanceNull()) {
			SignInData signInData = getJwtSignInData(jwt);
			init(appContext, signInData, environment);
		}
	}

	public static void enableLogs(final boolean enableLogs) {
		Logger.enableLogs(enableLogs);
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

	private synchronized static void init(@NonNull Context appContext, @NonNull SignInData signInData,
		@NonNull KinEnvironment environment) throws ClientException, BlockchainException {
		Configuration.setEnvironment(environment);
		instance = getInstance();
		appContext = appContext.getApplicationContext(); // use application context to avoid leaks.
		DeviceUtils.init(appContext);
		initBlockchain(appContext);
		initAuthRepository(appContext, signInData);
		initEventCommonData(appContext);
		instance.eventLogger.send(KinSdkInitiated.create());
		initAccountManager(appContext);
		initOrderRepository(appContext);
		initOfferRepository();
		setAppID();
	}

	private static void initAccountManager(@NonNull final Context context) {
		AccountManagerImpl
			.init(AccountManagerLocal.getInstance(context), instance.eventLogger, AuthRepository.getInstance(),
				BlockchainSourceImpl.getInstance());
		if (!AccountManagerImpl.getInstance().isAccountCreated()) {
			AccountManagerImpl.getInstance().start();
		}
	}

	private static void initEventCommonData(@NonNull Context context) {
		EventCommonDataUtil.setBaseData(context);
	}

	private static void setAppID() {
		ObservableData<String> observableData = AuthRepository.getInstance().getAppID();
		String appID = observableData.getValue();
		observableData.addObserver(new Observer<String>() {
			@Override
			public void onChanged(String appID) {
				BlockchainSourceImpl.getInstance().setAppID(appID);
			}
		});

		BlockchainSourceImpl.getInstance().setAppID(appID);
	}

	private static void initBlockchain(@NonNull final Context context) throws BlockchainException {
		final String networkUrl = Configuration.getEnvironment().getBlockchainNetworkUrl();
		final String networkId = Configuration.getEnvironment().getBlockchainPassphrase();
		KinClient kinClient = new KinClient(context, new ServiceProvider(networkUrl, networkId) {
			@Override
			protected String getIssuerAccountId() {
				return Configuration.getEnvironment().getIssuer();
			}
		}, KIN_ECOSYSTEM_STORE_PREFIX_KEY);
		BlockchainSourceImpl.init(instance.eventLogger, kinClient, BlockchainSourceLocal.getInstance(context));
	}

	private static void initAuthRepository(@NonNull final Context context, @NonNull final SignInData signInData)
		throws ClientException {
		AuthRepository.init(AuthLocalData.getInstance(context),
			AuthRemoteData.getInstance(instance.executorsUtil));
		String deviceID = AuthRepository.getInstance().getDeviceID();
		signInData.setDeviceId(deviceID != null ? deviceID : UUID.randomUUID().toString());
		signInData.setWalletAddress(getPublicAddress());
		AuthRepository.getInstance().setSignInData(signInData);
	}

	private static void initOfferRepository() {
		OfferRepository.init(OfferRemoteData.getInstance(instance.executorsUtil), OrderRepository.getInstance());
		OfferRepository.getInstance().getOffers(null);
	}

	private static void initOrderRepository(@NonNull final Context context) {
		OrderRepository.init(BlockchainSourceImpl.getInstance(),
			instance.eventLogger,
			OrderRemoteData.getInstance(instance.executorsUtil),
			OrderLocalData.getInstance(context, instance.executorsUtil));
	}

	private static boolean isInstanceNull() {
		return instance == null;
	}

	private static void checkInstanceNotNull() throws ClientException {
		if (isInstanceNull()) {
			throw ErrorUtil.getClientException(SDK_NOT_STARTED,
				new IllegalStateException("Kin.start(...) should be called first"));
		}
	}

	/**
	 * Launch Kin Marketplace if the user is activated, otherwise it will launch Welcome to Kin page.
	 *
	 * @param activity the activity user can go back to.
	 */
	public static void launchMarketplace(@NonNull final Activity activity) throws ClientException {
		checkInstanceNotNull();
		instance.eventLogger.send(EntrypointButtonTapped.create());
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
	 */
	public static String getPublicAddress() throws ClientException {
		checkInstanceNotNull();
		return BlockchainSourceImpl.getInstance().getPublicAddress();
	}

	/**
	 * Get the cached balance, can be different from the current balance on the network.
	 *
	 * @return balance amount
	 */
	public static Balance getCachedBalance() throws ClientException {
		checkInstanceNotNull();
		return BlockchainSourceImpl.getInstance().getBalance();
	}

	/**
	 * Get the current account balance from the network.
	 *
	 * @param callback balance amount
	 */
	public static void getBalance(@NonNull final KinCallback<Balance> callback) throws ClientException {
		checkInstanceNotNull();
		BlockchainSourceImpl.getInstance().getBalance(callback);
	}

	/**
	 * Add balance observer to start getting notified when the balance is changed on the blockchain network. On balance
	 * changes you will get {@link Balance} with the balance amount.
	 *
	 * Take in consideration that on adding this observer, a live network connection will be open to the blockchain
	 * network, In order to close the connection use {@link #removeBalanceObserver(Observer)} with the same observer. If
	 * no other observers on this connection, the connection will be closed.
	 */
	public static void addBalanceObserver(@NonNull final Observer<Balance> observer) throws ClientException {
		checkInstanceNotNull();
		BlockchainSourceImpl.getInstance().addBalanceObserverAndStartListen(observer);
	}

	/**
	 * Remove the balance observer, this method will close the live network connection to the blockchain network
	 * if there is no more observers.
	 */
	public static void removeBalanceObserver(@NonNull final Observer<Balance> observer) throws ClientException {
		checkInstanceNotNull();
		BlockchainSourceImpl.getInstance().removeBalanceObserverAndStopListen(observer);
	}

	/**
	 * Allowing your users to purchase virtual goods you define within your app, using KIN.
	 * This call might take time, due to transaction validation on the blockchain network.
	 *
	 * @param offerJwt Represents the offer in a JWT manner.
	 * @param callback {@link OrderConfirmation} The result will be a failure or a success with a jwt confirmation.
	 */
	public static void purchase(String offerJwt, @Nullable KinCallback<OrderConfirmation> callback)
		throws ClientException {
		checkInstanceNotNull();
		OrderRepository.getInstance().purchase(offerJwt, callback);
	}

	/**
	 * Allowing your users to earn Kin as a reward for native task you define.
	 * This call might take time, due to transaction validation on the blockchain network.
	 *
	 * @param offerJwt The offer details represented in a JWT manner.
	 * @param callback After validating the info and sending the payment to the user, you will receive {@link
	 * OrderConfirmation}, with the jwtConfirmation and you can validate the order when the order status is completed.
	 */
	public static void requestPayment(String offerJwt, @Nullable KinCallback<OrderConfirmation> callback)
		throws ClientException {
		checkInstanceNotNull();
		OrderRepository.getInstance().requestPayment(offerJwt, callback);
	}

	/**
	 * Allowing a user to pay to a different user for an offer defined within your app, using KIN.
	 * This call might take time, due to transaction validation on the blockchain network.
	 *
	 * @param offerJwt Represents a 'Pay to user' offer in a JWT manner.
	 * @param callback {@link OrderConfirmation} The result will be a failure or a success with a jwt confirmation.
	 */
	public static void payToUser(String offerJwt, @Nullable KinCallback<OrderConfirmation> callback)
		throws ClientException {
		checkInstanceNotNull();
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
	 * @throws ClientException
	 */
	public static void hasAccount(@NonNull String userId, @NonNull KinCallback<Boolean> callback)
		throws ClientException {
		checkInstanceNotNull();
		AuthRepository.getInstance().hasAccount(userId, callback);

	}

	/**
	 * Get user's stats which include history information such as number of Earn/Spend orders completed by the user or last earn/spend dates.
	 * This information could be used for re-engaging users, provide specific experience for users who never earn before etc.
	 * @param callback The result will be a {@link UserStats}
	 * @throws ClientException
	 */
	public static void userStats(@NonNull KinCallback<UserStats> callback)
		throws ClientException {
		checkInstanceNotNull();
		AuthRepository.getInstance().userStats(callback);
	}

	/**
	 * Returns a {@link OrderConfirmation}, with the order status and a jwtConfirmation if the order is completed.
	 *
	 * @param offerID The offerID that this order created from
	 */
	public static void getOrderConfirmation(@NonNull String offerID, @NonNull KinCallback<OrderConfirmation> callback)
		throws ClientException {
		checkInstanceNotNull();
		OrderRepository.getInstance().getExternalOrderStatus(offerID, callback);
	}

	/**
	 * Add a native offer {@link Observer} to receive a trigger when you native offers on Kin Marketplace are clicked.
	 */
	public static void addNativeOfferClickedObserver(@NonNull Observer<NativeOfferClickEvent> observer)
		throws ClientException {
		checkInstanceNotNull();
		OfferRepository.getInstance().addNativeOfferClickedObserver(observer);
	}

	/**
	 * Remove the callback if you no longer want to get triggered when your offer on Kin marketplace are clicked.
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
	 * @throws ClientException Could not add the offer to the list.
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
	 * @throws ClientException Could not remove the offer from the list.
	 */
	public static boolean removeNativeOffer(@NonNull NativeOffer nativeOffer) throws ClientException {
		checkInstanceNotNull();
		return OfferRepository.getInstance().removeNativeOffer(nativeOffer);
	}
}
