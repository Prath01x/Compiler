package tinycc.diagnostic;

import java.io.PrintStream;

/**
 * Default diagnostic module for the compiler.
 *
 * It simply prints out messages and remembers the error count.
 */
public final class PrintDiagnostic implements Diagnostic {
	private final PrintStream out;
	private int nErrors = 0;
	private boolean newErrors = false;

	public PrintDiagnostic(final PrintStream out) {
		this.out = out;
	}

	private void printDiagnostic(final String kind, final Locatable location, final String fmt, final Object... args) {
		if (location != null) {
			final int line = location.getLine();
			final int column = location.getColumn();
			final String locFmt = column != 0 ? "%s:%d:%d: " : line != 0 ? "%s:%d: " : "%s: ";
			out.printf(locFmt, location.getInputName(), line, column);
		}
		out.printf("%s: ", kind).printf(fmt, args).println();
	}

	@Override
	public void printError(final Locatable location, final String fmt, final Object... args) {
		++nErrors;
		newErrors = true;
		printDiagnostic("error", location, fmt, args);
	}

	@Override
	public void printNote(final Locatable location, final String fmt, final Object... args) {
		printDiagnostic("note", location, fmt, args);
	}

	/**
	 * Returns true if there were some errors since the last call and resets the new
	 * error flag.
	 *
	 * @return True if there were some errors since the last call
	 */
	public boolean hasNewErrors() {
		final boolean res = newErrors;
		newErrors = false;
		return res;
	}

	/**
	 * Prints an error summary: "52 error(s)"
	 *
	 * @return 1 if there were errors, 0 otherwise
	 */
	public int printSummary() {
		if (nErrors != 0) {
			out.printf("%d error(s)\n", nErrors);
			return 1;
		} else {
			return 0;
		}
	}
}
