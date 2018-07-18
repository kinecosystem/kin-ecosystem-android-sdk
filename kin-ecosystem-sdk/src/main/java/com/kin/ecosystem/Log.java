package com.kin.ecosystem;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;

public class Log {
	private static String COLON = ": ";
	private static String COMMA = ", ";
	private static String SPACE = " ";

	private int priority = DEBUG;
	private String tag;
	private StringBuilder content = new StringBuilder();

	public static final int DEBUG = 3;
	public static final int INFO = 4;
	public static final int WARN = 5;
	public static final int ERROR = 6;

	@IntDef({DEBUG, INFO, WARN, ERROR})
	@Retention(SOURCE)
	public @interface Priority {

	}

	public Log priority(@Priority final int priority) {
		this.priority = priority;
		return this;
	}

	public Log withTag(String tag) {
		this.tag = tag;
		return this;
	}

	public Log put(String name, Object value) {
		content.append(name)
			.append(COLON)
			.append(value)
			.append(COMMA);

		return this;
	}

	public Log text(String name) {
		content.append(name).append(SPACE);
		return this;
	}

	public void log() {
		Logger.log(priority, tag, content.substring(0, content.length() - 1));
	}
}
