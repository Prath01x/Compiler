package prog2.tests;

import java.util.ArrayList;

import mars.ErrorMessage;
import mars.ProcessingException;

public class MarsException extends Exception {
	private static final long serialVersionUID = 570633487443717280L;
	private final String location;
	private final ProcessingException exc;

	public MarsException(final String location, final ProcessingException exc) {
		this.location = location;
		this.exc = exc;
	}

	@Override
	public String getMessage() {
		final StringBuilder sb = new StringBuilder();
		sb.append("MARS error during step: " + location + "\n");
		@SuppressWarnings("unchecked")
		final ArrayList<ErrorMessage> errors = exc.errors().getErrorMessages();
		for (final ErrorMessage error : errors) {
			sb.append(error.getMessage());
		}
		return sb.toString();
	}
}
