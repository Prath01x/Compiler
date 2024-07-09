package prog2.tests;

import tinycc.diagnostic.Locatable;

public class FatalCompilerError extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final Locatable location;
	private final String message;

	public FatalCompilerError(final Locatable location, final String message) {
		if (location == null)
			throw new NullPointerException("location should not be null");
		this.location = location;
		this.message = message;
	}

	public Locatable getLocatable() {
		return location;
	}

	@Override
	public String getMessage() {
		return String.format("%s:%d:%d: %s", location.getInputName(), location.getLine(), location.getColumn(),
				message);
	}
}
