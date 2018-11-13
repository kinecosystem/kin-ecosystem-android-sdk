package com.kin.ecosystem.recovery.restore.presenter;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

public class FileSharingHelper {

	static final int REQUEST_RESULT_CANCELED = 0;
	static final int REQUEST_RESULT_OK = 1;
	static final int REQUEST_RESULT_FAILED = 2;

	private static final String INTENT_TYPE_ALL_IMAGE = "image/*";
	private static final int REQUEST_CODE_IMAGE = 800;
	private final Fragment fragment;

	public FileSharingHelper(Fragment fragment) {
		this.fragment = fragment;
	}

	void requestImageFile(String chooserTitle) {
		Intent intent;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			intent = new Intent(Intent.ACTION_OPEN_DOCUMENT)
				.addCategory(Intent.CATEGORY_OPENABLE)
				.setType(INTENT_TYPE_ALL_IMAGE);
		} else {
			intent = new Intent(Intent.ACTION_PICK)
			.setType(INTENT_TYPE_ALL_IMAGE);
		}
		fragment.startActivityForResult(Intent.createChooser(intent, chooserTitle), REQUEST_CODE_IMAGE);
	}

	RequestFileResult extractUriFromActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_IMAGE) {
			if (resultCode == Activity.RESULT_CANCELED) {
				return new RequestFileResult(REQUEST_RESULT_CANCELED, null);
			} else if (resultCode == Activity.RESULT_OK) {
				return new RequestFileResult(REQUEST_RESULT_OK, data.getData());
			}
		}
		return new RequestFileResult(REQUEST_RESULT_FAILED, null);
	}

	@NonNull
	public Intent getShareableIntent(@NonNull Uri uri) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		intent.setType("image/*");
		return intent;
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
