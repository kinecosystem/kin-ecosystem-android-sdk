package com.kin.ecosystem.transfer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.kin.ecosystem.Kin;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.KinEcosystemBaseActivity;
import com.kin.ecosystem.common.exception.ClientException;

import java.io.File;

public class AccountInfoActivity extends KinEcosystemBaseActivity {
    private static final String EXTRA_SOURCE_APP_NAME = "EXTRA_SOURCE_APP_NAME";

    private AccountInfoManager accountInfoManager = new AccountInfoManager();
    private AccountInfoAsyncTask asyncTask;

    @Override
    protected int getLayoutRes() {
        return R.layout.kinecosystem_activity_account_info;
    }

    @Override
    protected void initViews() {
        final Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_SOURCE_APP_NAME)) {
            String sourceApp = intent.getStringExtra(EXTRA_SOURCE_APP_NAME);
            if (!sourceApp.isEmpty()) {
                TextView title = findViewById(R.id.transfer_title);
                final CharSequence destinationApp = getApplicationInfo().loadLabel(getPackageManager());
                title.setText(getString(R.string.ecosystem_transfer_title, sourceApp, destinationApp));
            } else {
                onError();
            }
        } else {
            onError();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        asyncTask = new AccountInfoAsyncTask();
        asyncTask.execute(getFilesDir());

        findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirm();
            }
        });

        findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel();
            }
        });
    }

    void onInitCompleted(Boolean success) {
        if (success) {
            findViewById(R.id.confirm_button).setEnabled(true);
        } else {
            onError();
        }
    }

    @Override
    public void onBackPressed() {
        accountInfoManager.respondCancel(AccountInfoActivity.this);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asyncTask.cancel(true);
        asyncTask = null;
    }

    private void onConfirm() {
        accountInfoManager.respondOk(AccountInfoActivity.this);
        finish();
    }

    private void onCancel() {
        accountInfoManager.respondCancel(AccountInfoActivity.this);
        finish();
    }

    private void onError() {
        accountInfoManager.respondCancel(AccountInfoActivity.this, true);
        finish();
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
            onInitCompleted(aBoolean);
        }

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
