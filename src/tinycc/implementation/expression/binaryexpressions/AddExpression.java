package tinycc.implementation.expression.binaryexpressions;

import tinycc.parser.Token;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.semantics.Scope;
import tinycc.implementation.type.PointerType;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.RegisterInstruction;
import tinycc.mipsasmgen.SpecialInstruction;
import tinycc.mipsasmgen.SpecialRegisterInstruction;

public final class AddExpression extends BinaryExpression {

    private BinaryOperationKind kind;

    public AddExpression(final Token operator, final Expression left, final Expression right) {
        super(operator, left, right);
    }

    @Override
    public final String toString() {
        return String.format("Binary_+[%s, %s]", left, right);
    }

    public BinaryOperationKind getKind() {
        return this.kind;
    }

    @Override
    protected final void computeType(Diagnostic d, Scope s) {
        final Type leftT = left.getType(d, s);
        final Type rightT = right.getType(d, s);
        if (leftT.isIntegerType() && rightT.isIntegerType()) {
            this.type = leftT;
            this.kind = BinaryOperationKind.INT_INT;
        } else if (leftT.isPointerType() && rightT.isIntegerType()) {
            if (!((PointerType) leftT).getPointsToType().isCompleteType()) {
                d.printError(operator, "Pointer must point to complete (scalar) type");
            }
            this.type = leftT;
            this.kind = BinaryOperationKind.PTR_INT;
        } else if (leftT.isIntegerType() && rightT.isPointerType()) {
            if (!((PointerType) rightT).getPointsToType().isCompleteType()) {
                d.printError(operator, "Pointer must point to complete (scalar) type");
            }
            this.type = rightT;
            this.kind = BinaryOperationKind.INT_PTR;
        } else {
            d.printError(operator, "Invalid types to addition operation");
        }
    }

    public final GPRegister evalToRegister(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        final GPRegister leftEval = left.evalToRegister(stackFrame, out, gen);
        final GPRegister rightEval = right.evalToRegister(stackFrame, out, gen);
        if (left.getType().isPointerType() && right.getType().isIntegerType()) {
            final GPRegister temp = gen.getNextUnused();
            final int size = ((PointerType) left.getType()).getPointsToType().getSize();
            out.emitInstruction(ImmediateInstruction.ADDI, temp, size);
            out.emitInstruction(SpecialInstruction.MULT, rightEval, temp);
            out.emitInstruction(SpecialRegisterInstruction.MFLO, rightEval);
            gen.free(temp);
        } else if (right.getType().isPointerType() && left.getType().isIntegerType()) {
            final GPRegister temp = gen.getNextUnused();
            final int size = ((PointerType) left.getType()).getPointsToType().getSize();
            out.emitInstruction(ImmediateInstruction.ADDI, temp, size);
            out.emitInstruction(SpecialInstruction.MULT, leftEval, temp);
            out.emitInstruction(SpecialRegisterInstruction.MFLO, rightEval);
            gen.free(temp);
        }
        out.emitInstruction(RegisterInstruction.ADD, leftEval, rightEval);
        gen.free(rightEval);
        return leftEval;
    }
}
