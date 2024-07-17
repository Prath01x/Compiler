package tinycc.implementation.statement;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.semantics.Scope;
import tinycc.mipsasmgen.MipsAsmGen;

public class ExpressionStatement extends Statement {

    private final Expression exp;

    public ExpressionStatement(final Locatable loc, final Expression exp) {
        this.exp = exp;
        this.loc = loc;
    }

    @Override
    public boolean isExpression() {
        return true;
    }

    @Override
    public final String toString() {
        return exp.toString();
    }

    @Override
    public final void computeType(Diagnostic d, Scope s, boolean isBreakable) {
        exp.getType(d, s);
    }

    @Override
    public final void eval(final StackFrame stackFrame, MipsAsmGen out, CodeGenerator gen) {
        gen.free(exp.evalToRegister(stackFrame, out, gen));
    }
}
