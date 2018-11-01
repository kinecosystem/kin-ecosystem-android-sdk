package com.kin.ecosystem.recovery.backup.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog.Builder;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.kin.ecosystem.recovery.BackupManager;
import com.kin.ecosystem.recovery.R;
import com.kin.ecosystem.recovery.backup.presenter.CreatePasswordPresenter;
import com.kin.ecosystem.recovery.backup.presenter.CreatePasswordPresenterImpl;
import com.kin.ecosystem.recovery.backup.view.TextWatcherAdapter.TextChangeListener;
import com.kin.ecosystem.recovery.base.KeyboardHandler;
import com.kin.ecosystem.recovery.events.BroadcastManagerImpl;
import com.kin.ecosystem.recovery.events.CallbackManager;
import com.kin.ecosystem.recovery.events.EventDispatcherImpl;
import com.kin.ecosystem.recovery.widget.PasswordEditText;

public class CreatePasswordFragment extends Fragment implements CreatePasswordView {

	public static CreatePasswordFragment newInstance(@NonNull final BackupNextStepListener nextStepListener,
		@NonNull final KeyboardHandler keyboardHandler) {
		CreatePasswordFragment fragment = new CreatePasswordFragment();
		fragment.setNextStepListener(nextStepListener);
		fragment.setKeyboardHandler(keyboardHandler);
		return fragment;
	}

	private BackupNextStepListener nextStepListener;
	private KeyboardHandler keyboardHandler;
	private CreatePasswordPresenter createPasswordPresenter;

	private PasswordEditText enterPassEditText;
	private PasswordEditText confirmPassEditText;
	private Button nextButton;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
		@Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.kinrecovery_fragment_backup_create_password, container, false);
		initViews(root);
		createPasswordPresenter = new CreatePasswordPresenterImpl(
			new CallbackManager(new EventDispatcherImpl(new BroadcastManagerImpl(getActivity()))), nextStepListener,
			BackupManager.getKeyStoreProvider());
		createPasswordPresenter.onAttach(this);
		return root;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final CheckBox iUnderstandCheckbox = view.findViewById(R.id.understand_checkbox);
		iUnderstandCheckbox.setChecked(false);
	}

	private void initViews(View root) {
		enterPassEditText = root.findViewById(R.id.enter_pass_edittext);
		confirmPassEditText = root.findViewById(R.id.confirm_pass_edittext);
		nextButton = root.findViewById(R.id.next_button);

		initEnterPasswordText();
		initConfirmPassword();
		setNextButtonListener();

		final CheckBox iUnderstandCheckbox = root.findViewById(R.id.understand_checkbox);
		iUnderstandCheckbox.post(new Runnable() {
			@Override
			public void run() {
				iUnderstandCheckbox.setChecked(false);
			}
		});
		iUnderstandCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				createPasswordPresenter.iUnderstandChecked(isChecked);
			}
		});

		root.findViewById(R.id.understand_description).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				iUnderstandCheckbox.performClick();
			}
		});
	}

	private void setNextButtonListener() {
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createPasswordPresenter.nextButtonClicked(enterPassEditText.getText());
			}
		});
	}

	private void initEnterPasswordText() {
		enterPassEditText.addTextChangedListener(new TextWatcherAdapter(new TextChangeListener() {
			@Override
			public void afterTextChanged(Editable editable) {
				createPasswordPresenter.enterPasswordChanged(editable.toString(), confirmPassEditText.getText());
			}
		}));
		enterPassEditText.setFrameBackgroundColor(R.color.kinrecovery_gray);
		openKeyboard(enterPassEditText);
	}

	private void initConfirmPassword() {
		confirmPassEditText.addTextChangedListener(new TextWatcherAdapter(new TextChangeListener() {
			@Override
			public void afterTextChanged(Editable editable) {
				createPasswordPresenter.confirmPasswordChanged(enterPassEditText.getText(),
					editable.toString());
			}
		}));
		confirmPassEditText.setFrameBackgroundColor(R.color.kinrecovery_gray);
	}

	private void setNextStepListener(@NonNull final BackupNextStepListener nextStepListener) {
		this.nextStepListener = nextStepListener;
	}

	private void setKeyboardHandler(KeyboardHandler keyboardHandler) {
		this.keyboardHandler = keyboardHandler;
	}

	private void openKeyboard(View view) {
		keyboardHandler.openKeyboard(view);
	}

	@Override
	public void closeKeyboard() {
		keyboardHandler.closeKeyboard();
	}

	@Override
	public void resetEnterPasswordField() {
		enterPassEditText.setFrameBackgroundColor(R.color.kinrecovery_gray);
		enterPassEditText.removeError();
	}

	@Override
	public void resetConfirmPasswordField() {
		confirmPassEditText.setFrameBackgroundColor(R.color.kinrecovery_gray);
		confirmPassEditText.removeError();
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
		new Builder(getActivity(), R.style.KinrecoveryAlertDialogTheme)
			.setTitle(R.string.kinrecovery_something_went_wrong_title)
			.setMessage(R.string.kinrecovery_we_had_some_issues_to_create_backup)
			.setPositiveButton(R.string.kinrecovery_try_again, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					createPasswordPresenter.onRetryClicked(enterPassEditText.getText());
				}
			})
			.setNegativeButton(R.string.kinrecovery_cancel, null)
			.create()
			.show();
	}
}
