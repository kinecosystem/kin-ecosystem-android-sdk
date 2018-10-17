package com.kin.ecosystem.backup.restore.presentation;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class FileSharingHelper {

	static final int REQUEST_RESULT_CANCELED = 0;
	static final int REQUEST_RESULT_OK = 1;
	static final int REQUEST_RESULT_FAILED = 2;

	private static final String INTENT_TYPE_ALL_IMAGE = "image/*";
	private static final int REQUEST_CODE_IMAGE = 800;
	private final Activity activity;

	public FileSharingHelper(Activity activity) {
		this.activity = activity;
	}

	void requestImageFile() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(INTENT_TYPE_ALL_IMAGE);
		activity.startActivityForResult(intent, REQUEST_CODE_IMAGE);
	}

	RequestFileResult extractUriFromActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_CANCELED) {
			return new RequestFileResult(REQUEST_RESULT_CANCELED, null);
		} else if (requestCode == Activity.RESULT_OK) {
			return new RequestFileResult(REQUEST_RESULT_OK, data.getData());
		} else {
			return new RequestFileResult(REQUEST_RESULT_FAILED, null);
		}
	}

	static class RequestFileResult {

		final int result;
		final Uri fileUri;

		RequestFileResult(int result, Uri fileUri) {
			this.result = result;
			this.fileUri = fileUri;
		}
	}
}
