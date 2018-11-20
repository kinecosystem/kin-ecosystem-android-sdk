package com.kin.ecosystem.core.util;

public class Validator {

	public static void checkNotNull(Object obj, String paramName) {
		if (obj == null) {
			throw new IllegalArgumentException(paramName + " == null");
		}
	}

	public static boolean isEnvironmentName(String environmentName) {
		return environmentName.equals("beta") || environmentName.equals("prod");
	}
}
