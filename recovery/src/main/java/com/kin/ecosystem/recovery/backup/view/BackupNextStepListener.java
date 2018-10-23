package com.kin.ecosystem.recovery.backup.view;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface BackupNextStepListener {

	int STEP_START = 0x00000000;
	int STEP_FIRST = 0x00000001;
	int STEP_SECOND = 0x00000002;
	int STEP_CLOSE = 0x00000003;



	@IntDef({STEP_START, STEP_FIRST, STEP_SECOND, STEP_CLOSE})
	@Retention(RetentionPolicy.SOURCE)
	@interface Step {

	}

	void setStep(@Step final int step);
}
