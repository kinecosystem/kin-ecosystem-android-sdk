package com.kin.ecosystem.recovery.events;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.kin.ecosystem.recovery.BackupEvents;
import com.kin.ecosystem.recovery.RestoreEvents;
import com.kin.ecosystem.recovery.events.BroadcastManager.Listener;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class EventDispatcherImpl implements EventDispatcher {

	@Nullable
	private BackupEvents backupEvents;
	@Nullable
	private RestoreEvents restoreEvents;

	@NonNull
	private final BroadcastManager broadcastManager;
	private Listener broadcastListener;

	//Backup Events Code
	public static final int BACKUP_WELCOME_PAGE_VIEWED = 70000;
	public static final int BACKUP_CREATE_PASSWORD_PAGE_VIEWED = 70001;
	public static final int BACKUP_QR_CODE_PAGE_VIEWED = 70002;
	public static final int BACKUP_COMPLETED_PAGE_VIEWED = 70003;

	@IntDef({BACKUP_WELCOME_PAGE_VIEWED, BACKUP_CREATE_PASSWORD_PAGE_VIEWED, BACKUP_QR_CODE_PAGE_VIEWED,
		BACKUP_COMPLETED_PAGE_VIEWED})
	@Retention(RetentionPolicy.SOURCE)
	@interface BackupEventCode {

	}

	//Restore Events Code
	public static final int RESTORE_UPLOAD_QR_CODE_PAGE_VIEWED = 80000;
	public static final int RESTORE_UPLOAD_QR_CODE_BUTTON_TAPPED = 80001;
	public static final int RESTORE_ARE_YOUR_SURE_CANCEL_TAPPED = 80002;
	public static final int RESTORE_PASSWORD_ENTRY_PAGE_VIEWED = 80003;
	public static final int RESTORE_PASSWORD_DONE_TAPPED = 80004;


	@IntDef({RESTORE_UPLOAD_QR_CODE_PAGE_VIEWED, RESTORE_UPLOAD_QR_CODE_BUTTON_TAPPED, RESTORE_ARE_YOUR_SURE_CANCEL_TAPPED,
		RESTORE_PASSWORD_ENTRY_PAGE_VIEWED, RESTORE_PASSWORD_DONE_TAPPED})
	@Retention(RetentionPolicy.SOURCE)
	@interface RestoreEventCode {

	}

	public EventDispatcherImpl(@NonNull final BroadcastManager broadcastManager) {
		this.broadcastManager = broadcastManager;
	}

	@Override
	public void setBackupEvents(@Nullable BackupEvents backupEvents) {
		this.backupEvents = backupEvents;
		if (backupEvents != null) {
			registerBroadcastListener();
		}
	}

	@Override
	public void setRestoreEvents(@Nullable RestoreEvents restoreEvents) {
		this.restoreEvents = restoreEvents;
		if (restoreEvents != null) {
			registerBroadcastListener();
		}
	}

	@Override
	public void sendEvent(@EventType final int eventType, final int eventID) {
		Intent data = new Intent();
		data.putExtra(EXTRA_KEY_EVENT_TYPE, eventType);
		data.putExtra(EXTRA_KEY_EVENT_ID, eventID);
		broadcastManager.sendEvent(data);
	}

	@Override
	public void sendCallback(int resultCode, Intent data) {
		broadcastManager.sendCallback(resultCode, data);
	}

	@Override
	public void unregister() {
		if (broadcastListener != null) {
			broadcastManager.unregister();
		}
	}

	private void registerBroadcastListener() {
		if (broadcastListener != null) {
			broadcastListener = new Listener() {
				@Override
				public void onReceive(Intent data) {
					parseData(data);
				}
			};
			broadcastManager.register(broadcastListener);
		}
	}

	private void parseData(Intent data) {
		final int eventType = data.getIntExtra(EXTRA_KEY_EVENT_TYPE, -1);
		final int eventID = data.getIntExtra(EXTRA_KEY_EVENT_ID, -1);
		if (eventType == BACKUP_EVENTS) {
			handleBackupEvents(eventID);
		} else {
			if (eventType == RESTORE_EVENTS) {
				handleRestoreEvents(eventID);
			}
		}
	}

	private void handleBackupEvents(@BackupEventCode int eventID) {
		if (backupEvents != null) {
			switch (eventID) {
				case BACKUP_WELCOME_PAGE_VIEWED:
					backupEvents.onBackupWelcomePageViewed();
					break;
				case BACKUP_CREATE_PASSWORD_PAGE_VIEWED:
					backupEvents.onBackupCreatePasswordPageViewed();
					break;
				case BACKUP_QR_CODE_PAGE_VIEWED:
					backupEvents.onBackupQrCodePageViewed();
					break;
				case BACKUP_COMPLETED_PAGE_VIEWED:
					backupEvents.onBackupCompletedPageViewed();
					break;
			}
		}
	}

	private void handleRestoreEvents(@RestoreEventCode int eventID) {
		if (restoreEvents != null) {
			switch (eventID) {
				case RESTORE_UPLOAD_QR_CODE_PAGE_VIEWED:
					restoreEvents.onRestoreUploadQrCodePageViewed();
					break;
				case RESTORE_UPLOAD_QR_CODE_BUTTON_TAPPED:
					restoreEvents.onRestoreUploadQrCodeButtonTapped();
					break;
				case RESTORE_ARE_YOUR_SURE_CANCEL_TAPPED:
					restoreEvents.onRestoreAreYouSureCancelButtonTapped();
					break;
				case RESTORE_PASSWORD_ENTRY_PAGE_VIEWED:
					restoreEvents.onRestorePasswordEntryPageViewed();
					break;
				case RESTORE_PASSWORD_DONE_TAPPED:
					restoreEvents.onRestorePasswordDoneButtonTapped();
					break;
			}
		}
	}

}
