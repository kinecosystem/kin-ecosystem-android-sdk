package com.kin.ecosystem.recovery.restore.presenter;


import android.content.Intent;
import com.kin.ecosystem.recovery.base.BasePresenter;
import com.kin.ecosystem.recovery.restore.view.UploadQRView;

public interface UploadQRPresenter extends BasePresenter<UploadQRView> {

	void uploadClicked();

	void onActivityResult(int requestCode, int resultCode, Intent data);

	void onConsent();
}
