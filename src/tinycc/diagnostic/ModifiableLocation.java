package tinycc.diagnostic;

/**
 * A location object which can be modified
 */
public class ModifiableLocation implements Locatable {
	private String inputName;
	private int line;
	private int column;

	/**
	 * Initializes a new ModifiableLocation object with the given parameters
	 *
	 * @param inputName The input file name of this location object
	 * @param line      The initial line of this location object
	 * @param column    The initial column of this location object
	 * @see ModifiableLocation
	 */
	public ModifiableLocation(final String inputName, final int line, final int column) {
		if (inputName == null || line < 0 || column < 0)
			throw new IllegalArgumentException();
		this.inputName = inputName;
		this.line = line;
		this.column = column;
	}

	/**
	 * Initializes a new ModifiableLocation object with the given parameters
	 *
	 * @param inputName The input file name of this location object
	 * @see ModifiableLocation
	 */
	public ModifiableLocation(final String inputName) {
		this(inputName, 0, 0);
	}

	/**
	 * Initializes a new ModifiableLocation object with the given parameters
	 *
	 * @param location Another location object to copy the location from
	 * @see ModifiableLocation
	 */
	public ModifiableLocation(final Locatable location) {
		this(location.getInputName(), location.getLine(), location.getColumn());
	}

	/**
	 * Sets the current location information to the given one
	 *
	 * @param location The location object to copy the data from
	 */
	public final void set(final Locatable location) {
		inputName = location.getInputName();
		line = location.getLine();
		column = location.getColumn();
	}

	/**
	 * Increments the current line
	 */
	public final void incLine() {
		line += 1;
		column = 1;
	}

	/**
	 * Increments the current column
	 */
	public final void incColumn() {
		column += 1;
	}

	/**
	 * Returns the file name
	 *
	 * @return The input file name
	 */
	@Override
	public final String getInputName() {
		return inputName;
	}

	/**
	 * Returns the current line
	 *
	 * @return The current line
	 */
	@Override
	public final int getLine() {
		return line;
	}

	/**
	 * Returns the current column
	 *
	 * @return The current column
	 */
	@Override
	public final int getColumn() {
		return column;
	}

	@Override
	public String toString() {
		return inputName + ":" + line + ":" + column;
	}
}
