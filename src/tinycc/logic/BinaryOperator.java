package tinycc.logic;

public enum BinaryOperator {
	ADD("+"),
	SUB("-"),
	MUL("*"),
	LT("<"),
	LE("≤"),
	GT(">"),
	GE("≥"),
	EQ("=="),
	NE("≠"),
	AND("⋀"),
	OR("⋁"),
	IMPLIES("⇒");

	private final String stringRepr;

	private BinaryOperator(String stringRepr) {
		this.stringRepr = stringRepr;
	}

	@Override
	public String toString() {
		return this.stringRepr;
	}
}
