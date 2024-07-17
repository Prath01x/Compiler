package tinycc.implementation.expression.binaryexpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.semantics.Scope;
import tinycc.implementation.type.IntType;
import tinycc.implementation.type.PointerType;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.RegisterInstruction;
import tinycc.mipsasmgen.SpecialInstruction;
import tinycc.mipsasmgen.SpecialRegisterInstruction;
import tinycc.parser.Token;

public class MinusExpression extends BinaryExpression {

    private BinaryOperationKind kind;

    public MinusExpression(final Token operator, final Expression left, final Expression right) {
        super(operator, left, right);
    }

    @Override
    public final String toString() {
        return String.format("Binary_-[%s, %s]", left, right);
    }

    public BinaryOperationKind getKind() {
        return this.kind;
    }

    @Override
    public final void computeType(Diagnostic d, Scope s) {
        final Type leftT = left.getType(d, s);
        final Type rightT = right.getType(d, s);
        if (leftT.isIntegerType() && rightT.isIntegerType()) {
            this.type = new IntType();
            this.kind = BinaryOperationKind.INT_INT;
        } else if (leftT.isPointerType() && rightT.isIntegerType()) {
            if (!((PointerType) leftT).getPointsToType().isCompleteType()) {
                d.printError(operator, "Pointer must point to complete (scalar) type");
            }
            this.type = new IntType();
            this.kind = BinaryOperationKind.PTR_INT;
        } else if (leftT.isPointerType() && rightT.isPointerType()) {
            final Type leftP = ((PointerType) left.getType()).getPointsToType();
            final Type rightP = ((PointerType) right.getType()).getPointsToType();
            if (!leftP.isCompleteType() || !leftP.equals(rightP)) {
                d.printError(operator, "Pointer must point to complete (scalar) type and must be identical");
            }
            this.type = new IntType();
            this.kind = BinaryOperationKind.PTR_PTR;
        } else {
            d.printError(token, "Invalid types for subtraction.");
        }
    }

    private final void multiplyPointer(final MipsAsmGen out, final CodeGenerator gen, final GPRegister operand) {
        final GPRegister temp = gen.getNextUnused();
        final int size = ((PointerType) left.getType()).getPointsToType().getSize();
        out.emitInstruction(ImmediateInstruction.ADDI, temp, size);
        out.emitInstruction(SpecialInstruction.MULT, operand, temp);
        out.emitInstruction(SpecialRegisterInstruction.MFLO, operand);
        gen.free(temp);
    }

    @Override
    public final GPRegister evalToRegister(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        final GPRegister leftEval = left.evalToRegister(stackFrame, out, gen);
        final GPRegister rightEval = right.evalToRegister(stackFrame, out, gen);
        if (left.getType().isPointerType() && right.getType().isIntegerType()) {
            multiplyPointer(out, gen, rightEval);
        } else if (left.getType().isIntegerType() && right.getType().isPointerType()) {
            multiplyPointer(out, gen, leftEval);
        }
        out.emitInstruction(RegisterInstruction.SUB, leftEval, rightEval);
        gen.free(rightEval);
        return leftEval;
    }
}
