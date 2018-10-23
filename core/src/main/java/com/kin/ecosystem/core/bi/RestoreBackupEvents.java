package com.kin.ecosystem.core.bi;

import android.support.annotation.NonNull;
import com.kin.ecosystem.core.bi.events.BackupCompletedPageViewed;
import com.kin.ecosystem.core.bi.events.BackupCreatePasswordBackButtonTapped;
import com.kin.ecosystem.core.bi.events.BackupCreatePasswordNextButtonTapped;
import com.kin.ecosystem.core.bi.events.BackupCreatePasswordPageViewed;
import com.kin.ecosystem.core.bi.events.BackupPopupButtonTapped;
import com.kin.ecosystem.core.bi.events.BackupPopupLaterButtonTapped;
import com.kin.ecosystem.core.bi.events.BackupPopupPageViewed;
import com.kin.ecosystem.core.bi.events.BackupQrCodeBackButtonTapped;
import com.kin.ecosystem.core.bi.events.BackupQrCodeMyqrcodeButtonTapped;
import com.kin.ecosystem.core.bi.events.BackupQrCodePageViewed;
import com.kin.ecosystem.core.bi.events.BackupQrCodeSendButtonTapped;
import com.kin.ecosystem.core.bi.events.BackupStartButtonTapped;
import com.kin.ecosystem.core.bi.events.BackupWelcomePageBackButtonTapped;
import com.kin.ecosystem.core.bi.events.BackupWelcomePageViewed;
import com.kin.ecosystem.core.bi.events.RestoreAreYouSureCancelButtonTapped;
import com.kin.ecosystem.core.bi.events.RestoreAreYouSureOkButtonTapped;
import com.kin.ecosystem.core.bi.events.RestorePasswordDoneButtonTapped;
import com.kin.ecosystem.core.bi.events.RestorePasswordEntryBackButtonTapped;
import com.kin.ecosystem.core.bi.events.RestorePasswordEntryPageViewed;
import com.kin.ecosystem.core.bi.events.RestoreUploadQrCodeBackButtonTapped;
import com.kin.ecosystem.core.bi.events.RestoreUploadQrCodeButtonTapped;
import com.kin.ecosystem.core.bi.events.RestoreUploadQrCodePageViewed;
import com.kin.ecosystem.recovery.BackupEvents;

public class RestoreBackupEvents implements BackupEvents {
    private final EventLogger eventLogger;

    public RestoreBackupEvents(@NonNull EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    @Override
    public void onBackupWelcomePageViewed() {
        eventLogger.send(BackupWelcomePageViewed.create());
    }

    @Override
    public void onBackupWelcomePageBackButtonTapped() {
        eventLogger.send(BackupWelcomePageBackButtonTapped.create());
    }

    @Override
    public void onBackupStartButtonTapped() {
        eventLogger.send(BackupStartButtonTapped.create());
    }

    @Override
    public void onBackupCreatePasswordPageViewed() {
        eventLogger.send(BackupCreatePasswordPageViewed.create());
    }

    @Override
    public void onBackupCreatePasswordBackButtonTapped() {
        eventLogger.send(BackupCreatePasswordBackButtonTapped.create());
    }

    @Override
    public void onBackupCreatePasswordNextButtonTapped() {
        eventLogger.send(BackupCreatePasswordNextButtonTapped.create());
    }

    @Override
    public void onBackupQrCodePageViewed() {
        eventLogger.send(BackupQrCodePageViewed.create());
    }

    @Override
    public void onBackupQrCodeBackButtonTapped() {
        eventLogger.send(BackupQrCodeBackButtonTapped.create());
    }

    @Override
    public void onBackupQrCodeSendButtonTapped() {
        eventLogger.send(BackupQrCodeSendButtonTapped.create());
    }

    @Override
    public void onBackupQrCodeMyQrCodeButtonTapped() {
        eventLogger.send(BackupQrCodeMyqrcodeButtonTapped.create());
    }

    @Override
    public void onBackupCompletedPageViewed() {
        eventLogger.send(BackupCompletedPageViewed.create());
    }

    @Override
    public void onBackupPopupPageViewed() {
        eventLogger.send(BackupPopupPageViewed.create());
    }

    @Override
    public void onBackupPopupButtonTapped() {
        eventLogger.send(BackupPopupButtonTapped.create());
    }

    @Override
    public void onBackupPopupLaterButtonTapped() {
        eventLogger.send(BackupPopupLaterButtonTapped.create());
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
