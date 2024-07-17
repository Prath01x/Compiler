package tinycc.implementation.statement;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.semantics.Scope;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.RegisterInstruction;

public class ReturnStatement extends Statement {

    private final Expression exp;

    public ReturnStatement(final Locatable loc, final Expression exp) {
        this.exp = exp;
        this.loc = loc;
    }

    @Override
    public boolean isReturn() {
        return true;
    }

    @Override
    public String toString() {
        return String.format("Return[%s]", exp == null ? "" : exp);
    }

    public final Expression getExpression() {
        return this.exp;
    }

    @Override
    public final void computeType(Diagnostic d, Scope s, boolean isBreakable) {
        exp.getType(d, s);
    }

    @Override
    public final void eval(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        if (this.exp != null) {
            final GPRegister toReturn = this.exp.evalToRegister(stackFrame, out, gen);
            out.emitInstruction(RegisterInstruction.XOR, GPRegister.V0, GPRegister.V0);
            out.emitInstruction(RegisterInstruction.ADD, GPRegister.V0, toReturn);
            gen.free(toReturn);
        }
        stackFrame.jumpToEnd(out);
    }
}
