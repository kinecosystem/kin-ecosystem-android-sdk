package com.kin.ecosystem.recovery;

public interface BackupEvents {
    void onBackupWelcomePageViewed();
    void onBackupWelcomePageBackButtonTapped();
    void onBackupStartButtonTapped();
    void onBackupCreatePasswordPageViewed();
    void onBackupCreatePasswordBackButtonTapped();
    void onBackupCreatePasswordNextButtonTapped();
    void onBackupQrCodePageViewed();
    void onBackupQrCodeBackButtonTapped();
    void onBackupQrCodeSendButtonTapped();
    void onBackupQrCodeMyQrCodeButtonTapped();
    void onBackupCompletedPageViewed();
    void onBackupPopupPageViewed();
    void onBackupPopupButtonTapped();
    void onBackupPopupLaterButtonTapped();
    void onRestoreUploadQrCodePageViewed();
    void onRestoreUploadQrCodeBackButtonTapped();
    void onRestoreUploadQrCodeButtonTapped();
    void onRestoreAreYouSureOkButtonTapped();
    void onRestoreAreYouSureCancelButtonTapped();
    void onRestorePasswordEntryPageViewed();
    void onRestorePasswordEntryBackButtonTapped();
    void onRestorePasswordDoneButtonTapped();
}
