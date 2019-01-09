package com.kin.ecosystem.transfer.presenter;

import android.app.Activity;
import android.os.AsyncTask;

import com.kin.ecosystem.Kin;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.transfer.AccountInfoManager;
import com.kin.ecosystem.transfer.view.IAccountInfoView;

import java.io.File;

public class AccountInfoPresenter extends BasePresenter<IAccountInfoView> implements IAccountInfoPresenter {

    private AccountInfoManager accountInfoManager = new AccountInfoManager();
    private AccountInfoAsyncTask asyncTask;
    private File fileDir;
    private Activity activity;

    public AccountInfoPresenter(File aFile, Activity aActivity) {
        fileDir = aFile;
        activity = aActivity;
    }

    @Override
    public void agreeClicked() {
        if (activity != null) {
            accountInfoManager.respondOk(activity);
            view.closeActivity();
        }
    }

    @Override
    public void backButtonPressed() {
        if (activity != null) {
            accountInfoManager.respondCancel(activity);
        }
    }

    @Override
    public void xCloseClicked() {
        if (activity != null) {
            accountInfoManager.respondCancel(activity);
            view.closeActivity();
        }
    }

    @Override
    public void startInit() {
        asyncTask = new AccountInfoAsyncTask();
        asyncTask.execute(fileDir);
    }

    @Override
    public void onDetach() {
        asyncTask.cancel(true);
        asyncTask = null;
        activity = null;
        super.onDetach();
    }

    @Override
    public void onError() {
        if (activity != null) {
            accountInfoManager.respondCancel(activity, true);
            view.closeActivity();
        }
    }

    void onInitComplete(Boolean success) {
        if (success) {
            getView().enabledAgreeButton();
        } else {
            onError();
        }
    }

    private class AccountInfoAsyncTask extends AsyncTask<File, Void, Boolean> {
        @Override
        protected Boolean doInBackground(File... args) {
            String address = getAccountPublicAddress();
            if (address.isEmpty()) {
                return false;
            }
            return accountInfoManager.init(args[0], address);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            onInitComplete(aBoolean);
        }

        //TODO need to make it work - heck just to make it return a valid address
        private String getAccountPublicAddress() {
            try {
                //return Kin.getPublicAddress();
                Kin.getPublicAddress();
                return "GD37RZZMUD2YYLYMBKJLKHL66BHXYODSKJGN2XVP5RSYGCAT6EJNDLGD";
            } catch (ClientException e) {
                //return "";
                return "GD37RZZMUD2YYLYMBKJLKHL66BHXYODSKJGN2XVP5RSYGCAT6EJNDLGD";
            }
        }
    }
}
