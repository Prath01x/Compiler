package tinycc.implementation.expression.binaryexpressions;

import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.expression.Expression;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.RegisterInstruction;
import tinycc.parser.Token;

public class GreaterEqualThanExpression extends ComparisonExpression {

    public GreaterEqualThanExpression(final Token operator, final Expression left, final Expression right) {
        super(operator, left, right);
        this.strictEquals = true;
    }

    @Override
    public final String toString() {
        return String.format("Binary_>=[%s, %s]", left, right);
    }

    public BinaryOperationKind getKind() {
        return kind;
    }

    @Override
    public final GPRegister evalToRegister(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        final GPRegister leftEval = left.evalToRegister(stackFrame, out, gen);
        final GPRegister rightEval = right.evalToRegister(stackFrame, out, gen);
        final GPRegister target = gen.getNextUnused();
        out.emitInstruction(RegisterInstruction.SLT, leftEval, rightEval);
        out.emitInstruction(ImmediateInstruction.ADDI, target, 1);
        out.emitInstruction(RegisterInstruction.SUB, target, leftEval);
        gen.free(leftEval);
        gen.free(rightEval);
        return target;
    }
}
