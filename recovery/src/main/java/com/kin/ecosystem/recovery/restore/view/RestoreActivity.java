package com.kin.ecosystem.recovery.restore.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.kin.ecosystem.recovery.R;
import com.kin.ecosystem.recovery.base.BaseToolbarActivity;
import com.kin.ecosystem.recovery.events.BroadcastManagerImpl;
import com.kin.ecosystem.recovery.events.CallbackManager;
import com.kin.ecosystem.recovery.events.EventDispatcherImpl;
import com.kin.ecosystem.recovery.restore.presenter.RestorePresenter;
import com.kin.ecosystem.recovery.restore.presenter.RestorePresenterImpl;

public class RestoreActivity extends BaseToolbarActivity implements RestoreView {

	private RestorePresenter presenter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		presenter = new RestorePresenterImpl(
			new CallbackManager(new EventDispatcherImpl(new BroadcastManagerImpl(this))));
		presenter.onAttach(this);
	}

	@Override
	protected int getContentLayout() {
		return R.layout.kinrecovery_activity_restore;
	}

	@Override
	public void navigateToUpload() {
		UploadQRFragment fragment = (UploadQRFragment) getSupportFragmentManager()
			.findFragmentByTag(UploadQRFragment.class.getSimpleName());

		if (fragment == null) {
			fragment = UploadQRFragment.newInstance();
		}
		replaceFragment(fragment);
	}

	private void replaceFragment(Fragment fragment) {
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.fragment_frame, fragment)
			.commit();
	}

	@Override
	public void navigateToEnterPassword(String keystoreData) {
		RestoreEnterPasswordFragment fragment = (RestoreEnterPasswordFragment) getSupportFragmentManager()
			.findFragmentByTag(UploadQRFragment.class.getSimpleName());

		if (fragment == null) {
			fragment = RestoreEnterPasswordFragment.newInstance(keystoreData);
		}

		replaceFragment(fragment);
	}

	@Override
	public void navigateToEnterPassword() {
		navigateToEnterPassword(null);
	}

	@Override
	public void navigateToRestoreCompleted(Integer accountIndex) {
		RestoreCompletedFragment fragment = (RestoreCompletedFragment) getSupportFragmentManager()
			.findFragmentByTag(UploadQRFragment.class.getSimpleName());

		if (fragment == null) {
			fragment = RestoreCompletedFragment.newInstance(accountIndex);
		}

		replaceFragment(fragment);
	}

	@Override
	public void navigateToRestoreCompleted() {
		navigateToRestoreCompleted(-1);
	}

	@Override
	public void close() {
		finish();
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
