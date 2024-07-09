package tinycc.logic;

public class BinaryOpFormula implements Formula {

	private BinaryOperator op;
	private Formula left;
	private Formula right;

	public BinaryOpFormula(BinaryOperator op, Formula left, Formula right) {
		this.op = op;
		this.left = left;
		this.right = right;
	}

	public Formula getLeft() {
		return left;
	}

	public Formula getRight() {
		return right;
	}

	@Override
	public Type getType() {
		switch (op) {
		case ADD:
		case SUB:
		case MUL:
			return Type.INT;
		case LT:
		case LE:
		case GT:
		case GE:
		case EQ:
		case NE:
		case AND:
		case OR:
		case IMPLIES:
			return Type.BOOL;
		default:
			throw new UnsupportedOperationException("Unsupported binary operator.");
		}
	}

	public BinaryOperator getOperator() {
		return op;
	}

	@Override
	public Formula subst(Variable toReplace, Formula with) {
		Formula newLeft = this.left.subst(toReplace, with);
		Formula newRight = this.right.subst(toReplace, with);
		return new BinaryOpFormula(this.op, newLeft, newRight);
	}

	@Override
	public String toString() {
		return "(" + left.toString() + " " + op.toString() + " " + right.toString() + ")";
	}
}
