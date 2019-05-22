package com.kin.ecosystem.recovery.backup.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.kin.ecosystem.recovery.R;
import com.kin.ecosystem.recovery.backup.presenter.BackupPresenter;
import com.kin.ecosystem.recovery.backup.presenter.BackupPresenterImpl;
import com.kin.ecosystem.recovery.base.BaseToolbarActivity;
import com.kin.ecosystem.recovery.events.BroadcastManagerImpl;
import com.kin.ecosystem.recovery.events.CallbackManager;
import com.kin.ecosystem.recovery.events.EventDispatcherImpl;

public class BackupActivity extends BaseToolbarActivity implements BackupView {

	public static final String MOVE_TO_SAVE_AND_SHARE = "move_to_save_and_share";
	public static final String TAG_WELL_DONE_PAGE = WellDoneBackupFragment.class.getSimpleName();
	public static final String TAG_SAVE_AND_SHARE_PAGE = SaveAndShareFragment.class.getSimpleName();
	public static final String TAG_CREATE_PASSWORD_PAGE = CreatePasswordFragment.class.getSimpleName();
	private BackupPresenter backupPresenter;

	@Override
	protected int getContentLayout() {
		return R.layout.kinrecovery_frgment_activity;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		backupPresenter = new BackupPresenterImpl(
			new CallbackManager(new EventDispatcherImpl(new BroadcastManagerImpl(this))), savedInstanceState);
		backupPresenter.onAttach(this);
		setNavigationClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backupPresenter.onBackClicked();
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		backupPresenter.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void startBackupFlow() {
		setNavigationIcon(R.drawable.kinecosystem_ic_back_new);
		setToolbarTitle(EMPTY_TITLE);
		BackupInfoFragment backupInfoFragment = (BackupInfoFragment) getSupportFragmentManager()
			.findFragmentByTag(BackupInfoFragment.class.getSimpleName());

		if (backupInfoFragment == null) {
			backupInfoFragment = BackupInfoFragment.newInstance(backupPresenter);
		} else {
			backupInfoFragment.setNextStepListener(backupPresenter);
		}

		getSupportFragmentManager().beginTransaction()
			.replace(R.id.fragment_frame, backupInfoFragment)
			.commit();
	}

	@Override
	public void moveToCreatePasswordPage() {
		setNavigationIcon(R.drawable.kinecosystem_ic_back_new);
		setToolbarTitle(R.string.kinrecovery_create_password);
		setStep(1, 2);
		CreatePasswordFragment createPasswordFragment = getSavedCreatePasswordFragment();

		if (createPasswordFragment == null) {
			createPasswordFragment = CreatePasswordFragment.newInstance(backupPresenter, this);
		} else {
			setCreatePasswordFragmentAttributes(createPasswordFragment);
		}

		replaceFragment(createPasswordFragment, null, TAG_CREATE_PASSWORD_PAGE);
	}

	@Override
	public void moveToSaveAndSharePage(String key) {
		setNavigationIcon(R.drawable.kinecosystem_ic_back_new);
		setToolbarTitle(R.string.kinrecovery_save_your_qr_code);
		setStep(2, 2);
		backupPresenter.setAccountKey(key);
		SaveAndShareFragment saveAndShareFragment = (SaveAndShareFragment) getSupportFragmentManager()
			.findFragmentByTag(TAG_SAVE_AND_SHARE_PAGE);

		if (saveAndShareFragment == null) {
			saveAndShareFragment = SaveAndShareFragment.newInstance(backupPresenter, key);
			replaceFragment(saveAndShareFragment, MOVE_TO_SAVE_AND_SHARE, TAG_SAVE_AND_SHARE_PAGE);
		} else {
			saveAndShareFragment.setNextStepListener(backupPresenter);
			// We should not add to back stack because it's already in stack.
			replaceFragment(saveAndShareFragment, null, TAG_SAVE_AND_SHARE_PAGE);
		}
	}

	@Override
	public void moveToWellDonePage() {
		setNavigationIcon(R.drawable.kinecosystem_ic_close_new);
		setToolbarTitle(EMPTY_TITLE);
		clearSteps();
		WellDoneBackupFragment wellDoneFragment = (WellDoneBackupFragment) getSupportFragmentManager()
			.findFragmentByTag(TAG_WELL_DONE_PAGE);

		if (wellDoneFragment == null) {
			wellDoneFragment = WellDoneBackupFragment.newInstance();
		}

		replaceFragment(wellDoneFragment, null, TAG_WELL_DONE_PAGE);
	}

	private void replaceFragment(Fragment backupFragment, @Nullable String backStackName, @NonNull String tag) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
			.setCustomAnimations(
				R.anim.kinrecovery_slide_in_right,
				R.anim.kinrecovery_slide_out_left,
				R.anim.kinrecovery_slide_in_left,
				R.anim.kinrecovery_slide_out_right)
			.replace(R.id.fragment_frame, backupFragment, tag);

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
	public void showError() {
		Toast.makeText(this, R.string.kinrecovery_something_went_wrong_title, Toast.LENGTH_SHORT).show();
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
		if(count >= 1) {
			BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(count - 1);
			if (entry.getName().equals(MOVE_TO_SAVE_AND_SHARE)) {
				// After pressing back from SaveAndShareFragment, should put the attrs again.
				// Because this is the only fragment that should be in stack.
				CreatePasswordFragment createPasswordFragment = getSavedCreatePasswordFragment();
				if (createPasswordFragment != null) {
					setCreatePasswordFragmentAttributes(createPasswordFragment);
				}
			}
		}
		super.onBackPressed();
		if (count == 0) {
			closeKeyboard(); // Verify the keyboard is hidden
			overridePendingTransition(0, R.anim.kinrecovery_slide_out_right);
		}
	}

	private void setCreatePasswordFragmentAttributes(CreatePasswordFragment createPasswordFragment) {
		createPasswordFragment.setNextStepListener(backupPresenter);
		createPasswordFragment.setKeyboardHandler(this);
	}

	private CreatePasswordFragment getSavedCreatePasswordFragment() {
		return (CreatePasswordFragment) getSupportFragmentManager()
			.findFragmentByTag(TAG_CREATE_PASSWORD_PAGE);
	}
}
