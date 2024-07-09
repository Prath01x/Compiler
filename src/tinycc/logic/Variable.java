package tinycc.logic;

public class Variable implements Formula {

	private final String name;
	private final Type type;

	public Variable(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Formula subst(Variable toReplace, Formula with) {
		return this.equals(toReplace) ? with : this;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Variable) {
			Variable x = (Variable) obj;
			return this.name.equals(x.name);
		}
		return false;
	}
}
