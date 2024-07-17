package tinycc.implementation.type;

import tinycc.parser.TokenKind;

public abstract class BaseType extends Type {

    public static BaseType createBaseType(final TokenKind kind) {
        if (kind == TokenKind.INT)
            return new IntType();
        if (kind == TokenKind.CHAR)
            return new CharType();
        if (kind == TokenKind.VOID)
            return new VoidType();
        else
            throw new IllegalArgumentException("Cannot create base type here");
    }
}
