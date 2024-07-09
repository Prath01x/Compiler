package tinycc.diagnostic;

/**
 * Represents an object which can be located in the input program
 */
public interface Locatable {

	/**
	 * Returns the file name of this element
	 *
	 * @return The file name of this element
	 */
	public String getInputName();

	/**
	 * Returns the line of this element in the input program
	 *
	 * @return The line of this element in the input program
	 */
	public int getLine();

	/**
	 * Returns the column of this element in the input program
	 *
	 * @return The column of this element in the input program
	 */
	public int getColumn();
}
