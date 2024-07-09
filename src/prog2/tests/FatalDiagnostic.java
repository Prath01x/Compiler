package prog2.tests;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;

public class FatalDiagnostic implements Diagnostic {
	/* set this true if you need some help debugging */
	private static final boolean PRINT_ERRORS = false;

	@Override
	public void printError(final Locatable location, final String fmt, final Object... args) {
		if (PRINT_ERRORS) {
			System.err.printf("%s:%d:%d: error: %s\n", location.getInputName(), location.getLine(),
					location.getColumn(), String.format(fmt, args));
		}
		throw new FatalCompilerError(location, String.format(fmt, args));
	}

	@Override
	public void printNote(final Locatable location, final String fmt, final Object... args) {
	}
}
