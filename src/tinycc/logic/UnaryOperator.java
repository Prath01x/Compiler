package tinycc.logic;

public enum UnaryOperator {
	NOT("¬");

	private final String stringRepr;

	private UnaryOperator(String stringRepr) {
		this.stringRepr = stringRepr;
	}

	@Override
	public String toString() {
		return this.stringRepr;
	}
}
