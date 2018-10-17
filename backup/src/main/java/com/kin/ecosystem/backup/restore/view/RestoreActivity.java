package com.kin.ecosystem.backup.restore.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.kin.ecosystem.backup.R;
import com.kin.ecosystem.backup.base.BaseToolbarActivity;
import com.kin.ecosystem.backup.restore.presentation.RestorePresenter;
import com.kin.ecosystem.backup.restore.presentation.RestorePresenterImpl;

public class RestoreActivity extends BaseToolbarActivity implements RestoreView {

	private RestorePresenter presenter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		presenter = new RestorePresenterImpl();
		presenter.onAttach(this);
	}

	@Override
	protected int getContentLayout() {
		return R.layout.kin_backup_restore_activity;
	}

	@Override
	public void navigateToUpload() {
		UploadQRFragment uploadFragment = (UploadQRFragment) getSupportFragmentManager()
			.findFragmentByTag(UploadQRFragment.class.getSimpleName());

		if (uploadFragment == null) {
			uploadFragment = UploadQRFragment.newInstance();
		}

		getSupportFragmentManager().beginTransaction()
			.replace(R.id.fragment_frame, uploadFragment)
			.commit();
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
}
