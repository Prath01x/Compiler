package tinycc.implementation.declarations;

import tinycc.implementation.type.*;
import tinycc.parser.Token;

public abstract class ExternalDeclaration implements Declaration {

    protected Type type;
    protected Token token;

    public static ExternalDeclaration createExternalDeclaration(final Type type, final Token token) {
        if (type.isFunctionType()) {
            return new FunctionDeclaration(type, token);
        } else
            return new GlobalVariableDeclaration(type, token);
    }

    public Type getType() {
        return this.type;
    }

    public Token getToken() {
        return this.token;
    }

    public String getName() {
        return this.token.getText();
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof ExternalDeclaration) {
            ExternalDeclaration _other = (ExternalDeclaration) other;
            return type == _other.getType() && token == _other.getToken();
        }
        return false;
    }

}
