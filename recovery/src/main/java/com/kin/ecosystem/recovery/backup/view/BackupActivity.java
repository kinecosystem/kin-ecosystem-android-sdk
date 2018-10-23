package com.kin.ecosystem.recovery.backup.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import com.kin.ecosystem.recovery.R;
import com.kin.ecosystem.recovery.backup.presenter.BackupPresenter;
import com.kin.ecosystem.recovery.backup.presenter.BackupPresenterImpl;
import com.kin.ecosystem.recovery.base.BaseToolbarActivity;

public class BackupActivity extends BaseToolbarActivity implements BackupView {

	private BackupPresenter backupPresenter;

	@Override
	protected int getContentLayout() {
		return R.layout.kinrecovery_frgment_activity;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setNavigationClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backupPresenter.onBackClicked();
			}
		});
		backupPresenter = new BackupPresenterImpl();
		backupPresenter.onAttach(this);
	}

	public void startBackupFlow() {
		setToolbarColor(R.color.kinrecovery_bluePrimary);
		setNavigationIcon(R.drawable.kinrecovery_ic_back);
		setToolbarTitle(EMPTY_TITLE);
		BackupInfoFragment backupFragment = (BackupInfoFragment) getSupportFragmentManager()
			.findFragmentByTag(BackupInfoFragment.class.getSimpleName());

		if (backupFragment == null) {
			backupFragment = BackupInfoFragment.newInstance(backupPresenter);
		}

		getSupportFragmentManager().beginTransaction()
			.replace(R.id.fragment_frame, backupFragment)
			.commit();
	}

	@Override
	public void moveToSetPasswordPage() {
		setToolbarColorWithAnim(R.color.kinrecovery_white, 500);
		setNavigationIcon(R.drawable.kinrecovery_ic_back_black);
		setToolbarTitle(R.string.kinrecovery_keep_your_kin_safe);
		setStep(1, 2);
		CreatePasswordFragment backupFragment = (CreatePasswordFragment) getSupportFragmentManager()
			.findFragmentByTag(CreatePasswordFragment.class.getSimpleName());

		if (backupFragment == null) {
			backupFragment = CreatePasswordFragment.newInstance(backupPresenter);
		}

		getSupportFragmentManager().beginTransaction()
			.setCustomAnimations(
				R.anim.kinrecovery_slide_in_right,
				R.anim.kinrecovery_slide_out_left,
				R.anim.kinrecovery_slide_in_left,
				R.anim.kinrecovery_slide_out_right)
			.replace(R.id.fragment_frame, backupFragment)
			.commit();
	}

	@Override
	public void moveToSaveAndSharePage() {
		//TODO create save and share page
	}

	@Override
	public void backButtonClicked() {
		//TODO handle steps
		onBackPressed();
	}

	@Override
	public void close() {
		finish();
	}
}
