package tinycc.implementation.expression.primaryexpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.expression.PrimaryExpression;
import tinycc.implementation.semantics.Scope;
import tinycc.implementation.type.CharType;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class CharacterConstantExpression extends PrimaryExpression {

    public CharacterConstantExpression(final Token token) {
        super(token);
    }

    @Override
    public final String toString() {
        return String.format("Const_'%s'", token.getText());
    }

    @Override
    protected final void computeType(Diagnostic d, Scope s) {
        this.type = new CharType();
    }

    @Override
    public final GPRegister evalToRegister(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        final GPRegister target = gen.getNextUnused();
        out.emitInstruction(ImmediateInstruction.ADDIU, target, GPRegister.ZERO,
                (byte) Character.getNumericValue(token.getText().charAt(0)));
        return target;
    }
}
