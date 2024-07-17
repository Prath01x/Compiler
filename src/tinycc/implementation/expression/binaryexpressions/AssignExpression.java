package tinycc.implementation.expression.binaryexpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.semantics.Scope;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class AssignExpression extends BinaryExpression {

    public AssignExpression(final Token operator, final Expression left, final Expression right) {
        super(operator, left, right);
    }

    @Override
    public final String toString() {
        return String.format("Binary_=[%s, %s]", left, right);
    }

    @Override
    protected final void computeType(Diagnostic d, Scope s) {
        final Type leftT = left.getType(d, s);
        final Type rightT = right.getType(d, s);
        if (this.left.isAssignableFrom(d, s, right)) {
            this.type = leftT;
        } else {
            d.printError(this, "Cannot assign %s to %s", leftT, rightT);
        }
    }

    @Override
    public final GPRegister evalToRegister(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        gen.setLEval(true);
        final GPRegister leftEval = left.evalToRegister(stackFrame, out, gen);
        gen.setLEval(false);
        final GPRegister rightEval = right.evalToRegister(stackFrame, out, gen);
        out.emitInstruction(left.getType().getStoreInstruction(), rightEval, null, 0, leftEval);
        gen.free(leftEval);
        return rightEval;
    }
}
