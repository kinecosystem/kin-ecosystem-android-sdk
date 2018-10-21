package com.kin.ecosystem.recovery.restore.view;

import static com.kin.ecosystem.recovery.base.BaseToolbarActivity.EMPTY_TITLE;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import com.kin.ecosystem.recovery.R;
import com.kin.ecosystem.recovery.base.BaseToolbarActivity;
import com.kin.ecosystem.recovery.qr.QRBarcodeGeneratorImpl;
import com.kin.ecosystem.recovery.qr.QRFileUriHandlerImpl;
import com.kin.ecosystem.recovery.restore.presenter.FileSharingHelper;
import com.kin.ecosystem.recovery.restore.presenter.UploadQRPresenter;
import com.kin.ecosystem.recovery.restore.presenter.UploadQRPresenterImpl;
import com.kin.ecosystem.recovery.utils.ViewUtils;


public class UploadQRFragment extends Fragment implements UploadQRView {

	private UploadQRPresenter presenter;

	public static UploadQRFragment newInstance() {
		return new UploadQRFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
		@Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.kinrecovery_fragment_upload_qr, container, false);

		injectPresenter();
		presenter.onAttach(this);

		initToolbar();
		initViews(root);
		return root;
	}

	private void injectPresenter() {
		presenter = new UploadQRPresenterImpl(((RestoreActivity) getActivity()).getPresenter(),
			new FileSharingHelper(this),
			new QRBarcodeGeneratorImpl(new QRFileUriHandlerImpl(getContext())));
	}

	private void initViews(View root) {
		Group btnUploadGroup = root.findViewById(R.id.upload_btn_group);
		ViewUtils.registerToGroupOnClickListener(btnUploadGroup, root, new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				presenter.uploadClicked();
			}
		});
	}

	private void initToolbar() {
		BaseToolbarActivity toolbarActivity = (BaseToolbarActivity) getActivity();
		toolbarActivity.setNavigationIcon(R.drawable.kinrecovery_ic_back);
		toolbarActivity.setToolbarColor(R.color.kinrecovery_bluePrimary);
		toolbarActivity.setToolbarTitle(EMPTY_TITLE);
		toolbarActivity.setNavigationClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				presenter.onBackClicked();
			}
		});
	}

	@Override
	public void showConsentDialog() {
		new Builder(getActivity(), R.style.KinrecoveryAlertDialogTheme)
			.setTitle(R.string.kinrecovery_restore_consent_title)
			.setMessage(R.string.kinrecovery_consent_message)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					presenter.onConsent();
				}
			})
			.setNegativeButton(android.R.string.cancel, null)
			.create()
			.show();
	}

	@Override
	public void showErrorDecodingQRDialog() {
		Toast.makeText(getContext(), R.string.kinrecoevery_error_decoding_QR, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void showErrorLoadingFileDialog() {
		Toast.makeText(getContext(), R.string.kinrecovery_loading_file_error, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		presenter.onActivityResult(requestCode, resultCode, data);
	}
}
