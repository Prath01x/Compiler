package tinycc.implementation.expression.unaryexpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryExpression;
import tinycc.implementation.semantics.Scope;
import tinycc.implementation.type.PointerType;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class AddressOfExpression extends UnaryExpression {

    public AddressOfExpression(final Token operator, final Expression operand) {
        super(operator, operand);
    }

    @Override
    public final String toString() {
        return String.format("Unary_&[%s]", operand);
    }

    @Override
    public final void computeType(Diagnostic d, Scope s) {
        if (operand.getType(d, s).isCompleteType() && operand.isLValue(d, s)) {
            this.type = new PointerType(operand.getType(d, s));
        } else {
            d.printError(operator, "Operand %s has to be L-value", operand);
        }
    }

    @Override
    public final GPRegister evalToRegister(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        gen.setLEval(true);
        final GPRegister target = operand.evalToRegister(stackFrame, out, gen);
        gen.setLEval(false);
        return target;
    }
}
