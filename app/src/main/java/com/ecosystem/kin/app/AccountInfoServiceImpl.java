package com.ecosystem.kin.app;

import com.ecosystem.kin.app.model.SignInRepo;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.common.model.WhitelistData;
import com.kin.ecosystem.transfer.AccountInfoService;
/*
*  Example usage of extending AccountInfoService
*  Name of class should not be changed
*
* */
public class AccountInfoServiceImpl extends AccountInfoService {
    public AccountInfoServiceImpl() {
        super("AccountInfoService");
    }

    @Override
    public void getAccountInfo(AccountInfoService.AccountInfoListener listener) {
        login(listener);
    }

    private void login(final AccountInfoService.AccountInfoListener listener) {
        if (BuildConfig.IS_JWT_REGISTRATION) {
            String jwt = SignInRepo.getJWT(this);

            Kin.login(jwt, new KinCallback<Void>() {
                @Override
                public void onResponse(Void response) {
                    try {
                        listener.onAccountInfoReady(Kin.getPublicAddress());
                    } catch (Exception e) {
                        listener.onAccountInfoFailed("Cant get Kin.getPublicAddress() " + e.getMessage());
                    }

                }

                @Override
                public void onFailure(KinEcosystemException exception) {
                    listener.onAccountInfoFailed(exception.getMessage());
                }
            });
        } else {
            /** Use {@link WhitelistData} for small scale testing */
            WhitelistData whitelistData = SignInRepo.getWhitelistSignInData(this, BuildConfig.SAMPLE_APP_ID, BuildConfig.SAMPLE_API_KEY);
            Kin.login(whitelistData, new KinCallback<Void>() {
                @Override
                public void onResponse(Void response) {
                    try {
                        listener.onAccountInfoReady(Kin.getPublicAddress());
                    } catch (Exception e) {
                        listener.onAccountInfoFailed("Cant get Kin.getPublicAddress() " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(KinEcosystemException exception) {
                    listener.onAccountInfoFailed(exception.getMessage());
                }
            });
        }
    }
}
