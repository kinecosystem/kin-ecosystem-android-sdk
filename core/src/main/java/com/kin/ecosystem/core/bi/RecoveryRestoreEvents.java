package com.kin.ecosystem.core.bi;

import android.support.annotation.NonNull;
import com.kin.ecosystem.core.bi.events.RestoreAreYouSureCancelButtonTapped;
import com.kin.ecosystem.core.bi.events.RestoreAreYouSureOkButtonTapped;
import com.kin.ecosystem.core.bi.events.RestorePasswordDoneButtonTapped;
import com.kin.ecosystem.core.bi.events.RestorePasswordEntryBackButtonTapped;
import com.kin.ecosystem.core.bi.events.RestorePasswordEntryPageViewed;
import com.kin.ecosystem.core.bi.events.RestoreUploadQrCodeBackButtonTapped;
import com.kin.ecosystem.core.bi.events.RestoreUploadQrCodeButtonTapped;
import com.kin.ecosystem.core.bi.events.RestoreUploadQrCodePageViewed;
import com.kin.ecosystem.recovery.RestoreEvents;

public class RecoveryRestoreEvents implements RestoreEvents {

	private final EventLogger eventLogger;

	public RecoveryRestoreEvents(@NonNull EventLogger eventLogger) {
		this.eventLogger = eventLogger;
	}

	@Override
	public void onRestoreUploadQrCodePageViewed() {
		eventLogger.send(RestoreUploadQrCodePageViewed.create());
	}

	@Override
	public void onRestoreUploadQrCodeBackButtonTapped() {
		eventLogger.send(RestoreUploadQrCodeBackButtonTapped.create());
	}

	@Override
	public void onRestoreUploadQrCodeButtonTapped() {
		eventLogger.send(RestoreUploadQrCodeButtonTapped.create());
	}

	@Override
	public void onRestoreAreYouSureOkButtonTapped() {
		eventLogger.send(RestoreAreYouSureOkButtonTapped.create());
	}

	@Override
	public void onRestoreAreYouSureCancelButtonTapped() {
		eventLogger.send(RestoreAreYouSureCancelButtonTapped.create());
	}

	@Override
	public void onRestorePasswordEntryPageViewed() {
		eventLogger.send(RestorePasswordEntryPageViewed.create());
	}

	@Override
	public void onRestorePasswordEntryBackButtonTapped() {
		eventLogger.send(RestorePasswordEntryBackButtonTapped.create());
	}

	@Override
	public void onRestorePasswordDoneButtonTapped() {
		eventLogger.send(RestorePasswordDoneButtonTapped.create());
	}
}
