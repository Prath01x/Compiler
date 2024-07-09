package tinycc.mipsasmgen;

public enum ImmediateRange {
	SIGNED16  (-32768, 32767),
	UNSIGNED16(     0, 65535),
	UNSIGNED5 (     0,    31);

	private final int min;
	private final int max;

	ImmediateRange(final int min, final int max) {
		this.min = min;
		this.max = max;
	}

	public boolean inRange(final int v) {
		return min <= v && v <= max;
	}
}
