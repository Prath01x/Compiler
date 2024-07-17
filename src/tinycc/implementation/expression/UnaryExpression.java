package tinycc.implementation.expression;

import tinycc.implementation.expression.unaryexpressions.*;
import tinycc.parser.Token;

public abstract class UnaryExpression extends Expression {

    protected Expression operand;
    protected Token operator;

    protected UnaryExpression(final Token _token, final Expression _operand) {
        super(_token);
        operator = token;
        operand = _operand;
    }

    public static final UnaryExpression createUnaryExpression(final Token operator, final Expression operand) {
        switch (operator.getKind()) {
            case ASTERISK:
                return new IndirectionExpression(operator, operand);
            case AND:
                return new AddressOfExpression(operator, operand);
            case SIZEOF:
                return new SizeofExpression(operator, operand);
            default:
                return null;
        }
    }
}
