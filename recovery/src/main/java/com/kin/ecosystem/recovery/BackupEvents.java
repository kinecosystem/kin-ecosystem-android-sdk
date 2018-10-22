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
    void onRstoreUploadQrCodePageViewed();
    void onRstoreUploadQrCodeBackButtonTapped();
    void onRstoreUploadQrCodeButtonTapped();
    void onRstoreAreYouSureOkButtonTapped();
    void onRstoreAreYouSureCancelButtonTapped();
    void onRstorePasswordEntryPageViewed();
    void onRstorePasswordEntryBackButtonTapped();
    void onRstorePasswordDoneButtonTapped();
}
