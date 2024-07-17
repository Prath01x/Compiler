package tinycc.implementation.expression.primaryexpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.codegeneration.StackFrame.NoOffsetForIdException;
import tinycc.implementation.declarations.Declaration;
import tinycc.implementation.expression.PrimaryExpression;
import tinycc.implementation.semantics.IdUndeclared;
import tinycc.implementation.semantics.Scope;
import tinycc.mipsasmgen.DataLabel;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.MemoryInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.RegisterInstruction;
import tinycc.parser.Token;

public class IdentifierExpression extends PrimaryExpression {

    private String id;
    private Declaration decl;

    public IdentifierExpression(final Token token) {
        super(token);
        this.id = token.getText();
    }

    @Override
    public final String toString() {
        return String.format("Var_%s", token.getText());
    }

    public final String getId() {
        return this.id;
    }

    public final Declaration getDeclaration() {
        return this.decl;
    }

    @Override
    public final boolean isIdentifier() {
        return true;
    }

    @Override
    public final boolean isLValue(Diagnostic d, Scope s) {
        try {
            Declaration res = s.lookupId(id);
            return res.getType().isCompleteType();
        } catch (IdUndeclared e) {
            d.printError(token, "Cannot assign to undeclared identifier %s", id);
            return false;
        }
    }

    @Override
    protected final void computeType(Diagnostic d, Scope s) {
        try {
            this.decl = s.lookupId(id);
            this.type = this.decl.getType();
        } catch (IdUndeclared e) {
            d.printError(token, "Variable %s not declared", id);
        }
    }

    // Get value of var
    public final GPRegister evalAsR(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        final GPRegister target = gen.getNextUnused();
        final GPRegister lEval = this.evalAsL(stackFrame, out, gen);
        MemoryInstruction inst = getType().getLoadInstruction();
        out.emitInstruction(inst, target, null, 0, lEval);
        gen.free(lEval);
        return target;
    }

    // Put address of var into register
    public final GPRegister evalAsL(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        final GPRegister target = gen.getNextUnused();
        try {
            final int offset = stackFrame.getOffset(id);
            out.emitInstruction(RegisterInstruction.ADD, target, GPRegister.SP);
            if (offset != 0)
                out.emitInstruction(ImmediateInstruction.ADDI, target, offset);
            return target;
        } catch (NoOffsetForIdException e) {
            final DataLabel global = gen.getGlobalLabel(id);
            out.emitInstruction(MemoryInstruction.LA, target, global, 0, null);
            return target;
        }
    }

    @Override
    public final GPRegister evalToRegister(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        if (gen.isLEval()) {
            return evalAsL(stackFrame, out, gen);
        } else {
            return evalAsR(stackFrame, out, gen);
        }
    }
}
