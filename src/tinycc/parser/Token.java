package tinycc.parser;

import tinycc.diagnostic.Locatable;
import tinycc.diagnostic.Location;
import tinycc.util.Util;

/**
 * Represents a single token. It contains the location and the text of a single
 * token.
 *
 */
public class Token extends Location {
	private final TokenKind kind;
	private final String text;


	/**
	 * Initializes a new token
	 *
	 * @param loc  The location of this token
	 * @param kind The type of the token
	 * @param text The text of the token
	 */
	public Token(final Locatable loc, final TokenKind kind, final String text) {
		super(loc);
		if (kind == null || text == null)
			throw new IllegalArgumentException();
		this.kind = kind;
		this.text = text;
	}

	/**
	 * Initializes a new token
	 *
	 * @param loc  The location of this token
	 * @param kind The type of the token
	 */
	public Token(final Locatable loc, final TokenKind kind) {
		this(loc, kind, kind.getText());
	}

	@Override
	public String toString() {
		switch (getKind()) {
		case CHARACTER:
			return Util.escapeString(text, '\'');
		case STRING:
			return Util.escapeString(text, '"');
		default:
			return text;
		}
	}

	/**
	 * Returns the kind of the token
	 *
	 * @return The kind of the token
	 * @see TokenKind
	 */
	public TokenKind getKind() {
		return kind;
	}

	/**
	 * The text of the token.
	 *
	 * Example:
	 * - "var" in the case of an identifier "var" (TokenKind.IDENTIFIER)
	 * - "3" in the case of a number (TokenKind.NUMBER)
	 * - "hi" in the case of string literal "hi" (TokenKind.STRING)
	 * - "a" in the case of character literal "a" (TokenKind.CHARACTER)
	 *
	 * @return The text of this token.
	 */
	public String getText() {
		return text;
	}
}
