package tinycc.implementation.AST;

import java.util.ArrayList;
import java.util.List;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.declarations.ExternalDeclaration;
import tinycc.implementation.declarations.FunctionDefinition;
import tinycc.implementation.expression.*;
import tinycc.implementation.statement.*;
import tinycc.implementation.type.*;
import tinycc.parser.ASTFactory;
import tinycc.parser.Token;
import tinycc.parser.TokenKind;

public class ASTBuilder implements ASTFactory {

    private final List<ExternalDeclaration> exts = new ArrayList<ExternalDeclaration>();
    private final List<FunctionDefinition> funcs = new ArrayList<FunctionDefinition>();

    @Override
    public final Statement createBlockStatement(Locatable loc, List<Statement> statements) {
        return new BlockStatement(loc, statements);
    }

    @Override
    public final Statement createBreakStatement(Locatable loc) {
        return new BreakStatement(loc);
    }

    @Override
    public final Statement createContinueStatement(Locatable loc) {
        return new ContinueStatement(loc);
    }

    @Override
    public final Statement createDeclarationStatement(Type type, Token name, Expression init) {
        return new DeclarationStatement(type, name, init);
    }

    @Override
    public final Statement createExpressionStatement(Locatable loc, Expression exp) {
        return new ExpressionStatement(loc, exp);
    }

    @Override
    public final Statement createIfStatement(Locatable loc, Expression cond, Statement consequence,
            Statement alternative) {
        return new IfStatement(loc, cond, consequence, alternative);
    }

    @Override
    public final Statement createReturnStatement(Locatable loc, Expression exp) {
        return new ReturnStatement(loc, exp);
    }

    @Override
    public final Statement createWhileStatement(Locatable loc, Expression condition, Statement body) {
        return new WhileStatement(loc, condition, body);
    }

    @Override
    public final Type createFunctionType(Type returnType, List<Type> parameters) {
        return new FunctionType(returnType, parameters);
    }

    @Override
    public final Type createPointerType(Type pointsTo) {
        return new PointerType(pointsTo);
    }

    @Override
    public final Type createBaseType(TokenKind kind) {
        return BaseType.createBaseType(kind);
    }

    @Override
    public final Expression createBinaryExpression(Token operator, Expression left, Expression right) {
        return BinaryExpression.createBinaryExpression(operator, left, right);
    }

    @Override
    public final Expression createCallExpression(Token token, Expression callee, List<Expression> arguments) {
        return new CallExpression(token, callee, arguments);
    }

    @Override
    public final Expression createConditionalExpression(Token token, Expression condition, Expression consequence,
            Expression alternative) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public final Expression createUnaryExpression(Token operator, boolean postfix, Expression operand) {
        return UnaryExpression.createUnaryExpression(operator, operand);
    }

    @Override
    public final Expression createPrimaryExpression(Token token) {
        return PrimaryExpression.createPrimaryExpression(token);
    }

    @Override
    public final void createExternalDeclaration(Type type, Token name) {
        ExternalDeclaration ed = ExternalDeclaration.createExternalDeclaration(type, name);
        if (!this.exts.contains(ed)) {
            exts.add(ed);
        }
    }

    @Override
    public final void createFunctionDefinition(Type type, Token name, List<Token> parameterNames, Statement body) {
        FunctionDefinition fd = new FunctionDefinition(type, name, parameterNames, body);
        if (!this.exts.contains(fd)) {
            exts.add(fd);
            funcs.add(fd);
        }
    }

    public final List<ExternalDeclaration> getExternalDeclarations() {
        return this.exts;
    }

    public final List<FunctionDefinition> getFunctionDefinitions() {
        return this.funcs;
    }
}