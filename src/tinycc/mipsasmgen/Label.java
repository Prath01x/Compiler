package tinycc.mipsasmgen;

/**
 * A label for a segment of an emitted MIPS program.
 *
 * @see DataLabel
 * @see TextLabel
 */
public abstract class Label {
	private final String name;
	private final boolean externalVisible;

	/**
	 * Initializes a new label
	 *
	 * @param name            The specified name of the label
	 * @param externalVisible The label is visible to other translation units.
	 */
	Label(final String name, final boolean externalVisible) {
		if (name == null)
			throw new IllegalArgumentException();
		this.name = name;
		this.externalVisible = externalVisible;
	}

	/**
	 * Returns the name of the label
	 *
	 * @returns The name of the label
	 */
	@Override
	public String toString() {
		return name;
	}

	public boolean isExternalVisible() {
		return externalVisible;
	}
}
