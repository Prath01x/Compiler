package tinycc.parser;

/**
 * The precedences for each token. Only relevant for the parser.
 */
enum Precedence {
	NONE,
	EXPRESSION,
	ASSIGNMENT,
	CONDITIONAL,
	LOGICAL_OR,
	LOGICAL_AND,
	OR,
	XOR,
	AND,
	EQUALITY,
	RELATIONAL,
	SHIFT,
	ADDITIVE,
	MULTIPLICATIVE,
	CAST,
	UNARY,
	POSTFIX,
	PRIMARY;

	/**
	 * Returns true if this precedence is smaller than the given one.
	 *
	 * @param p The other precedence to test
	 * @return True if this precedence is smaller than the given one.
	 */
	boolean less(final Precedence p) {
		return ordinal() < p.ordinal();
	}
}

/**
 * The type of a token.
 */
public enum TokenKind {
	// End of file token
	EOF("<end of file>"),

	// Primary expression tokens
	IDENTIFIER("<identifier>"),
	NUMBER("<number>"),
	CHARACTER("<character constant>"),
	STRING("<string literal>"),

	// Grouping tokens
	LBRACE("{"),
	RBRACE("}"),
	LPAREN("(", Precedence.POSTFIX),
	RPAREN(")"),
	LBRACKET("[", Precedence.POSTFIX),
	RBRACKET("]"),

	// Operator tokens
	TILDE          ("~"),
	BANG           ("!"),
	MINUS_MINUS    ("--", Precedence.POSTFIX),
	PLUS_PLUS      ("++", Precedence.POSTFIX),
	PERCENT        ("%",  Precedence.MULTIPLICATIVE, Precedence.CAST),
	SLASH          ("/",  Precedence.MULTIPLICATIVE, Precedence.CAST),
	ASTERISK       ("*",  Precedence.MULTIPLICATIVE, Precedence.CAST),
	MINUS          ("-",  Precedence.ADDITIVE,       Precedence.MULTIPLICATIVE),
	PLUS           ("+",  Precedence.ADDITIVE,       Precedence.MULTIPLICATIVE),
	LESS_LESS      ("<<", Precedence.SHIFT,          Precedence.ADDITIVE),
	GREATER_GREATER(">>", Precedence.SHIFT,          Precedence.ADDITIVE),
	LESS           ("<",  Precedence.RELATIONAL,     Precedence.SHIFT),
	GREATER        (">",  Precedence.RELATIONAL,     Precedence.SHIFT),
	LESS_EQUAL     ("<=", Precedence.RELATIONAL,     Precedence.SHIFT),
	GREATER_EQUAL  (">=", Precedence.RELATIONAL,     Precedence.SHIFT),
	EQUAL_EQUAL    ("==", Precedence.EQUALITY,       Precedence.RELATIONAL),
	BANG_EQUAL     ("!=", Precedence.EQUALITY,       Precedence.RELATIONAL),
	AND            ("&",  Precedence.AND,            Precedence.EQUALITY),
	HAT            ("^",  Precedence.XOR,            Precedence.AND),
	PIPE           ("|",  Precedence.OR,             Precedence.XOR),
	AND_AND        ("&&", Precedence.LOGICAL_AND,    Precedence.OR),
	PIPE_PIPE      ("||", Precedence.LOGICAL_OR,     Precedence.LOGICAL_AND),
	QUESTION_MARK  ("?",  Precedence.CONDITIONAL,    Precedence.CONDITIONAL),
	EQUAL          ("=",  Precedence.ASSIGNMENT,     Precedence.ASSIGNMENT),
	COMMA          (",",  Precedence.EXPRESSION,     Precedence.ASSIGNMENT),

	// Annotations
	ANNOT("@"),

	// Delimiter tokens
	COLON(":"),
	SEMICOLON(";"),

	// Keyword tokens
	AUTO("auto"),
	BREAK("break"),
	CASE("case"),
	CHAR("char"),
	CONST("const"),
	CONTINUE("continue"),
	DEFAULT("default"),
	DO("do"),
	DOUBLE("double"),
	ELSE("else"),
	ENUM("enum"),
	EXTERN("extern"),
	FLOAT("float"),
	FOR("for"),
	GOTO("goto"),
	IF("if"),
	INLINE("inline"),
	INT("int"),
	LONG("long"),
	REGISTER("register"),
	RESTRICT("restrict"),
	RETURN("return"),
	SHORT("short"),
	SIGNED("signed"),
	SIZEOF("sizeof"),
	STATIC("static"),
	STRUCT("struct"),
	SWITCH("switch"),
	TYPEDEF("typedef"),
	UNION("union"),
	UNSIGNED("unsigned"),
	VOID("void"),
	VOLATILE("volatile"),
	WHILE("while"),
	_BOUND("_Bound"),
	_INVARIANT("_Invariant"),
	_TERM("_Term"),
	_ASSUME("_Assume"),
	_ASSERT("_Assert"),
	_ALIGNAS("_Alignas"),
	_ALIGNOF("_Alignof"),
	_ATOMIC("_Atomic"),
	_BOOL("_Bool"),
	_COMPLEX("_Complex"),
	_GENERIC("_Generic"),
	_IMAGINARY("_Imaginary"),
	_NORETURN("_Noreturn"),
	_STATIC_ASSERT("_Static_assert"),
	_THREAD_LOCAL("_Thread_local");

	TokenKind(final String text, final Precedence lprec, final Precedence rprec) {
		this.text = text;
		this.lprec = lprec;
		this.rprec = rprec;
	}

	TokenKind(final String text, final Precedence lprec) {
		this(text, lprec, Precedence.NONE);
	}

	TokenKind(final String text) {
		this(text, Precedence.NONE, Precedence.NONE);
	}

	/**
	 * Returns the textual representation of this token type.
	 *
	 * @return The textual representation of this token type.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns the left precedence of this token
	 *
	 * @return The left precedence of this token
	 */
	public Precedence getLPrec() {
		return lprec;
	}

	/**
	 * Returns the right precedence of this token
	 *
	 * @return The right precedence of this token
	 */
	public Precedence getRPrec() {
		return rprec;
	}

	/**
	 * Returns the textual representation of this token type.
	 *
	 * @return The textual representation of this token type.
	 */
	@Override
	public String toString() {
		return text;
	}

	private String text;
	private Precedence lprec;
	private Precedence rprec;
}
