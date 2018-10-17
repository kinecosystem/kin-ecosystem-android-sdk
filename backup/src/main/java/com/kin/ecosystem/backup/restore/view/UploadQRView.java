package com.kin.ecosystem.backup.restore.view;


import com.kin.ecosystem.backup.base.BaseView;

public interface UploadQRView extends BaseView {

	void showConsentDialog();

	void showErrorLoadingFileDialog();

	void showErrorDecodingQRDialog();
}
