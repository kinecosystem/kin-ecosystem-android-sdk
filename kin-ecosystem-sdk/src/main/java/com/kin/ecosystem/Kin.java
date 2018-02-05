package com.kin.ecosystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.kin.ecosystem.exception.InitializeException;
import com.kin.ecosystem.exception.TaskFailedException;
import com.kin.ecosystem.marketplace.view.MarketplaceActivity;

import kin.sdk.core.Balance;
import kin.sdk.core.KinAccount;
import kin.sdk.core.KinClient;
import kin.sdk.core.ResultCallback;
import kin.sdk.core.exception.CreateAccountException;


public class Kin {
    private static Kin instance;
    private KinClient kinClient;

    private Kin() {
    }

    private static Kin getInstance() {
        if (instance == null) {
            synchronized (Kin.class) {
                instance = new Kin();
            }
        }

        return instance;
    }

    public static void start(@NonNull Context appContext, @NonNull String apiKey, String userID) throws InitializeException {
        instance = getInstance();
        instance.kinClient = new KinClient(appContext, StellarNetwork.NETWORK_TEST.getProvider());
        try {
            instance.kinClient.createAccount(""); // blockchain-sdk should generate and take care of that passphrase.
        } catch (CreateAccountException e) {
            throw new InitializeException(e.getMessage());
        }
        //TODO store apiKey and use to auth
        //TODO store userID and use to auth
    }

    private static void checkInstanceNotNull() throws TaskFailedException {
        if (instance == null) {
            throw new TaskFailedException("Kin.start(...) should be called first");
        }
    }

    public static void launchMarketplace(@NonNull Context context) throws TaskFailedException {
        checkInstanceNotNull();
        context.startActivity(new Intent(context, MarketplaceActivity.class));
        ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    public static String getPublicAddress() throws TaskFailedException {
        checkInstanceNotNull();
        KinAccount account = instance.kinClient.getAccount();
        if (account == null) {
            return null;
        }
        return account.getPublicAddress();
    }

    public static void getBalance(@NonNull final ResultCallback<Integer> balanceResult) throws TaskFailedException {
        checkInstanceNotNull();
        KinAccount account = instance.kinClient.getAccount();
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
