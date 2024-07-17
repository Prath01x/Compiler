package tinycc.implementation.expression.binaryexpressions;

import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.expression.Expression;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.RegisterInstruction;
import tinycc.parser.Token;

public class LessThanExpression extends ComparisonExpression {

    public LessThanExpression(final Token operator, final Expression left, final Expression right) {
        super(operator, left, right);
        this.strictEquals = true;
    }

    @Override
    public final String toString() {
        return String.format("Binary_<[%s, %s]", left, right);
    }

    @Override
    public final GPRegister evalToRegister(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        final GPRegister leftEval = left.evalToRegister(stackFrame, out, gen);
        final GPRegister rightEval = right.evalToRegister(stackFrame, out, gen);
        out.emitInstruction(RegisterInstruction.SLT, leftEval, rightEval);
        gen.free(rightEval);
        return leftEval;
    }
}
