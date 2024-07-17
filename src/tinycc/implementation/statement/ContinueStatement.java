package tinycc.implementation.statement;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.semantics.Scope;
import tinycc.mipsasmgen.JumpInstruction;
import tinycc.mipsasmgen.MipsAsmGen;

public class ContinueStatement extends Statement {

    public ContinueStatement(final Locatable loc) {
        this.loc = loc;
    }

    @Override
    public boolean isContinue() {
        return true;
    }

    @Override
    public final String toString() {
        return "Continue";
    }

    @Override
    public final void computeType(Diagnostic d, Scope s, boolean isBreakable) {
        if (!isBreakable) {
            d.printError(loc, "Cannot continue here");
        }
    }

    @Override
    public final void eval(StackFrame stackFrame, MipsAsmGen out, CodeGenerator gen) {
        out.emitInstruction(JumpInstruction.J, gen.getLoopHeadLabel());
    }
}