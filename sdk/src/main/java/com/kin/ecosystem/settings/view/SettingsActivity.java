package com.kin.ecosystem.settings.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import com.kin.ecosystem.R;
import com.kin.ecosystem.base.BaseToolbarActivity;
import com.kin.ecosystem.core.bi.EventLoggerImpl;
import com.kin.ecosystem.core.data.blockchain.BlockchainSourceImpl;
import com.kin.ecosystem.core.data.settings.SettingsDataSourceImpl;
import com.kin.ecosystem.core.data.settings.SettingsDataSourceLocal;
import com.kin.ecosystem.recovery.BackupManager;
import com.kin.ecosystem.settings.presenter.ISettingsPresenter;
import com.kin.ecosystem.settings.presenter.SettingsPresenter;

public class SettingsActivity extends BaseToolbarActivity implements ISettingsView, OnClickListener {

	private ISettingsPresenter settingsPresenter;

	private SettingsItem backupItem;
	private SettingsItem restoreItem;

	@Override
	protected int getLayoutRes() {
		return R.layout.kinecosystem_activity_settings;
	}

	@Override
	protected int getTitleRes() {
		return R.string.kinecosystem_settings;
	}

	@Override
	protected int getNavigationIcon() {
		return R.drawable.kinecosystem_ic_back_black;
	}

	@Override
	protected OnClickListener getNavigationClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				settingsPresenter.backClicked();
			}
		};
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		backupItem = findViewById(R.id.keep_your_kin_safe);
		restoreItem = findViewById(R.id.restore_prev_wallet);

		backupItem.setOnClickListener(this);
		restoreItem.setOnClickListener(this);

		settingsPresenter = new SettingsPresenter(this,
			new SettingsDataSourceImpl(new SettingsDataSourceLocal(getApplicationContext())),
			BlockchainSourceImpl.getInstance(),
			new BackupManager(this, BlockchainSourceImpl.getInstance().getKeyStoreProvider()),
			EventLoggerImpl.getInstance());
	}

	@Override
	protected void initViews() {

	}

	@Override
	public void attachPresenter(ISettingsPresenter presenter) {
		settingsPresenter = presenter;
		settingsPresenter.onAttach(this);
	}

	@Override
	public void onClick(View v) {
		final int vId = v.getId();
		if (vId == R.id.keep_your_kin_safe) {
			settingsPresenter.backupClicked();
		} else if (vId == R.id.restore_prev_wallet) {
			settingsPresenter.restoreClicked();
		}
	}

	@Override
	public void navigateBack() {
		onBackPressed();
		overridePendingTransition(0, R.anim.kinecosystem_slide_out_right);
	}

	@Override
	public void setIconColor(@Item final int item, @IconColor int color) {
		final SettingsItem settingsItem = getSettingsItem(item);
		if (settingsItem != null) {
			final @ColorRes int colorRes = getColorRes(color);
			if (colorRes != -1) {
				settingsItem.changeIconColor(colorRes);
			}
		}
	}

	@Override
	public void changeTouchIndicatorVisibility(@Item final int item, final boolean isVisible) {
		final SettingsItem settingsItem = getSettingsItem(item);
		if (settingsItem != null) {
			settingsItem.setTouchIndicatorVisibility(isVisible);
		}
	}

	private SettingsItem getSettingsItem(@Item final int item) {
		switch (item) {
			case ITEM_BACKUP:
				return backupItem;
			case ITEM_RESTORE:
				return restoreItem;
			default:
				return null;
		}
	}

	private int getColorRes(@IconColor final int color) {
		switch (color) {
			case BLUE:
				return R.color.kinecosystem_hot_blue;
			case GRAY:
				return R.color.kinecosystem_gray_dark;
			default:
				return -1;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		settingsPresenter.onActivityResult(requestCode, resultCode, data);
	}
}
