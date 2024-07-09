package tinycc.logic;

public interface Formula {

	public Type getType();

	public Formula subst(Variable toReplace, Formula with);
}
