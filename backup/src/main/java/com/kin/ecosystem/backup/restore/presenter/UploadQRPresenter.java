package com.kin.ecosystem.backup.restore.presenter;


import android.content.Intent;
import com.kin.ecosystem.backup.base.BasePresenter;
import com.kin.ecosystem.backup.restore.view.UploadQRView;

public interface UploadQRPresenter extends BasePresenter<UploadQRView> {

	void uploadClicked();

	void onActivityResult(int requestCode, int resultCode, Intent data);
}
