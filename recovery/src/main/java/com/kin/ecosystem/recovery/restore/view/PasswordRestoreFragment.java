package com.kin.ecosystem.recovery.restore.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.kin.ecosystem.recovery.R;
import com.kin.ecosystem.recovery.base.BaseToolbarActivity;
import com.kin.ecosystem.recovery.restore.presenter.PasswordRestorePresenter;
import com.kin.ecosystem.recovery.restore.presenter.PasswordRestorePresenterImpl;
import com.kin.ecosystem.recovery.utils.ViewUtils;


public class PasswordRestoreFragment extends Fragment implements PasswordRestoreView {

	private PasswordRestorePresenter presenter;

	public static UploadQRFragment newInstance() {
		return new UploadQRFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
		@Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.kinrecovery_fragment_password_restore, container, false);

		injectPresenter();
		presenter.onAttach(this);

		initToolbar();
		initViews(root);
		return root;
	}

	private void injectPresenter() {
		presenter = new PasswordRestorePresenterImpl();
	}

	private void initToolbar() {
		BaseToolbarActivity toolbarActivity = (BaseToolbarActivity) getActivity();
		toolbarActivity.setNavigationIcon(R.drawable.kinrecovery_ic_back_black);
		toolbarActivity.setToolbarColor(R.color.kinrecovery_white);
		toolbarActivity.setToolbarTitle(R.string.kinrecovery_password_restore_title);
		toolbarActivity.setNavigationClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				presenter.onBackClicked();
			}
		});
	}

	private void initViews(View root) {
		Group doneBtn = root.findViewById(R.id.upload_btn_group);
		ViewUtils.registerToGroupOnClickListener(doneBtn, root, new OnClickListener() {
			@Override
			public void onClick(View v) {
				presenter.restoreClicked("");
			}
		});
	}

	@Override
	public void openKeyboard() {

	}

	@Override
	public void enableDone() {

	}

	@Override
	public void revealRestoreSucceed() {

	}

	@Override
	public void decodeError() {

	}
}
