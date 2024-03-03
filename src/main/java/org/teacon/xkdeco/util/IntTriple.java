package org.teacon.xkdeco.util;

import java.util.Arrays;

public final class IntTriple {
	private static final IntTriple ZERO = new IntTriple(0, 0, 0);

	private final int[] values;

	private IntTriple(int left, int middle, int right) {
		this.values = new int[]{left, middle, right};
	}

	public static IntTriple of(int left, int middle, int right) {
		return (left | middle | right) == 0 ? ZERO : new IntTriple(left, middle, right);
	}

	public static boolean matchFrontBack(IntTriple front, IntTriple back) {
		var middleDiff = front.values[1] - back.values[1];
		var rightDiff = front.values[2] - back.values[0];
		var leftDiff = front.values[0] - back.values[2];
		return (middleDiff | rightDiff | leftDiff) == 0;
	}

	public static boolean matchDownUp(IntTriple down, IntTriple up) {
		var rightDiff = up.values[1] * (16 - down.values[2]) - up.values[2] * (16 - down.values[1]);
		var leftDiff = up.values[0] * (16 - down.values[1]) - up.values[1] * (16 - down.values[0]);
		return (rightDiff | leftDiff) == 0;
	}

	public int getLeft() {
		return this.values[0];
	}

	public int getMiddle() {
		return this.values[1];
	}

	public int getRight() {
		return this.values[2];
	}

	@Override
	public boolean equals(Object o) {
		return this == o || o instanceof IntTriple that && Arrays.equals(this.values, that.values);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.values);
	}

	@Override
	public String toString() {
		return "(" + this.values[0] + ", " + this.values[1] + ", " + this.values[2] + ")";
	}
}
