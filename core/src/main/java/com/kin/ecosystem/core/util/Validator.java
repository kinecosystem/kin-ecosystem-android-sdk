package com.kin.ecosystem.core.util;

import com.kin.ecosystem.core.data.internal.EnvironmentName;

public class Validator {

	public static void checkNotNull(Object obj, String paramName) {
		if (obj == null) {
			throw new IllegalArgumentException(paramName + " == null");
		}
	}

	public static boolean isEnvironmentName(String environmentName) {
		return environmentName.equals(EnvironmentName.BETA) || environmentName.equals(EnvironmentName.PRODUCTION);
	}
}
