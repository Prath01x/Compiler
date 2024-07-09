package tinycc.logic;

public enum BoolConst implements Formula {
	TRUE, FALSE;

	@Override
	public Type getType() {
		return Type.BOOL;
	}

	@Override
	public Formula subst(Variable toReplace, Formula with) {
		return this;
	}

	@Override
	public String toString() {
		return name();
	}
}
