package com.kin.ecosystem.transfer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.kin.ecosystem.R;
import com.kin.ecosystem.base.KinEcosystemBaseActivity;
import com.kin.ecosystem.transfer.presenter.AccountInfoPresenter;
import com.kin.ecosystem.transfer.presenter.IAccountInfoPresenter;
import com.kin.ecosystem.transfer.view.IAccountInfoView;

public class AccountInfoActivity extends KinEcosystemBaseActivity implements IAccountInfoView {
    private static final String EXTRA_SOURCE_APP_NAME = "EXTRA_SOURCE_APP_NAME";
    private IAccountInfoPresenter presenter;

    @Override
    protected int getLayoutRes() {
        return R.layout.kinecosystem_activity_account_info;
    }

    @Override
    protected void initViews() {
        findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter != null) {
                    presenter.agreeClicked();
                }
            }
        });

        findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter != null) {
                    presenter.xCloseClicked();
                }
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachPresenter(new AccountInfoPresenter(getFilesDir(), this));
        if (!initIntent()) {
            if (presenter != null) {
                presenter.onError();
            }
        }
        if (presenter != null) {
            presenter.startInit();
        }
    }

    @Override
    public void onBackPressed() {
        if (presenter != null) {
            presenter.backButtonPressed();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.onDetach();
        }
        super.onDestroy();
    }

    @Override
    public void enabledAgreeButton() {
        findViewById(R.id.confirm_button).setEnabled(true);
    }

    @Override
    public void closeActivity() {
        finish();
    }

    @Override
    public void attachPresenter(AccountInfoPresenter presenter) {
        this.presenter = presenter;
        presenter.onAttach(this);
    }

    private boolean initIntent() {
        final Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_SOURCE_APP_NAME)) {
            String sourceApp = intent.getStringExtra(EXTRA_SOURCE_APP_NAME);
            if (!sourceApp.isEmpty()) {
                TextView title = findViewById(R.id.transfer_title);
                final CharSequence destinationApp = getApplicationInfo().loadLabel(getPackageManager());
                title.setText(getString(R.string.ecosystem_transfer_title, sourceApp, destinationApp));
                return true;
            }
        }
        return false;
    }


}
