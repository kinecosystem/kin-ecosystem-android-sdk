package com.kin.ecosystem.core.data.auth;

import static com.kin.ecosystem.core.data.auth.UserLoginState.DIFFERENT_USER;
import static com.kin.ecosystem.core.data.auth.UserLoginState.FIRST;
import static com.kin.ecosystem.core.data.auth.UserLoginState.SAME_USER;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({FIRST, SAME_USER, DIFFERENT_USER})
@Retention(RetentionPolicy.SOURCE)
public @interface UserLoginState {

	int FIRST = 0x00000000;
	int SAME_USER = 0x00000001;
	int DIFFERENT_USER = 0x00000002;
}
