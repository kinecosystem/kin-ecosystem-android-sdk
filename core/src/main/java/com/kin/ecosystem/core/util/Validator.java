package com.kin.ecosystem.core.util;

public class Validator {

	private static final String BETA = "beta";
	private static final String PRODUCTION = "prod";

	public static void checkNotNull(Object obj, String paramName) {
		if (obj == null) {
			throw new IllegalArgumentException(paramName + " == null");
		}
	}

	public static boolean isEnvironmentName(String environmentName) {
		return environmentName.equals(BETA) || environmentName.equals(PRODUCTION);
	}
}
