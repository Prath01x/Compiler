package tinycc.implementation.expression.unaryexpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryExpression;
import tinycc.implementation.type.PointerType;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;
import tinycc.implementation.semantics.Scope;

public class IndirectionExpression extends UnaryExpression {

    public IndirectionExpression(final Token operator, final Expression operand) {
        super(operator, operand);
    }

    @Override
    public final String toString() {
        return String.format("Unary_*[%s]", operand);
    }

    @Override
    public final boolean isLValue(Diagnostic d, Scope s) {
        return operand.getType(d, s).isPointerType()
                && ((PointerType) operand.getType(d, s)).getPointsToType().isCompleteType();
    }

    @Override
    public final void computeType(Diagnostic d, Scope s) {
        final Type operandType = operand.getType(d, s);
        if (operandType.isPointerType()) {
            final Type pointsTo = ((PointerType) operand.getType(d, s)).getPointsToType();
            if (!pointsTo.isCompleteType()) {
                d.printError(this, "Cannot dereference void pointer");
            }
            this.type = pointsTo;
        } else {
            d.printError(this, "Invalid expression for indirection operator %s", operand);
        }
    }

    @Override
    public final GPRegister evalToRegister(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        if (!gen.isLEval()) {
            final GPRegister target = operand.evalToRegister(stackFrame, out, gen);
            out.emitInstruction(operand.getType().getLoadInstruction(), target, null, 0, target);
            return target;
        } else {
            gen.setLEval(false);
            return operand.evalToRegister(stackFrame, out, gen);
        }
    }
}
