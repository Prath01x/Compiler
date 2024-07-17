package tinycc.implementation.expression.primaryexpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.expression.PrimaryExpression;
import tinycc.implementation.semantics.Scope;
import tinycc.implementation.type.CharType;
import tinycc.implementation.type.PointerType;
import tinycc.mipsasmgen.DataLabel;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MemoryInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class StringLiteralExpression extends PrimaryExpression {

    public StringLiteralExpression(final Token token) {
        super(token);
    }

    @Override
    public final String toString() {
        return String.format("Const_\"%s\"", token.getText());
    }

    @Override
    protected final void computeType(Diagnostic d, Scope s) {
        this.type = new PointerType(new CharType());
    }

    @Override
    public final GPRegister evalToRegister(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        final DataLabel dataLabel = out.makeDataLabel(token.getText());
        out.emitASCIIZ(dataLabel, token.getText());
        final GPRegister target = gen.getNextUnused();
        out.emitInstruction(MemoryInstruction.LA, target, dataLabel, 0, null);
        return target;
    }
}
