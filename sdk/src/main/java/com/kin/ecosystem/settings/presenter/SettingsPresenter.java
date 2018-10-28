package com.kin.ecosystem.settings.presenter;

import static com.kin.ecosystem.settings.view.ISettingsView.BLUE;
import static com.kin.ecosystem.settings.view.ISettingsView.GRAY;
import static com.kin.ecosystem.settings.view.ISettingsView.ITEM_BACKUP;

import android.content.Intent;
import android.support.annotation.NonNull;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.model.Balance;
import com.kin.ecosystem.core.bi.EventLogger;
import com.kin.ecosystem.core.bi.RecoveryBackupEvents;
import com.kin.ecosystem.core.bi.RecoveryRestoreEvents;
import com.kin.ecosystem.core.data.blockchain.BlockchainSource;
import com.kin.ecosystem.core.data.settings.SettingsDataSource;
import com.kin.ecosystem.recovery.BackupCallback;
import com.kin.ecosystem.recovery.BackupManager;
import com.kin.ecosystem.recovery.RestoreCallback;
import com.kin.ecosystem.settings.view.ISettingsView;
import com.kin.ecosystem.settings.view.ISettingsView.IconColor;
import com.kin.ecosystem.settings.view.ISettingsView.Item;
import java.math.BigDecimal;

public class SettingsPresenter extends BasePresenter<ISettingsView> implements ISettingsPresenter {

	private final BackupManager backupManager;
	private final SettingsDataSource settingsDataSource;
	private final BlockchainSource blockchainSource;
	private final EventLogger eventLogger;

	private Observer<Balance> balanceObserver;
	private Balance currentBalance;

	public SettingsPresenter(@NonNull final ISettingsView view, @NonNull final SettingsDataSource settingsDataSource,
		@NonNull final BlockchainSource blockchainSource, @NonNull final BackupManager backupManager,
		@NonNull final EventLogger eventLogger) {
		this.view = view;
		this.backupManager = backupManager;
		this.settingsDataSource = settingsDataSource;
		this.blockchainSource = blockchainSource;
		this.eventLogger = eventLogger;
		this.currentBalance = blockchainSource.getBalance();
		registerToCallbacks();
		registerToEvents();

		this.view.attachPresenter(this);
	}

	@Override
	public void onAttach(ISettingsView view) {
		super.onAttach(view);
		updateSettingsIcon();
		backupClicked();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		removeBalanceObserver();
	}

	private void addBalanceObserver() {
		balanceObserver = new Observer<Balance>() {
			@Override
			public void onChanged(Balance value) {
				currentBalance = value;
				if (isGreaterThenZero(value)) {
					updateSettingsIcon();
				}
			}
		};
		blockchainSource.addBalanceObserver(balanceObserver);
	}

	private boolean isGreaterThenZero(Balance value) {
		return value.getAmount().compareTo(BigDecimal.ZERO) == 1;
	}

	private void updateSettingsIcon() {
		if (!settingsDataSource.isBackedUp()) {
			changeIconColor(ITEM_BACKUP, GRAY);
			if (isGreaterThenZero(currentBalance)) {
				changeTouchIndicator(ITEM_BACKUP, true);
				removeBalanceObserver();
			} else {
				addBalanceObserver();
				changeTouchIndicator(ITEM_BACKUP, false);
			}
		} else {
			changeIconColor(ITEM_BACKUP, BLUE);
			changeTouchIndicator(ITEM_BACKUP, false);
		}
	}

	private void removeBalanceObserver() {
		if (balanceObserver != null) {
			blockchainSource.removeBalanceObserver(balanceObserver);
		}
	}

	@Override
	public void backupClicked() {
		backupManager.backupFlow();
	}

	@Override
	public void restoreClicked() {
		backupManager.restoreFlow();
	}

	@Override
	public void backClicked() {
		if (view != null) {
			view.navigateBack();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		backupManager.onActivityResult(requestCode, resultCode, data);
	}

	private void registerToEvents() {
		backupManager.registerBackupEvents(new RecoveryBackupEvents(eventLogger));
		backupManager.registerRestoreEvents(new RecoveryRestoreEvents(eventLogger));
	}

	private void registerToCallbacks() {
		backupManager.registerBackupCallback(new BackupCallback() {
			@Override
			public void onSuccess() {
				onBackupSuccess();
			}

			@Override
			public void onCancel() {

			}

			@Override
			public void onFailure(Throwable throwable) {

			}
		});

		backupManager.registerRestoreCallback(new RestoreCallback() {
			@Override
			public void onSuccess(int index) {

			}

			@Override
			public void onCancel() {

			}

			@Override
			public void onFailure(Throwable throwable) {

			}
		});
	}

	private void onBackupSuccess() {
		settingsDataSource.setIsBackedUp(true);
		updateSettingsIcon();
	}

	private void changeTouchIndicator(@Item final int item, final boolean isVisible) {
		if (view != null) {
			view.changeTouchIndicatorVisibility(item, isVisible);
		}
	}

	private void changeIconColor(@Item final int item, @IconColor final int color) {
		if (view != null) {
			view.setIconColor(item, color);
		}
	}
}
