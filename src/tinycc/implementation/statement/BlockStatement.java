package tinycc.implementation.statement;

import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.semantics.Scope;
import tinycc.mipsasmgen.MipsAsmGen;

public class BlockStatement extends Statement {
    private final List<Statement> statements;
    private boolean isBreakable;
    private byte nLocals;

    public BlockStatement(Locatable loc, List<Statement> statements) {
        this.statements = statements;
        this.loc = loc;
        this.nLocals = 0;
    }

    @Override
    public boolean isBlock() {
        return true;
    }

    public final void setBreakable(final boolean val) {
        this.isBreakable = val;
    }

    public final boolean isBreakable() {
        return isBreakable;
    }

    public final List<Statement> getStatements() {
        return this.statements;
    }

    @Override
    public final String toString() {
        String res = "Block[";
        int i = 0;
        for (; i < statements.size() - 1; i++)
            res += "\t" + statements.get(i) + "\n,";
        return res + (statements.size() == 0 ? "" : statements.get(i)) + "]";
    }

    @Override
    public final void computeType(Diagnostic d, Scope s, boolean isBreakable) {
        Scope nested = s.newNestedScope();
        for (final Statement st : statements) {
            if (st.isDeclaration()) {
                nLocals++;
            }
            if (st.isBreak() || st.isContinue()) {
                if (!isBreakable) {
                    d.printError(st, "Cannot break / continue here");
                }
            }
            st.computeType(d, nested, isBreakable);
        }
    }

    public final int getNLocals() {
        return this.nLocals;
    }

    @Override
    public final void eval(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        final StackFrame newFrame = new StackFrame(stackFrame);
        for (final Statement st : statements) {
            st.eval(newFrame, out, gen);
        }
    }
}
