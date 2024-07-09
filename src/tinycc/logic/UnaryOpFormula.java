package tinycc.logic;

public class UnaryOpFormula implements Formula {

	private UnaryOperator op;
	private Formula e;

	public UnaryOpFormula(UnaryOperator op, Formula e) {
		this.op = op;
		this.e = e;
	}

	public Formula getOperand() {
		return e;
	}

	@Override
	public Type getType() {
		switch (op) {
		case NOT:
			return Type.BOOL;
		default:
			throw new UnsupportedOperationException("Unsupported unary operator.");
		}
	}

	@Override
	public Formula subst(Variable toReplace, Formula with) {
		Formula newE = e.subst(toReplace, with);
		return new UnaryOpFormula(this.op, newE);
	}

	@Override
	public String toString() {
		return "(" + op.toString() + e.toString() + ")";
	}
}
