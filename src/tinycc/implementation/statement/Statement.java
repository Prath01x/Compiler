package tinycc.implementation.statement;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.semantics.Scope;
import tinycc.mipsasmgen.MipsAsmGen;

/**
 * The main statement class (see project description)
 *
 * You can change this class but the given name of the class must not be
 * modified.
 */
public abstract class Statement implements Locatable {

	protected Locatable loc;

	/**
	 * Creates a string representation of this statement.
	 *
	 * @remarks See project documentation.
	 * @see StringBuilder
	 */
	@Override
	public abstract String toString();

	public String getInputName() {
		return loc.getInputName();
	}

	public int getLine() {
		return loc.getLine();
	}

	public int getColumn() {
		return loc.getColumn();
	}

	public abstract void computeType(Diagnostic d, Scope s, boolean isBreakable);

	public abstract void eval(StackFrame stackFrame, MipsAsmGen out, CodeGenerator gen);

	public boolean isBlock() {
		return false;
	}

	public boolean isBreak() {
		return false;
	}

	public boolean isContinue() {
		return false;
	}

	public boolean isDeclaration() {
		return false;
	}

	public boolean isExpression() {
		return false;
	}

	public boolean isIf() {
		return false;
	}

	public boolean isReturn() {
		return false;
	}

	public boolean isWhile() {
		return false;
	}
}
