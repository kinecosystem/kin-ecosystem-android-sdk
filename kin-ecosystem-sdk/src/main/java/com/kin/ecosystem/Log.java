package com.kin.ecosystem;

import static com.kin.ecosystem.Logger.DEBUG;

import com.kin.ecosystem.Logger.Priority;

public class Log {
	private static String COLON = ": ";
	private static String COMMA = ", ";
	private static String SPACE = " ";

	private int priority = DEBUG;
	private String tag;
	private StringBuilder content = new StringBuilder();

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
