package tinycc.implementation.expression;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.semantics.Scope;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

/**
 * The main expression class (see project description)
 *
 * You can change this class but the given name of the class must not be
 * modified.
 */
public abstract class Expression implements Locatable {

	protected Type type = null;
	protected Token token;

	protected Expression(final Token _token) {
		token = _token;
	}

	/**
	 * Creates a string representation of this expression.
	 *
	 * @remarks See project documentation.
	 * @see StringBuilder
	 */
	@Override
	public abstract String toString();

	public Type getType(Diagnostic d, Scope s) {
		if (type == null)
			computeType(d, s);
		return type;
	}

	public Type getType() {
		return type;
	}

	public boolean isIdentifier() {
		return false;
	}

	public boolean isZero() {
		return false;
	}

	public boolean isAssignableFrom(Diagnostic d, Scope s, Expression right) {
		final Type leftT = this.getType(d, s);
		final Type rightT = right.getType(d, s);
		if (!isLValue(d, s))
			return false;
		if (leftT.isPointerType() && right.isZero())
			return true;
		if (leftT.equals(rightT))
			return true;
		if (leftT.isIntegerType() && rightT.isIntegerType())
			return true;
		if (leftT.isVoidPointer() || rightT.isVoidPointer())
			return true;
		if ((leftT.isPointerType() && right.isZero()))
			return true;
		return false;
	}

	public boolean isLValue(Diagnostic d, Scope s) {
		return false;
	}

	protected abstract void computeType(Diagnostic d, Scope s);

	/**
	 * Evaluates an expression returns the register in which the
	 * result is stored.
	 */
	public abstract GPRegister evalToRegister(final StackFrame stackFrame, final MipsAsmGen out,
			final CodeGenerator gen);

	public final String getInputName() {
		return token.getInputName();
	}

	public final int getLine() {
		return token.getLine();
	}

	public final int getColumn() {
		return token.getColumn();
	}
}
