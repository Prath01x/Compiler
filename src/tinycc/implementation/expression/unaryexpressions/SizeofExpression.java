package tinycc.implementation.expression.unaryexpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryExpression;
import tinycc.implementation.expression.primaryexpressions.StringLiteralExpression;
import tinycc.implementation.semantics.Scope;
import tinycc.implementation.type.IntType;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class SizeofExpression extends UnaryExpression {

    public SizeofExpression(final Token operator, final Expression operand) {
        super(operator, operand);
    }

    @Override
    public final String toString() {
        return String.format("Unary_sizeof[%s]", operand);
    }

    @Override
    public final void computeType(Diagnostic d, Scope s) {
        if (operand.getType(d, s).isCompleteType() && !(operand instanceof StringLiteralExpression)) {
            this.type = new IntType();
        } else if (operand instanceof StringLiteralExpression) {
            this.type = new IntType();
        } else {
            d.printError(operator, "Cannot get sizeof %s", operand);
        }
    }

    @Override
    public final GPRegister evalToRegister(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        int size = 0;
        if (operand instanceof StringLiteralExpression) {
            size = operand.toString().length() - 7;
        } else {
            size = operand.getType().getSize();
        }
        final GPRegister target = gen.getNextUnused();
        out.emitInstruction(ImmediateInstruction.ADDIU, target, GPRegister.ZERO, size);
        return target;
    }
}
