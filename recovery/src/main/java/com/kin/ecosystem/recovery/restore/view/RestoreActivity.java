package com.kin.ecosystem.recovery.restore.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.kin.ecosystem.recovery.R;
import com.kin.ecosystem.recovery.base.BaseToolbarActivity;
import com.kin.ecosystem.recovery.events.BroadcastManagerImpl;
import com.kin.ecosystem.recovery.events.CallbackManager;
import com.kin.ecosystem.recovery.events.EventDispatcherImpl;
import com.kin.ecosystem.recovery.restore.presenter.RestorePresenter;
import com.kin.ecosystem.recovery.restore.presenter.RestorePresenterImpl;

public class RestoreActivity extends BaseToolbarActivity implements RestoreView {

	private RestorePresenter presenter;
	private String backStackName;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		presenter = new RestorePresenterImpl(
			new CallbackManager(new EventDispatcherImpl(new BroadcastManagerImpl(this))));
		presenter.onAttach(this);
	}

	@Override
	protected int getContentLayout() {
		return R.layout.kinrecovery_frgment_activity;
	}

	@Override
	public void navigateToUpload() {
		backStackName = UploadQRFragment.class.getSimpleName();
		UploadQRFragment fragment = (UploadQRFragment) getSupportFragmentManager()
			.findFragmentByTag(backStackName);

		if (fragment == null) {
			fragment = UploadQRFragment.newInstance();
		}
		replaceFragment(fragment, backStackName, false);
	}

	private void replaceFragment(Fragment fragment, String fragmentName, boolean addAnimation) {
		FragmentTransaction transaction = getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.fragment_frame, fragment)
			.addToBackStack(fragmentName);

		if (addAnimation) {
			transaction.setCustomAnimations(
				R.anim.kinrecovery_slide_in_right,
				R.anim.kinrecovery_slide_out_left,
				R.anim.kinrecovery_slide_in_left,
				R.anim.kinrecovery_slide_out_right);
		}
		transaction.commit();
	}

	@Override
	public void navigateToEnterPassword(String keystoreData) {
		backStackName = RestoreEnterPasswordFragment.class.getSimpleName();
		RestoreEnterPasswordFragment fragment = (RestoreEnterPasswordFragment) getSupportFragmentManager()
			.findFragmentByTag(backStackName);

		if (fragment == null) {
			fragment = RestoreEnterPasswordFragment.newInstance(keystoreData, this);
		}

		replaceFragment(fragment, backStackName, true);
	}

	@Override
	public void navigateToRestoreCompleted(Integer accountIndex) {
		backStackName = RestoreCompletedFragment.class.getSimpleName();
		RestoreCompletedFragment fragment = (RestoreCompletedFragment) getSupportFragmentManager()
			.findFragmentByTag(backStackName);

		if (fragment == null) {
			fragment = RestoreCompletedFragment.newInstance(accountIndex);
		}

		replaceFragment(fragment, backStackName, true);
	}

	@Override
	public void navigateBack() {
		getSupportFragmentManager().popBackStack(null, 0);
	}

	@Override
	public void close() {
		closeKeyboard(); // Verify the keyboard is hidden
		finish();
		overridePendingTransition(0, R.anim.kinrecovery_slide_out_right);
	}

	public RestorePresenter getPresenter() {
		return presenter;
	}

	@Override
	public void onBackPressed() {
		presenter.previousStep();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		presenter.onActivityResult(requestCode, resultCode, data);
	}
}