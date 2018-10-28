package com.kin.ecosystem.recovery.restore.view;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.kin.ecosystem.recovery.R;
import com.kin.ecosystem.recovery.base.BaseToolbarActivity;
import com.kin.ecosystem.recovery.restore.presenter.RestoreEnterPasswordPresenter;
import com.kin.ecosystem.recovery.restore.presenter.RestoreEnterPasswordPresenterImpl;
import com.kin.ecosystem.recovery.utils.ViewUtils;


public class RestoreEnterPasswordFragment extends Fragment implements RestoreEnterPasswordView {

	private static final String BUNDLE_KEY_KEYSTORE = "BUNDLE_KEY_KEYSTORE";
	private RestoreEnterPasswordPresenter presenter;
	private View root;
	private Group doneBtn;
	private TextView contentText;

	public static RestoreEnterPasswordFragment newInstance(String keystoreData) {
		RestoreEnterPasswordFragment fragment = new RestoreEnterPasswordFragment();
		Bundle bundle = new Bundle();
		bundle.putString(BUNDLE_KEY_KEYSTORE, keystoreData);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
		@Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.kinrecovery_fragment_password_restore, container, false);

		String keystoreData = extractKeyStoreData(savedInstanceState);
		injectPresenter(keystoreData);
		presenter.onAttach(this, ((RestoreActivity) getActivity()).getPresenter());

		initToolbar();
		initViews(root);
		return root;
	}

	@NonNull
	private String extractKeyStoreData(@Nullable Bundle savedInstanceState) {
		String keystoreData;
		Bundle bundle = savedInstanceState != null ? savedInstanceState : getArguments();
		if (bundle == null) {
			throw new IllegalStateException("Bundle is null, can't extract required keystore data.");
		}
		keystoreData = bundle.getString(BUNDLE_KEY_KEYSTORE);
		if (keystoreData == null) {
			throw new IllegalStateException("Can't find keystore data inside Bundle.");
		}
		return keystoreData;
	}

	private void injectPresenter(String keystoreData) {
		presenter = new RestoreEnterPasswordPresenterImpl(keystoreData);
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
		this.root = root;
		final EditText password = root.findViewById(R.id.kinrecovery_password_edit);
		contentText = root.findViewById(R.id.kinrecovery_password_recovery_text);
		doneBtn = root.findViewById(R.id.btn_group);
		ViewUtils.setGroupEnable(doneBtn, root, false);
		ViewUtils.registerToGroupOnClickListener(doneBtn, root, new OnClickListener() {
			@Override
			public void onClick(View v) {
				presenter.restoreClicked(password.getText().toString());
			}
		});

		password.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				presenter.onPasswordChanged(s.toString());
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	@Override
	public void openKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getContext()
			.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null) {
			inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		}
	}

	@Override
	public void enableDoneButton() {
		ViewUtils.setGroupEnable(doneBtn, root, true);
	}

	@Override
	public void disableDoneButton() {
		ViewUtils.setGroupEnable(doneBtn, root, false);
	}

	@Override
	public void decodeError() {
		contentText.setText(R.string.kinrecovery_restore_password_error);
		contentText.setTextColor(ContextCompat.getColor(getContext(), R.color.kinrecovery_red));
	}

	@Override
	public void invalidQrError() {
		contentText.setText(R.string.kinrecovery_restore_invalid_qr);
		contentText.setTextColor(ContextCompat.getColor(getContext(), R.color.kinrecovery_red));
	}
}
