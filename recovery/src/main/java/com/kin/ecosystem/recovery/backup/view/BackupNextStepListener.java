package com.kin.ecosystem.recovery.backup.view;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface BackupNextStepListener {

	int STEP_START = 0x00000000;
	int STEP_CREATE_PASSWORD = 0x00000001;
	int STEP_SAVE_AND_SHARE = 0x00000002;
	int STEP_WELL_DONE = 0x00000003;
	int STEP_CLOSE = 0x00000004;


	@IntDef({STEP_START, STEP_CREATE_PASSWORD, STEP_SAVE_AND_SHARE, STEP_WELL_DONE, STEP_CLOSE})
	@Retention(RetentionPolicy.SOURCE)
	@interface Step {

	}

	void setStep(@Step final int step, @Nullable final Bundle data);
}
