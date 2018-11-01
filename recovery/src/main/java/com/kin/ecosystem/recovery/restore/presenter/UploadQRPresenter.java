package com.kin.ecosystem.recovery.restore.presenter;


import android.content.Intent;
import com.kin.ecosystem.recovery.restore.view.UploadQRView;

public interface UploadQRPresenter extends BaseChildPresenter<UploadQRView> {

	void uploadClicked();

	void onActivityResult(int requestCode, int resultCode, Intent data);

	void onConsent(String chooserTitle);
}
