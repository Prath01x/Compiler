package tinycc.implementation.expression.binaryexpressions;

import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.expression.Expression;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.RegisterInstruction;
import tinycc.parser.Token;

public class NotEqualsExpression extends ComparisonExpression {

    public NotEqualsExpression(final Token operator, final Expression left, final Expression right) {
        super(operator, left, right);
        this.strictEquals = false;
    }

    @Override
    public final String toString() {
        return String.format("Binary_!=[%s, %s]", left, right);
    }

    @Override
    public final GPRegister evalToRegister(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        final GPRegister leftEval = left.evalToRegister(stackFrame, out, gen);
        final GPRegister rightEval = right.evalToRegister(stackFrame, out, gen);
        final GPRegister target1 = gen.getNextUnused();
        final GPRegister target2 = gen.getNextUnused();
        out.emitInstruction(RegisterInstruction.SLT, target1, leftEval, rightEval);
        out.emitInstruction(RegisterInstruction.SLT, target2, rightEval, leftEval);
        out.emitInstruction(RegisterInstruction.ADD, target1, target2);
        gen.free(leftEval, rightEval, target2);
        return target1;
    }
}