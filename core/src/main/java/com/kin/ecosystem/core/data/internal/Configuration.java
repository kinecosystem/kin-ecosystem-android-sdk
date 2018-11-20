package com.kin.ecosystem.core.data.internal;

import com.kin.ecosystem.common.KinEnvironment;

public interface Configuration {

	KinEnvironment getEnvironment();

	void setEnvironment(String name);

	interface Local {

		KinEnvironment getEnvironment();

		void setEnvironment(KinEnvironment kinEnvironment);
	}

}
