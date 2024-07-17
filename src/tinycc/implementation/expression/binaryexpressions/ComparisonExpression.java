package tinycc.implementation.expression.binaryexpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.semantics.Scope;
import tinycc.implementation.type.IntType;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public abstract class ComparisonExpression extends BinaryExpression {

    protected boolean strictEquals;
    protected BinaryOperationKind kind;

    public ComparisonExpression(final Token token, final Expression left, final Expression right) {
        super(token, left, right);
    }

    public static final ComparisonExpression createComparisonExpression(final Token operator, final Expression left,
            final Expression right) {
        switch (operator.getKind()) {
            case EQUAL_EQUAL:
                return new EqualsExpression(operator, left, right);
            case BANG_EQUAL:
                return new NotEqualsExpression(operator, left, right);
            case LESS_EQUAL:
                return new LessEqualThanExpression(operator, left, right);
            case GREATER_EQUAL:
                return new GreaterEqualThanExpression(operator, left, right);
            case LESS:
                return new LessThanExpression(operator, left, right);
            case GREATER:
                return new GreaterThanExpression(operator, left, right);
            default:
                return null;
        }
    }

    public BinaryOperationKind getKind() {
        return this.kind;
    }

    @Override
    public final void computeType(Diagnostic d, Scope s) {
        final Type leftT = left.getType(d, s);
        final Type rightT = right.getType(d, s);
        if (leftT.isIntegerType() && rightT.isIntegerType()) {
            this.kind = BinaryOperationKind.INT_INT;
        } else if (leftT.isPointerType() || rightT.isPointerType()) {
            boolean equal = leftT.equals(rightT);
            if (strictEquals && equal) {
                this.kind = BinaryOperationKind.PTR_PTR;
            } else {
                d.printError(operator, "Comparison oerands must be of identical type.");
                return;
            }
            if (equal || leftT.isVoidPointer() || rightT.isVoidPointer()
                    || right.isZero() || left.isZero()) {
                this.kind = BinaryOperationKind.PTR_PTR;
            } else {
                d.printError(operator, "Comparison not allowed between (%s, %s)", leftT, rightT);
            }
        } else {
            d.printError(operator, "Cannot compare %s to %s", leftT, rightT);
        }
        this.type = new IntType();
    }
}
