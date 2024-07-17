package tinycc.implementation.statement;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.semantics.Scope;
import tinycc.mipsasmgen.BranchInstruction;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.JumpInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.TextLabel;

public class IfStatement extends Statement {

    private final Expression condition;
    private final Statement consequence;
    private final Statement alternative;

    public IfStatement(final Locatable loc, final Expression condition, final Statement consequence,
            final Statement alternative) {
        this.condition = condition;
        this.consequence = consequence;
        this.alternative = alternative;
        this.loc = loc;
    }

    @Override
    public boolean isIf() {
        return true;
    }

    @Override
    public final String toString() {
        return String.format("If[%s,%s", condition, consequence)
                + (alternative == null || alternative.toString() == "" ? "]" : "," + alternative + "]");
    }

    @Override
    public final void computeType(Diagnostic d, Scope s, boolean isBreakable) {
        Scope nested = s.newNestedScope();
        if (condition.getType(d, nested).isScalarType()) {
            consequence.computeType(d, nested, isBreakable);
            if (alternative != null)
                alternative.computeType(d, nested, isBreakable);
        } else {
            d.printError(loc, "Type of condition %s needs to be scalar", condition);
        }
    }

    @Override
    public final void eval(StackFrame stackFrame, MipsAsmGen out, CodeGenerator gen) {
        final TextLabel alternative = out.makeUniqueTextLabel("_ifalt");
        final TextLabel end = out.makeUniqueTextLabel("_ifend");
        final GPRegister condition_res = condition.evalToRegister(stackFrame, out, gen);
        out.emitInstruction(BranchInstruction.BEQ, condition_res, alternative);
        this.consequence.eval(stackFrame, out, gen);
        out.emitInstruction(JumpInstruction.J, end);
        out.emitLabel(alternative);
        if (this.alternative != null)
            this.alternative.eval(stackFrame, out, gen);
        out.emitLabel(end);
        gen.free(condition_res);
    }
}
