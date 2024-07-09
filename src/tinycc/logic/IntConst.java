package tinycc.logic;

public class IntConst implements Formula {

	private final int value;

	public IntConst(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public Type getType() {
		return Type.INT;
	}

	@Override
	public int hashCode() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntConst) {
			IntConst c = (IntConst) obj;
			return this.value == c.value;
		}
		return false;
	}

	@Override
	public Formula subst(Variable toReplace, Formula with) {
		return this;
	}

	@Override
	public String toString() {
		return "" + value;
	}
}
