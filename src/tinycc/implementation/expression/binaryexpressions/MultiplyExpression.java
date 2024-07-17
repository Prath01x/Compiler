package tinycc.implementation.expression.binaryexpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.semantics.Scope;
import tinycc.implementation.type.IntType;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.RegisterInstruction;
import tinycc.parser.Token;

public class MultiplyExpression extends BinaryExpression {

    public MultiplyExpression(final Token operator, final Expression left, final Expression right) {
        super(operator, left, right);
    }

    @Override
    public final String toString() {
        return String.format("Binary_*[%s, %s]", left, right);
    }

    @Override
    protected final void computeType(Diagnostic d, Scope s) {
        if (left.getType(d, s).isIntegerType() && right.getType(d, s).isIntegerType()) {
            this.type = new IntType();
        } else {
            d.printError(operator, "Multiplication expects two integers but got %s, %s", left.getType(d, s),
                    right.getType(d, s));
        }
    }

    @Override
    public final GPRegister evalToRegister(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        final GPRegister leftEval = left.evalToRegister(stackFrame, out, gen);
        final GPRegister rightEval = right.evalToRegister(stackFrame, out, gen);
        out.emitInstruction(RegisterInstruction.MUL, leftEval, rightEval);
        gen.free(rightEval);
        return leftEval;
    }
}
