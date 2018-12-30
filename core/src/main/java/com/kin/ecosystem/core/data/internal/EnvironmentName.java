package com.kin.ecosystem.core.data.internal;

import static com.kin.ecosystem.core.data.internal.EnvironmentName.BETA;
import static com.kin.ecosystem.core.data.internal.EnvironmentName.PRODUCTION;
import static com.kin.ecosystem.core.data.internal.EnvironmentName.TEST;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({TEST, BETA, PRODUCTION})
@Retention(RetentionPolicy.SOURCE)
public @interface EnvironmentName {

	String TEST = "test";
	String BETA = "beta";
	String PRODUCTION = "prod";
}
