package com.kin.ecosystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import com.kin.ecosystem.data.auth.AuthLocalData;
import com.kin.ecosystem.data.auth.AuthRemoteData;
import com.kin.ecosystem.data.auth.AuthRepository;
import com.kin.ecosystem.data.blockchain.BlockchainSource;
import com.kin.ecosystem.data.offer.OfferRemoteData;
import com.kin.ecosystem.data.offer.OfferRepository;
import com.kin.ecosystem.data.order.OrderLocalData;
import com.kin.ecosystem.data.order.OrderRemoteData;
import com.kin.ecosystem.data.order.OrderRepository;
import com.kin.ecosystem.exception.InitializeException;
import com.kin.ecosystem.exception.TaskFailedException;
import com.kin.ecosystem.marketplace.view.MarketplaceActivity;
import com.kin.ecosystem.network.model.AuthToken;
import com.kin.ecosystem.network.model.SignInData;
import com.kin.ecosystem.splash.view.SplashViewActivity;
import com.kin.ecosystem.util.DeviceUtils;
import com.kin.ecosystem.util.ExecutorsUtil;


public class Kin {

    private static Kin instance;

    private final ExecutorsUtil executorsUtil;

    private Kin() {
        executorsUtil = new ExecutorsUtil();
    }

    private static Kin getInstance() {
        if (instance == null) {
            synchronized (Kin.class) {
                instance = new Kin();
            }
        }

        return instance;
    }

    public static void start(@NonNull Context appContext, @NonNull SignInData signInData)
        throws InitializeException {
        instance = getInstance();
        appContext = appContext.getApplicationContext(); // use application context to avoid leaks.
        DeviceUtils.init(appContext);
        initBlockchain(appContext, signInData.getAppId());
        registerAccount(appContext, signInData);
        initOfferRepository();
        initOrderRepository(appContext);
    }

    private static void initBlockchain(Context context, String appID) throws InitializeException {
        BlockchainSource.init(context, appID);
    }

    private static void registerAccount(@NonNull final Context context, @NonNull final SignInData signInData)
        throws InitializeException {
        String publicAddress = null;
        try {
            publicAddress = getPublicAddress();
            signInData.setPublicAddress(publicAddress);
            AuthRepository.init(signInData, AuthLocalData.getInstance(context, instance.executorsUtil),
                AuthRemoteData.getInstance(instance.executorsUtil));
        } catch (TaskFailedException e) {
            throw new InitializeException(e.getMessage());
        }
    }

    private static void initOfferRepository() {
        OfferRepository.init(OfferRemoteData.getInstance(instance.executorsUtil));
        OfferRepository.getInstance().getOffers(null);
    }

    private static void initOrderRepository(@NonNull final Context context) {
        OrderRepository.init(BlockchainSource.getInstance(), OfferRepository.getInstance(),
            OrderRemoteData.getInstance(instance.executorsUtil), OrderLocalData.getInstance(context, instance.executorsUtil));
    }

    private static void checkInstanceNotNull() throws TaskFailedException {
        if (instance == null) {
            throw new TaskFailedException("Kin.start(...) should be called first");
        }
    }

    public static void launchMarketplace(@NonNull final Activity activity) throws TaskFailedException {
        checkInstanceNotNull();
        boolean isActivated = AuthRepository.getInstance().isActivated();
        if (isActivated) {
            navigateToMarketplace(activity);
        } else {
            navigateToSplash(activity);
        }
    }

    private static void navigateToSplash(@NonNull final Activity activity) {
        activity.startActivity(new Intent(activity, SplashViewActivity.class));
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private static void navigateToMarketplace(@NonNull final Activity activity) {
        activity.startActivity(new Intent(activity, MarketplaceActivity.class));
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static String getPublicAddress() throws TaskFailedException {
        checkInstanceNotNull();
        return BlockchainSource.getInstance().getPublicAddress();
    }

    public static void getBalance(@NonNull final Callback<Integer> callback) throws TaskFailedException {
        checkInstanceNotNull();
        BlockchainSource.getInstance().getBalance(callback);
    }
}
