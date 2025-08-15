package com.xtremand.domain.enums;

public enum EmailCategory {
	WELCOME("WELCOME"), FOLLOW_UP("FOLLOW_UP"), PROMOTION("PROMOTION"), NEWSLETTER("NEWSLETTER"),
	COLD_OUTREACH("COLD_OUTREACH"), NURTURE("NURTURE"), GENERAL("GENERAL");

	private final String value;

	EmailCategory(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static boolean contains(String category) {
		for (EmailCategory c : values()) {
			if (c.getValue().equalsIgnoreCase(category))
				return true;
		}
		return false;
	}
}
