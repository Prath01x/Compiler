package tinycc.implementation.statement;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.semantics.Scope;
import tinycc.mipsasmgen.JumpInstruction;
import tinycc.mipsasmgen.MipsAsmGen;

public class BreakStatement extends Statement {

    public BreakStatement(final Locatable loc) {
        this.loc = loc;
    }

    @Override
    public boolean isBreak() {
        return true;
    }

    @Override
    public final String toString() {
        return "Break";
    }

    @Override
    public final void computeType(Diagnostic d, Scope s, boolean isBreakable) {
        if (!isBreakable) {
            d.printError(loc, "Cannot break here");
        }
    }

    @Override
    public final void eval(StackFrame stackFrame, MipsAsmGen out, CodeGenerator gen) {
        out.emitInstruction(JumpInstruction.J, gen.getLoopEndLabel());
    }
}
