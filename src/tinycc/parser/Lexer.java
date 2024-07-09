package tinycc.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.ModifiableLocation;
import tinycc.util.Util;

/**
 * The TinyC lexer.
 */
public class Lexer {
	private final Diagnostic diagnostic;
	private final Reader reader;
	private final ModifiableLocation currentlocation;
	private final ModifiableLocation startLocation;
	private final StringBuilder id = new StringBuilder();

	private int c = '\n';

	private final Map<String, TokenKind> keywords = new HashMap<String, TokenKind>();

	/**
	 * Initializes a new lexer.
	 *
	 * @param diagnostic The diagnostic module to use.
	 * @param reader     The reader to read the input data from.
	 * @param inputName  The file name to use for each token location.
	 * @see Diagnostic
	 * @see Reader
	 */
	public Lexer(final Diagnostic diagnostic, final Reader reader, final String inputName) {
		if (diagnostic == null || reader == null)
			throw new IllegalArgumentException();
		this.diagnostic = diagnostic;
		this.reader = reader;
		this.currentlocation = new ModifiableLocation(inputName);
		this.startLocation = new ModifiableLocation(inputName);

		for (final TokenKind t : TokenKind.values()) {
			final String text = t.getText();
			if (Character.isJavaIdentifierStart(text.charAt(0)))
				keywords.put(text, t);
		}
	}

	// -------------- Internal methods of the lexer --------------

	private void nextChar() {
		currentlocation.incColumn();
		try {
			c = reader.read();
		} catch (final IOException e) {
			diagnostic.printError(currentlocation, "%s", e);
			c = -1;
		}
	}

	private boolean acceptChar(final int x) {
		if (c == x) {
			nextChar();
			return true;
		} else {
			return false;
		}
	}

	private Token parseString(final TokenKind kind, final int delimiter) {
		nextChar();
		final StringBuilder text = new StringBuilder();
		end: for (;;) {
			final char add;
			switch (c) {
			case -1:
				diagnostic.printError(startLocation, "end of input while parsing string");
				break end;

			case '\r':
			case '\n':
				diagnostic.printError(startLocation, "new line while parsing string");
				break end;

			case '\\': {
				nextChar();
				switch (c) {
				case -1:
					diagnostic.printError(startLocation, "end of input while parsing string");
					break end;

				case '"':  add = '"';   break;
				case '?':  add = '?';   break;
				case '\'': add = '\'';  break;
				case '\\': add = '\\';  break;
				case 'a':  add = '\7';  break;
				case 'b':  add = '\b';  break;
				case 'f':  add = '\f';  break;
				case 'n':  add = '\n';  break;
				case 'r':  add = '\r';  break;
				case 't':  add = '\t';  break;
				case 'v':  add = '\13'; break;

				default: {
					int v = 0;
					int n = 0;
					for (; n != 3 && Util.isOctalDigit(c); ++n) {
						v = v * 8 + (c - '0');
						nextChar();
					}
					if (n != 0) {
						final int octLimit = 0377;
						if (v > octLimit) {
							diagnostic.printError(currentlocation, "octal escape sequence '\\%o' out of range", v);
							v &= octLimit;
						}
						text.append((char) v);
					} else {
						diagnostic.printError(currentlocation, "invalid escape sequence '\\%c'", c);
					}
					continue;
				}
				}
				break;
			}

			default:
				if (acceptChar(delimiter))
					break end;
				add = (char)c;
				break;
			}
			text.append(add);
			nextChar();
		}
		return new Token(startLocation, kind, text.toString());
	}

	private Token makeToken(final TokenKind kind) {
		nextChar();
		return new Token(startLocation, kind);
	}

	private Token makeToken(final char tryChar, final TokenKind longKind, final TokenKind shortKind) {
		nextChar();
		final TokenKind kind = acceptChar(tryChar) ? longKind : shortKind;
		return new Token(startLocation, kind);
	}

	private Token makeToken(final char tryChar0, final TokenKind longKind0, final char tryChar1, final TokenKind longKind1, final TokenKind shortKind) {
		nextChar();
		final TokenKind kind =
				acceptChar(tryChar0) ? longKind0 :
					acceptChar(tryChar1) ? longKind1 :
						shortKind;
		return new Token(startLocation, kind);
	}

	/**
	 * Identifies, creates and returns the next token in the input stream
	 *
	 * @return The next token in the input stream
	 */
	Token next() {
		for (;;) {
			startLocation.set(currentlocation);
			switch (c) {
			case -1:
				return new Token(startLocation, TokenKind.EOF);

			case '\r':
			case '\n':
				consumeNewline();
				continue;

			case '\13':
			case '\f':
			case '\t':
			case ' ':
				break;

			case '\'': {
				final Token t = parseString(TokenKind.CHARACTER, c);
				if (t.getText().length() != 1)
					diagnostic.printError(t, "invalid character constant '%s'", t);
				return t;
			}

			case '"':
				return parseString(TokenKind.STRING, c);

			case '/':
				nextChar();
				switch (c) {
				case '*':
					skipMultilineComment();
					continue;
				case '/':
					skipLineComment();
					continue;
				default:
					return new Token(startLocation, TokenKind.SLASH);
				}

			case '@': return makeToken(TokenKind.ANNOT);
			case '%': return makeToken(TokenKind.PERCENT);
			case '(': return makeToken(TokenKind.LPAREN);
			case ')': return makeToken(TokenKind.RPAREN);
			case '*': return makeToken(TokenKind.ASTERISK);
			case ',': return makeToken(TokenKind.COMMA);
			case ':': return makeToken(TokenKind.COLON);
			case ';': return makeToken(TokenKind.SEMICOLON);
			case '?': return makeToken(TokenKind.QUESTION_MARK);
			case '[': return makeToken(TokenKind.LBRACKET);
			case ']': return makeToken(TokenKind.RBRACKET);
			case '^': return makeToken(TokenKind.HAT);
			case '{': return makeToken(TokenKind.LBRACE);
			case '}': return makeToken(TokenKind.RBRACE);
			case '~': return makeToken(TokenKind.TILDE);

			case '!': return makeToken('=', TokenKind.BANG_EQUAL,  TokenKind.BANG);
			case '&': return makeToken('&', TokenKind.AND_AND,     TokenKind.AND);
			case '+': return makeToken('+', TokenKind.PLUS_PLUS,   TokenKind.PLUS);
			case '-': return makeToken('-', TokenKind.MINUS_MINUS, TokenKind.MINUS);
			case '=': return makeToken('=', TokenKind.EQUAL_EQUAL, TokenKind.EQUAL);
			case '|': return makeToken('|', TokenKind.PIPE_PIPE,   TokenKind.PIPE);

			case '<': return makeToken('<', TokenKind.LESS_LESS,       '=', TokenKind.LESS_EQUAL,    TokenKind.LESS);
			case '>': return makeToken('>', TokenKind.GREATER_GREATER, '=', TokenKind.GREATER_EQUAL, TokenKind.GREATER);

			default:
				if (Character.isDigit(c)) {
					final int tmp = c;
					appendId();
					if (tmp != '0') {
						while (Character.isDigit(c)) {
							appendId();
						}
					}
					return new Token(startLocation, TokenKind.NUMBER, getId());
				} else if (Character.isJavaIdentifierStart(c)) {
					do {
						appendId();
					} while (Character.isJavaIdentifierPart(c));
					final String text = getId();
					final TokenKind idKind = keywords.get(text);
					return new Token(startLocation, idKind != null ? idKind : TokenKind.IDENTIFIER, text);
				} else {
					diagnostic.printError(startLocation, "invalid input character '%c'", c);
				}
			}
			nextChar();
		}
	}

	private void appendId() {
		id.append((char) c);
		nextChar();
	}

	private String getId() {
		final String text = id.toString();
		id.setLength(0);
		return text;
	}

	private void consumeNewline() {
		acceptChar('\r');
		acceptChar('\n');
		currentlocation.incLine();
	}

	private void skipLineComment() {
		for (;;) {
			nextChar();
			switch (c) {
			case '\r':
			case '\n':
				consumeNewline();
				return;

			case -1:
				return;
			}
		}
	}

	private void skipMultilineComment() {
		nextChar();
		for (;;) {
			switch (c) {
			case '*':
				nextChar();
				if (acceptChar('/'))
					return;
				break;

			case -1:
				diagnostic.printError(startLocation, "unterminated comment");
				return;

			case '\r':
			case '\n':
				consumeNewline();
				break;

			default:
				nextChar();
				break;
			}
		}
	}
}
