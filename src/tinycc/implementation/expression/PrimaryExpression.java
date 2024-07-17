package tinycc.implementation.expression;

import tinycc.parser.Token;
import tinycc.implementation.expression.primaryexpressions.*;

public abstract class PrimaryExpression extends Expression {

    protected PrimaryExpression(final Token _token) {
        super(_token);
    }

    public static final PrimaryExpression createPrimaryExpression(final Token token) {
        switch (token.getKind()) {
            case NUMBER:
                return new IntegerConstantExpression(token);
            case CHARACTER:
                return new CharacterConstantExpression(token);
            case STRING:
                return new StringLiteralExpression(token);
            case IDENTIFIER:
                return new IdentifierExpression(token);
            default:
                return null;
        }
    }
}
