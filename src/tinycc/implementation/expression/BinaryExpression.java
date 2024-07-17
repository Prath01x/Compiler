package tinycc.implementation.expression;

import tinycc.implementation.expression.binaryexpressions.*;
import tinycc.parser.Token;

public abstract class BinaryExpression extends Expression {

    protected Expression left;
    protected Expression right;
    protected Token operator;

    protected int registerCount;

    protected BinaryExpression(final Token _token, final Expression _left, final Expression _right) {
        super(_token);
        operator = token;
        left = _left;
        right = _right;
        registerCount = 0;
    }

    public final static BinaryExpression createBinaryExpression(final Token operator,
            final Expression left,
            final Expression right) {

        switch (operator.getKind()) {
            case PLUS:
                return new AddExpression(operator, left, right);
            case MINUS:
                return new MinusExpression(operator, left, right);
            case ASTERISK:
                return new MultiplyExpression(operator, left, right);
            case SLASH:
                return new DivideExpression(operator, left, right);
            case EQUAL:
                return new AssignExpression(operator, left, right);
            default:
                return ComparisonExpression.createComparisonExpression(operator, left, right);
        }
    }
}
