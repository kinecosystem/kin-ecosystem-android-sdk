package com.kin.ecosystem.main;

import static com.kin.ecosystem.main.ScreenId.MARKETPLACE;
import static com.kin.ecosystem.main.ScreenId.ORDER_HISTORY;
import static com.kin.ecosystem.main.ScreenId.SETTINGS;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({MARKETPLACE, ORDER_HISTORY, SETTINGS})
@Retention(RetentionPolicy.SOURCE)
public @interface ScreenId {

	int NONE = 0x00000000;
	int MARKETPLACE = 0x00000001;
	int ORDER_HISTORY = 0x00000002;
	int SETTINGS = 0x00000003;
}