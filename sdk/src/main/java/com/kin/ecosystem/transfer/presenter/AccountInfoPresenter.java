package com.kin.ecosystem.transfer.presenter;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.common.exception.BlockchainException;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl;
import com.kin.ecosystem.transfer.AccountInfoManager;
import com.kin.ecosystem.transfer.view.IAccountInfoView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AccountInfoPresenter extends BasePresenter<IAccountInfoView> implements IAccountInfoPresenter {
    private static final String EXTRA_SOURCE_APP_NAME = "EXTRA_SOURCE_APP_NAME";
    private static final int TASK_STATE_UNDEFINED = 0;
    private static final int TASK_STATE_SUCCESS = 10;
    private static final int TASK_STATE_FAILURE = 20;

    @IntDef({TASK_STATE_UNDEFINED, TASK_STATE_SUCCESS, TASK_STATE_FAILURE})
    @Retention(RetentionPolicy.SOURCE)
    @interface TaskState {
    }

    private AccountInfoManager accountInfoManager;
    private AccountInfoAsyncTask asyncTask;
    private boolean isPaused = false;
    @TaskState
    private int taskState = TASK_STATE_UNDEFINED;

    public AccountInfoPresenter(@NonNull AccountInfoManager accountInfoManager, @NonNull IAccountInfoView view, @NonNull Intent intent) {
        this.accountInfoManager = accountInfoManager;
        processIntent(view, intent);
        view.attachPresenter(this);
        startAccountInfoTask();
    }

    private void processIntent(IAccountInfoView view, Intent intent) {
        if (intent != null && intent.hasExtra(EXTRA_SOURCE_APP_NAME)) {
            String sourceApp = intent.getStringExtra(EXTRA_SOURCE_APP_NAME);
            if (!sourceApp.isEmpty()) {
                view.updateSourceApp(sourceApp);
            } else {
                onError(view);
            }
        } else {
            onError(view);
        }
    }

    @Override
    public void agreeClicked() {
        if (accountInfoManager != null) {
            accountInfoManager.respondOk();
            if (view != null) {
                view.close();
            }
        }
    }

    @Override
    public void backButtonPressed() {
        if (accountInfoManager != null) {
            accountInfoManager.respondCancel();
        }
    }

    @Override
    public void closeClicked() {
        if (accountInfoManager != null) {
            accountInfoManager.respondCancel();
            if (view != null) {
                view.close();
            }
        }
    }

    @Override
    public void onDetach() {
        asyncTask.cancel(true);
        asyncTask = null;
        super.onDetach();
    }

    @Override
    public void onResume() {
        isPaused = false;
        checkTaskState();
    }

    @Override
    public void onPause() {
        isPaused = true;
    }

    private void startAccountInfoTask() {
        asyncTask = new AccountInfoAsyncTask();
        asyncTask.execute();
    }

    private void onError(IAccountInfoView view) {
        if (accountInfoManager != null) {
            accountInfoManager.respondError();
            if (view != null) {
                view.close();
            }
        }
    }

    private void onTaskComplete(Integer state) {
        taskState = state;
        if (!isPaused) {
            checkTaskState();
        }
    }

    private void checkTaskState() {
        if (taskState == TASK_STATE_SUCCESS) {
            if (view != null) {
                view.enabledAgreeButton();
            }
            taskState = TASK_STATE_UNDEFINED;
        } else if (taskState == TASK_STATE_FAILURE) {
            onError(getView());
            taskState = TASK_STATE_UNDEFINED;
        }
    }

    private class AccountInfoAsyncTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... args) {
            String address = getAccountPublicAddress();
            @TaskState int state = TASK_STATE_FAILURE;
            if (!address.isEmpty() && accountInfoManager != null) {
                if (accountInfoManager.init(address)) {
                    state = TASK_STATE_SUCCESS;
                }
            }
            return state;
        }

        @Override
        protected void onPostExecute(Integer state) {
            super.onPostExecute(state);
            onTaskComplete(state);
        }

        private String getAccountPublicAddress() {
            try {
                return BlockchainSourceImpl.getInstance().getPublicAddress();
            } catch (BlockchainException e) {
                return "";
            }
        }
    }
}
