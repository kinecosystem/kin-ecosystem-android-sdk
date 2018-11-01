package com.kin.ecosystem.recovery.restore.view;


import com.kin.ecosystem.recovery.base.BaseView;

public interface UploadQRView extends BaseView {

	void showConsentDialog();

	void showErrorLoadingFileDialog();

	void showErrorDecodingQRDialog();
}
