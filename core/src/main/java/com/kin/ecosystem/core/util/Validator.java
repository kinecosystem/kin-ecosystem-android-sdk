package com.kin.ecosystem.core.util;

public class Validator {

	public static void checkNotNull(Object obj, String paramName) {
		if (obj == null) {
			throw new IllegalArgumentException(paramName + " == null");
		}
	}
}
