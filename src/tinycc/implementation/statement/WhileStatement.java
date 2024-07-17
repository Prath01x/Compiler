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

public class WhileStatement extends Statement {

    private final Expression condition;
    private final Statement body;

    public WhileStatement(final Locatable loc, final Expression condition, final Statement body) {
        this.condition = condition;
        this.body = body;
        this.loc = loc;
    }

    @Override
    public boolean isWhile() {
        return true;
    }

    @Override
    public final String toString() {
        return String.format("While[%s,%s]", condition, body);
    }

    @Override
    public final void computeType(Diagnostic d, Scope s, boolean isBreakable) {
        if (condition.getType(d, s).isScalarType()) {
            body.computeType(d, s, body.isBlock());
        } else {
            d.printError(loc, "Type of condition %s needs to be scalar", condition);
        }
    }

    @Override
    public final void eval(StackFrame stackFrame, MipsAsmGen out, CodeGenerator gen) {
        final TextLabel head = out.makeUniqueTextLabel("_whilehead");
        final TextLabel loop = out.makeUniqueTextLabel("_whileloop");
        final TextLabel end = out.makeUniqueTextLabel("_whileend");
        final TextLabel cachedhead = gen.getLoopHeadLabel();
        final TextLabel cachedend = gen.getLoopEndLabel();

        gen.setCurrentLoopHead(head);
        gen.setCurrentLoopEnd(end);

        out.emitInstruction(JumpInstruction.J, head);
        out.emitLabel(loop);
        body.eval(stackFrame, out, gen);
        out.emitLabel(head);

        GPRegister condition_res = condition.evalToRegister(stackFrame, out, gen);
        out.emitInstruction(BranchInstruction.BNE, condition_res, loop);

        out.emitLabel(end);

        gen.setCurrentLoopEnd(cachedend);
        gen.setCurrentLoopHead(cachedhead);
        gen.free(condition_res);
    }
}
