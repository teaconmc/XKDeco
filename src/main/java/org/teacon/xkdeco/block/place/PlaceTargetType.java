package org.teacon.xkdeco.block.place;

public enum PlaceTargetType {
	TEMPLATE("@"),
	BLOCK("");

	public final String prefix;

	PlaceTargetType(String prefix) {
		this.prefix = prefix;
	}
}
