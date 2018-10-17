package com.kin.ecosystem.backup.restore.view;

import static com.kin.ecosystem.backup.base.BaseToolbarActivity.EMPTY_TITLE;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.kin.ecosystem.backup.R;
import com.kin.ecosystem.backup.base.BaseToolbarActivity;
import com.kin.ecosystem.backup.qr.QRBarcodeGeneratorImpl;
import com.kin.ecosystem.backup.qr.QRFileUriHandlerImpl;
import com.kin.ecosystem.backup.restore.presentation.FileSharingHelper;
import com.kin.ecosystem.backup.restore.presentation.UploadQRPresenterImpl;


public class UploadQRFragment extends Fragment implements UploadQRView {

	private UploadQRPresenterImpl presenter;

	public static UploadQRFragment newInstance() {
		return new UploadQRFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
		@Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.kinbackup_upload_qr_fragment, container, false);

		injectPresenter();
		presenter.onAttach(this);

		initToolbar();
		//initViews(root);
		//marketplacePresenter.onAttach(this);
		return root;
	}

	private void injectPresenter() {
		presenter = new UploadQRPresenterImpl(((RestoreActivity) getActivity()).getPresenter(),
			new FileSharingHelper(getActivity()),
			new QRBarcodeGeneratorImpl(new QRFileUriHandlerImpl(getContext())));
	}

	private void initToolbar() {
		BaseToolbarActivity toolbarActivity = (BaseToolbarActivity) getActivity();
		toolbarActivity.setNavigationIcon(R.drawable.kinbackup_ic_back);
		toolbarActivity.setToolbarColor(R.color.kinbackup_bluePrimary);
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

	}

	@Override
	public void showErrorLoadingFileDialog() {

	}

	@Override
	public void showErrorDecodingQRDialog() {

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		presenter.onActivityResult(requestCode, resultCode, data);
	}
}
