package com.kin.ecosystem.transfer.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.KinEcosystemBaseActivity;
import com.kin.ecosystem.transfer.AccountInfoManager;
import com.kin.ecosystem.transfer.presenter.AccountInfoPresenter;
import com.kin.ecosystem.transfer.presenter.IAccountInfoPresenter;

public class AccountInfoActivity extends KinEcosystemBaseActivity implements IAccountInfoView {
    private IAccountInfoPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new AccountInfoPresenter(new AccountInfoManager(this), this, getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
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
    public void close() {
        finish();
    }

    @Override
    public void updateSourceApp(String sourceApp) {
        TextView title = findViewById(R.id.transfer_title);
        final CharSequence destinationApp = getApplicationInfo().loadLabel(getPackageManager());
        title.setText(getString(R.string.kinecosystem_transfer_title, sourceApp, destinationApp));
    }

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
                    presenter.closeClicked();
                }
            }
        });
    }

    @Override
    public void attachPresenter(AccountInfoPresenter presenter) {
        this.presenter = presenter;
        presenter.onAttach(this);
    }
}
