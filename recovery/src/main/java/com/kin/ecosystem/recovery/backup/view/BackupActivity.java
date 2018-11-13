package com.kin.ecosystem.recovery.backup.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import com.kin.ecosystem.recovery.R;
import com.kin.ecosystem.recovery.backup.presenter.BackupPresenter;
import com.kin.ecosystem.recovery.backup.presenter.BackupPresenterImpl;
import com.kin.ecosystem.recovery.base.BaseToolbarActivity;
import com.kin.ecosystem.recovery.events.BroadcastManagerImpl;
import com.kin.ecosystem.recovery.events.CallbackManager;
import com.kin.ecosystem.recovery.events.EventDispatcherImpl;

public class BackupActivity extends BaseToolbarActivity implements BackupView {

	public static final String MOVE_TO_SAVE_AND_SHARE = "move_to_save_and_share";
	public static final int TOOLBAR_COLOR_ANIM_DURATION = 500;
	private BackupPresenter backupPresenter;

	@Override
	protected int getContentLayout() {
		return R.layout.kinrecovery_frgment_activity;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		backupPresenter = new BackupPresenterImpl(new CallbackManager(new EventDispatcherImpl(new BroadcastManagerImpl(this))));
		backupPresenter.onAttach(this);
		setNavigationClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backupPresenter.onBackClicked();
			}
		});
	}

	@Override
	public void startBackupFlow() {
		setToolbarColor(R.color.kinrecovery_bluePrimary);
		setNavigationIcon(R.drawable.kinrecovery_ic_back);
		setToolbarTitle(EMPTY_TITLE);
		BackupInfoFragment backupInfoFragment = (BackupInfoFragment) getSupportFragmentManager()
			.findFragmentByTag(BackupInfoFragment.class.getSimpleName());

		if (backupInfoFragment == null) {
			backupInfoFragment = BackupInfoFragment.newInstance(backupPresenter);
		}

		getSupportFragmentManager().beginTransaction()
			.replace(R.id.fragment_frame, backupInfoFragment)
			.commit();
	}

	@Override
	public void moveToCreatePasswordPage() {
		setToolbarColorWithAnim(R.color.kinrecovery_white, TOOLBAR_COLOR_ANIM_DURATION);
		setNavigationIcon(R.drawable.kinrecovery_ic_back_black);
		setToolbarTitle(R.string.kinrecovery_keep_your_kin_safe);
		setStep(1, 2);
		CreatePasswordFragment createPasswordFragment = (CreatePasswordFragment) getSupportFragmentManager()
			.findFragmentByTag(CreatePasswordFragment.class.getSimpleName());

		if (createPasswordFragment == null) {
			createPasswordFragment = CreatePasswordFragment.newInstance(backupPresenter, this);
		}

		replaceFragment(createPasswordFragment, null);
	}

	@Override
	public void moveToSaveAndSharePage(String key) {
		setStep(2, 2);
		SaveAndShareFragment saveAndShareFragment = (SaveAndShareFragment) getSupportFragmentManager()
			.findFragmentByTag(SaveAndShareFragment.class.getSimpleName());

		if (saveAndShareFragment == null) {
			saveAndShareFragment = SaveAndShareFragment.newInstance(backupPresenter, key);
		}

		replaceFragment(saveAndShareFragment, MOVE_TO_SAVE_AND_SHARE);
	}

	@Override
	public void moveToWellDonePage() {
		setToolbarColorWithAnim(R.color.kinrecovery_bluePrimary, TOOLBAR_COLOR_ANIM_DURATION);
		setNavigationIcon(R.drawable.kinrecovery_close_icon);
		setToolbarTitle(EMPTY_TITLE);
		clearSteps();
		WellDoneBackupFragment wellDoneFragment = (WellDoneBackupFragment) getSupportFragmentManager()
			.findFragmentByTag(WellDoneBackupFragment.class.getSimpleName());

		if (wellDoneFragment == null) {
			wellDoneFragment = WellDoneBackupFragment.newInstance();
		}

		replaceFragment(wellDoneFragment, null);
	}

	private void replaceFragment(Fragment backupFragment, @Nullable String backStackName) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
			.setCustomAnimations(
				R.anim.kinrecovery_slide_in_right,
				R.anim.kinrecovery_slide_out_left,
				R.anim.kinrecovery_slide_in_left,
				R.anim.kinrecovery_slide_out_right)
			.replace(R.id.fragment_frame, backupFragment);

		if (backStackName != null) {
			transaction.addToBackStack(backStackName);
		}
		transaction.commit();
	}


	@Override
	public void close() {
		closeKeyboard(); // Verify the keyboard is hidden
		finish();
		overridePendingTransition(0, R.anim.kinrecovery_slide_out_right);
	}

	@Override
	public void onBackPressed() {
		backupPresenter.onBackClicked();
	}

	@Override
	protected void onStop() {
		super.onStop();
		closeKeyboard(); // Verify the keyboard is hidden
	}

	@Override
	public void onBackButtonClicked() {
		int count = getSupportFragmentManager().getBackStackEntryCount();
		super.onBackPressed();
		if (count == 0) {
			closeKeyboard(); // Verify the keyboard is hidden
			overridePendingTransition(0, R.anim.kinrecovery_slide_out_right);
		}
	}
}
