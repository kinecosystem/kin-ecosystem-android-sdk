package com.kin.ecosystem.recovery.backup.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kin.ecosystem.recovery.R;
import com.kin.ecosystem.recovery.backup.presenter.CreatePasswordPresenter;
import com.kin.ecosystem.recovery.backup.presenter.CreatePasswordPresenterImpl;

public class CreatePasswordFragment extends Fragment implements CreatePasswordView {

	public static CreatePasswordFragment newInstance(@NonNull final BackupNextStepListener nextStepListener) {
		CreatePasswordFragment fragment = new CreatePasswordFragment();
		fragment.setNextStepListener(nextStepListener);
		return fragment;
	}

	private BackupNextStepListener nextStepListener;
	private CreatePasswordPresenter createPasswordPresenter;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
		@Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.kinrecovery_fragment_backup_create_password, container, false);
		initViews(root);
		createPasswordPresenter = new CreatePasswordPresenterImpl(nextStepListener);
		createPasswordPresenter.onAttach(this);
		return root;
	}

	private void initViews(View root) {

	}

	private void setNextStepListener(@NonNull final BackupNextStepListener nextStepListener) {
		this.nextStepListener = nextStepListener;
	}
}
