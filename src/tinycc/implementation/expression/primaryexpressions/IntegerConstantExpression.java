package tinycc.implementation.expression.primaryexpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.expression.PrimaryExpression;
import tinycc.implementation.semantics.Scope;
import tinycc.implementation.type.IntType;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public final class IntegerConstantExpression extends PrimaryExpression {

    public IntegerConstantExpression(final Token token) {
        super(token);
    }

    @Override
    public final String toString() {
        return "Const_" + token.getText();
    }

    @Override
    public boolean isZero() {
        return token.getText().equals("0");
    }

    @Override
    protected final void computeType(Diagnostic d, Scope s) {
        this.type = new IntType();
    }

    @Override
    public final GPRegister evalToRegister(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        final GPRegister target = gen.getNextUnused();
        out.emitInstruction(ImmediateInstruction.ADDIU, target, GPRegister.ZERO, Integer.parseInt(token.getText()));
        return target;
    }
}
