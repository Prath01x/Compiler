package tinycc.diagnostic;

/**
 * A location in the context of a source program.
 */
public class Location implements Locatable {
	private final String inputName;
	private final int line;
	private final int column;

	/**
	 * Initializes a new location with the given parameters.
	 *
	 * @param inputName The input name for this location.
	 * @param line      The line number of this location.
	 * @param column    The column number of this location.
	 */
	public Location(final String inputName, final int line, final int column) {
		if (inputName == null || line < 0 || column < 0)
			throw new IllegalArgumentException();
		this.inputName = inputName;
		this.line = line;
		this.column = column;
	}

	/**
	 * Initializes a new location with the given input name.
	 *
	 * @param inputName The input name for this location.
	 */
	public Location(final String inputName) {
		this(inputName, 0, 0);
	}

	/**
	 * Initializes the location with the given location.
	 *
	 * @param location The location to copy the data from.
	 */
	public Location(final Locatable location) {
		this(location.getInputName(), location.getLine(), location.getColumn());
	}

	@Override
	public final String getInputName() {
		return inputName;
	}

	@Override
	public final int getLine() {
		return line;
	}

	@Override
	public final int getColumn() {
		return column;
	}

	@Override
	public String toString() {
		return inputName + ":" + line + ":" + column;
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof Location))
			return false;
		final Location l = (Location) o;
		return inputName.equals(l.inputName) && line == l.line && column == l.column;
	}

	public static boolean equals(final Locatable a, final Locatable b) {
		return a.getInputName().equals(b.getInputName()) && a.getLine() == b.getLine()
				&& a.getColumn() == b.getColumn();
	}

	public static String toString(final Locatable l) {
		return l.getInputName() + ':' + l.getLine() + ':' + l.getColumn();
	}
}
