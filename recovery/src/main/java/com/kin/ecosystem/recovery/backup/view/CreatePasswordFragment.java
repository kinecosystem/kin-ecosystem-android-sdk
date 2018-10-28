package com.kin.ecosystem.recovery.backup.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import com.kin.ecosystem.recovery.BackupManager;
import com.kin.ecosystem.recovery.R;
import com.kin.ecosystem.recovery.backup.presenter.CreatePasswordPresenter;
import com.kin.ecosystem.recovery.backup.presenter.CreatePasswordPresenterImpl;
import com.kin.ecosystem.recovery.backup.view.TextWatcherAdapter.TextChangeListener;
import com.kin.ecosystem.recovery.widget.PasswordEditText;

public class CreatePasswordFragment extends Fragment implements CreatePasswordView {

	public static CreatePasswordFragment newInstance(@NonNull final BackupNextStepListener nextStepListener) {
		CreatePasswordFragment fragment = new CreatePasswordFragment();
		fragment.setNextStepListener(nextStepListener);
		return fragment;
	}

	private BackupNextStepListener nextStepListener;
	private CreatePasswordPresenter createPasswordPresenter;

	private PasswordEditText enterPassEditText;
	private PasswordEditText confirmPassEditText;
	private CheckBox iUnderstandCheckbox;
	private Button nextButton;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
		@Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.kinrecovery_fragment_backup_create_password, container, false);
		initViews(root);
		createPasswordPresenter = new CreatePasswordPresenterImpl(nextStepListener,
			BackupManager.getKeyStoreProvider());
		createPasswordPresenter.onAttach(this);
		return root;
	}

	private void initViews(View root) {
		enterPassEditText = root.findViewById(R.id.enter_pass_edittext);
		confirmPassEditText = root.findViewById(R.id.confirm_pass_edittext);
		iUnderstandCheckbox = root.findViewById(R.id.understand_checkbox);
		nextButton = root.findViewById(R.id.next_button);

		setEnterPasswordTextChangeListener();
		setConfirmPasswordTextChangeListener();
		setCheckedListener();
		setNextButtonListener();

		enterPassEditText.setText("qwer1234Q!");
		confirmPassEditText.setText("qwer1234Q!");
	}

	private void setNextButtonListener() {
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createPasswordPresenter.nextButtonClicked(enterPassEditText.getText());
			}
		});
	}

	private void setCheckedListener() {
		iUnderstandCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				createPasswordPresenter.iUnderstandChecked(isChecked);
			}
		});
	}

	private void setEnterPasswordTextChangeListener() {
		enterPassEditText.addTextChangedListener(new TextWatcherAdapter(new TextChangeListener() {
			@Override
			public void afterTextChanged(Editable editable) {
				createPasswordPresenter.enterPasswordChanged(editable.toString());
			}
		}));
	}

	private void setConfirmPasswordTextChangeListener() {
		confirmPassEditText.addTextChangedListener(new TextWatcherAdapter(new TextChangeListener() {
			@Override
			public void afterTextChanged(Editable editable) {
				createPasswordPresenter.confirmPasswordChanged(enterPassEditText.getText(),
					editable.toString());
			}
		}));
	}

	private void setNextStepListener(@NonNull final BackupNextStepListener nextStepListener) {
		this.nextStepListener = nextStepListener;
	}

	@Override
	public void setEnterPasswordIsCorrect(boolean isCorrect) {
		if (isCorrect) {
			enterPassEditText.setFrameBackgroundColor(R.color.kinrecovery_bluePrimary);
			enterPassEditText.removeError();
		} else {
			enterPassEditText.setFrameBackgroundColor(R.color.kinrecovery_red);
			enterPassEditText.showError(R.string.kinrecovery_password_does_not_meet_req_above);
		}
	}

	@Override
	public void setConfirmPasswordIsCorrect(boolean isCorrect) {
		if (isCorrect) {
			confirmPassEditText.setFrameBackgroundColor(R.color.kinrecovery_bluePrimary);
			confirmPassEditText.removeError();
		} else {
			confirmPassEditText.setFrameBackgroundColor(R.color.kinrecovery_red);
			confirmPassEditText.showError(R.string.kinrecovery_password_does_not_match);
		}
	}

	@Override
	public void enableNextButton() {
		nextButton.setEnabled(true);
		nextButton.setClickable(true);
	}

	@Override
	public void disableNextButton() {
		nextButton.setEnabled(false);
		nextButton.setClickable(false);
	}

	@Override
	public void showBackupFailed() {
		Toast.makeText(getContext(), "Backup Failed", Toast.LENGTH_SHORT).show();
	}
}
