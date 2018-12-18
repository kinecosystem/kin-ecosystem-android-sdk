package com.kin.ecosystem;

import static com.kin.ecosystem.EcosystemExperience.MARKETPLACE;
import static com.kin.ecosystem.EcosystemExperience.ORDER_HISTORY;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({MARKETPLACE, ORDER_HISTORY})
@Retention(RetentionPolicy.SOURCE)
public @interface EcosystemExperience {

	int NONE = 0x00000001;
	int MARKETPLACE = 0x00000002;
	int ORDER_HISTORY = 0x00000003;
}
