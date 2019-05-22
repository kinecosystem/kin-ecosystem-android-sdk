package com.kin.ecosystem.recovery.restore.view;


import static com.kin.ecosystem.recovery.restore.presenter.RestorePresenterImpl.KEY_ACCOUNT_KEY;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.kin.ecosystem.base.ThemeUtil;
import com.kin.ecosystem.recovery.BackupManager;
import com.kin.ecosystem.recovery.R;
import com.kin.ecosystem.recovery.backup.view.TextWatcherAdapter;
import com.kin.ecosystem.recovery.backup.view.TextWatcherAdapter.TextChangeListener;
import com.kin.ecosystem.recovery.base.BaseToolbarActivity;
import com.kin.ecosystem.recovery.base.KeyboardHandler;
import com.kin.ecosystem.recovery.events.BroadcastManagerImpl;
import com.kin.ecosystem.recovery.events.CallbackManager;
import com.kin.ecosystem.recovery.events.EventDispatcherImpl;
import com.kin.ecosystem.recovery.restore.presenter.RestoreEnterPasswordPresenter;
import com.kin.ecosystem.recovery.restore.presenter.RestoreEnterPasswordPresenterImpl;
import com.kin.ecosystem.recovery.widget.PasswordEditText;


public class RestoreEnterPasswordFragment extends Fragment implements RestoreEnterPasswordView {

	public static final int VIEW_MIN_DELAY_MILLIS = 150;

	private RestoreEnterPasswordPresenter presenter;
	private KeyboardHandler keyboardHandler;
	private Button doneBtn;
	private TextView contentText;
	private PasswordEditText password;
	private TextWatcherAdapter textWatcherAdapter;
	private int strokeRegularColor;

	public static RestoreEnterPasswordFragment newInstance(String keystoreData,
		@NonNull KeyboardHandler keyboardHandler) {
		RestoreEnterPasswordFragment fragment = new RestoreEnterPasswordFragment();
		fragment.setKeyboardHandler(keyboardHandler);
		if (keystoreData != null) {
			Bundle bundle = new Bundle();
			bundle.putString(KEY_ACCOUNT_KEY, keystoreData);
			fragment.setArguments(bundle);
		}
		return fragment;
	}

	public void setKeyboardHandler(@NonNull KeyboardHandler keyboardHandler) {
		this.keyboardHandler = keyboardHandler;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
		@Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.kinrecovery_fragment_password_restore, container, false);
		strokeRegularColor = ThemeUtil.Companion.themeAttributeToColor(getContext(), R.attr.buttonDisabledColor, R.color.kinecosystem_subtitle_gray);
		initToolbar();
		initViews(root);

		String keystoreData = extractKeyStoreData(savedInstanceState);
		injectPresenter(keystoreData);
		return root;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		presenter.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@NonNull
	private String extractKeyStoreData(@Nullable Bundle savedInstanceState) {
		String keystoreData;
		Bundle bundle = savedInstanceState != null ? savedInstanceState : getArguments();
		if (bundle == null) {
			throw new IllegalStateException("Bundle is null, can't extract required keystore data.");
		}
		keystoreData = bundle.getString(KEY_ACCOUNT_KEY);
		if (keystoreData == null) {
			throw new IllegalStateException("Can't find keystore data inside Bundle.");
		}
		return keystoreData;
	}

	private void injectPresenter(String keystoreData) {
		presenter = new RestoreEnterPasswordPresenterImpl(
			new CallbackManager(new EventDispatcherImpl(new BroadcastManagerImpl(getActivity()))), keystoreData,
			BackupManager.getKeyStoreProvider());
		presenter.onAttach(this, ((RestoreActivity) getActivity()).getPresenter());
	}

	private void initToolbar() {
		BaseToolbarActivity toolbarActivity = (BaseToolbarActivity) getActivity();
		toolbarActivity.setNavigationIcon(R.drawable.kinecosystem_ic_back_new);
		toolbarActivity.setToolbarTitle(R.string.kinrecovery_load_your_kin_from_backup);
		toolbarActivity.setNavigationClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				presenter.onBackClicked();
			}
		});
	}

	private void initViews(View root) {
		password = root.findViewById(R.id.kinrecovery_password_edit);
		contentText = root.findViewById(R.id.kinrecovery_password_recovery_text);
		doneBtn = root.findViewById(R.id.kinrecovery_password_recovery_btn);
		doneBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				presenter.restoreClicked(password.getText());
			}
		});
		textWatcherAdapter = new TextWatcherAdapter(new TextChangeListener() {
			@Override
			public void afterTextChanged(Editable editable) {
				presenter.onPasswordChanged(editable.toString());
			}
		});

		password.addTextChangedListener(textWatcherAdapter);
		password.postDelayed(new Runnable() {
			@Override
			public void run() {
				openKeyboard(password);
			}
		}, VIEW_MIN_DELAY_MILLIS);
	}

	private void openKeyboard(View view) {
		keyboardHandler.openKeyboard(view);
	}

	@Override
	public void enableDoneButton() {
		doneBtn.setEnabled(true);
		doneBtn.setClickable(true);
	}

	@Override
	public void disableDoneButton() {
		doneBtn.setEnabled(false);
		doneBtn.setClickable(false);
		password.setFrameBackgroundColor(strokeRegularColor);
	}

	@Override
	public void decodeError() {
		contentText.setText(R.string.kinrecovery_restore_password_error);
		contentText.setTextColor(ContextCompat.getColor(getContext(), R.color.kinecosystem_failed));
		password.setFrameBackgroundColorRes(R.color.kinecosystem_failed);
	}

	@Override
	public void invalidQrError() {
		contentText.setText(R.string.kinrecovery_restore_invalid_qr);
		contentText.setTextColor(ContextCompat.getColor(getContext(), R.color.kinecosystem_failed));
		password.setFrameBackgroundColorRes(R.color.kinecosystem_failed);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		textWatcherAdapter.release();
	}
}
