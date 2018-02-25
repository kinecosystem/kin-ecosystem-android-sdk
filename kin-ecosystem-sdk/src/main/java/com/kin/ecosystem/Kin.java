package com.kin.ecosystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.kin.ecosystem.data.auth.AuthLocalData;
import com.kin.ecosystem.data.auth.AuthRemoteData;
import com.kin.ecosystem.data.auth.AuthRepository;
import com.kin.ecosystem.data.offer.OfferRemoteData;
import com.kin.ecosystem.data.offer.OfferRepository;
import com.kin.ecosystem.data.order.OrderHistoryRemoteData;
import com.kin.ecosystem.data.order.OrderHistoryRepository;
import com.kin.ecosystem.exception.InitializeException;
import com.kin.ecosystem.exception.TaskFailedException;
import com.kin.ecosystem.marketplace.view.MarketplaceActivity;
import com.kin.ecosystem.network.model.SignInData;
import com.kin.ecosystem.util.DeviceUtils;
import com.kin.ecosystem.util.ExecutorsUtil;
import kin.core.Balance;
import kin.core.KinAccount;
import kin.core.KinClient;
import kin.core.ResultCallback;
import kin.core.exception.CreateAccountException;


public class Kin {

    private static Kin instance;
    private KinClient kinClient;

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
        instance.kinClient = new KinClient(appContext, StellarNetwork.NETWORK_TEST.getProvider());
        createKinAccountInNeeded();
        registerAccount(appContext, signInData);
        initOrderRepository();
        initOfferRepository();
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

    private static void initOrderRepository() {
        OrderHistoryRepository.init(OrderHistoryRemoteData.getInstance(instance.executorsUtil));
    }

    private static void createKinAccountInNeeded() throws InitializeException {
        try {
            KinAccount account = instance.kinClient.getAccount(0);
            if (account == null) {
                instance.kinClient.addAccount(""); // blockchain-sdk should generate and take care of that passphrase.
            }
        } catch (CreateAccountException e) {
            throw new InitializeException(e.getMessage());
        }
    }

    private static void checkInstanceNotNull() throws TaskFailedException {
        if (instance == null) {
            throw new TaskFailedException("Kin.start(...) should be called first");
        }
    }

    public static void launchMarketplace(@NonNull Activity activity) throws TaskFailedException {
        checkInstanceNotNull();
        activity.startActivity(new Intent(activity, MarketplaceActivity.class));
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    public static String getPublicAddress() throws TaskFailedException {
        checkInstanceNotNull();
        KinAccount account = instance.kinClient.getAccount(0);
        if (account == null) {
            return null;
        }
        return account.getPublicAddress();
    }

    public static void getBalance(@NonNull final ResultCallback<Integer> balanceResult) throws TaskFailedException {
        checkInstanceNotNull();
        KinAccount account = instance.kinClient.getAccount(0);
        if (account == null) {
            balanceResult.onError(new TaskFailedException("Account not found"));
        } else {
            account.getBalance().run(new ResultCallback<Balance>() {
                @Override
                public void onResult(Balance balance) {
                    balanceResult.onResult(balance.value().intValue());
                }

                @Override
                public void onError(Exception e) {
                    balanceResult.onError(e);
                }
            });
        }
    }
}
