package tinycc.implementation.declarations;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.semantics.Scope;
import tinycc.implementation.type.Type;

public interface Declaration {

    public Type getType();

    public String getName();

    public abstract void check(Diagnostic d, Scope s);
}