package tinycc.diagnostic;

/**
 * Basic diagnostic interface for error as well as information messages.
 */
public interface Diagnostic {

	/**
	 * Reports an error to the user which was caused at the given location
	 *
	 * @param location The location at which the error was reported
	 * @param fmt      A format string for the error message
	 * @param args     Given format arguments for the message
	 * @remarks The only parameter which will be tested is the location. The output
	 *          message and the arguments will not be tested.
	 */
	void printError(Locatable location, String fmt, Object... args);

	/**
	 * Reports an information message to the user which was caused at the given
	 * location
	 *
	 * @param location The location at which the error was reported
	 * @param fmt      A format string for the error message
	 * @param args     Given format arguments for the message
	 * @remarks This function is only used in the scope of the parser. You are not
	 *          required to report any information messages.
	 */
	void printNote(Locatable location, String fmt, Object... args);
}
