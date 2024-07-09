package tinycc.util;

public final class Util {
	private Util() {
	}

	public static boolean isOctalDigit(final int c) {
		return '0' <= c && c <= '7';
	}

	/**
	 * Escapes the given string and surrounds it with delimiters.
	 *
	 * @param s         The string to escape.
	 * @param delimiter The delimiter to use.
	 * @return An escaped string surrounded with delimiters.
	 */
	public static String escapeString(final String s, final char delimiter) {
		final StringBuilder b = new StringBuilder();
		b.append(delimiter);
		for (int i = 0, n = s.length(); i != n; ++i) {
			final char c = s.charAt(i);
			switch (c) {
			case '\\':  b.append("\\\\"); break;
			case '\b':  b.append("\\b");  break;
			case '\f':  b.append("\\f");  break;
			case '\n':  b.append("\\n");  break;
			case '\r':  b.append("\\r");  break;
			case '\t':  b.append("\\t");  break;

			default:
				if (32 <= c && c < 127) {
					if (c == delimiter)
						b.append('\\');
					b.append(c);
				} else if (0 <= c && c < 256) {
					final boolean octalFollows = i + 1 != n && isOctalDigit(s.charAt(i + 1));
					b.append(String.format(octalFollows ? "\\%03o" : "\\%o", (int) c));
				} else {
					throw new IllegalArgumentException("invalid character in text");
				}
				break;
			}
		}
		b.append(delimiter);
		return b.toString();
	}
}
