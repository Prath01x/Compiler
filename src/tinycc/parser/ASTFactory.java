package tinycc.parser;

import java.util.List;

import tinycc.diagnostic.Locatable;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.statement.Statement;
import tinycc.implementation.type.Type;

/**
 * The abstract-syntax-tree factory to create AST nodes.
 */
public interface ASTFactory {

	// ------------ Statements ------------

	/**
	 * Creates a block statement with the given location. This block contains the
	 * given list of statements.
	 *
	 * Example:
	 * {
	 *     statement*
	 * }
	 *
	 * @param loc        The location of this block.
	 * @param statements The list of statements which belong to this block.
	 * @return A statement which represents this block.
	 */
	public Statement createBlockStatement(Locatable loc, List<Statement> statements);

	/**
	 * BONUS Creates a break statement with the given location.
	 *
	 * @param loc The given location of this statement.
	 *
	 * Example:
	 *     break;
	 *
	 * @return A statement which represents this break statement.
	 */
	public default Statement createBreakStatement(Locatable loc) {
		throw new UnsupportedOperationException("Break statement not implemented!");
	}

	/**
	 * BONUS Creates a continue statement with the given location.
	 *
	 * @param loc The given location of this statement.
	 *
	 * Example:
	 *     continue;
	 *
	 * @return A statement which represents this continue statement.
	 */
	public default Statement createContinueStatement(Locatable loc) {
		throw new UnsupportedOperationException("Continue statement not implemented!");
	}

	/**
	 * Creates a declaration statement. Use the location of {@code name} for the
	 * location of this statement.
	 *
	 * @param type The type of the declared variable.
	 * @param name The name of the declared variable.
	 * @param init The initialization expression (if available, null otherwise).
	 *
	 * Example:
	 *     int i;
	 *     int j = 42;
	 *
	 * @return A statement which represents this declaration.
	 */
	public Statement createDeclarationStatement(Type type, Token name, Expression init);

	/**
	 * Creates an expression statement with the given location.
	 * Example:
	 *     a = 5;
	 * @param loc The given location of this statement.
	 * @param expression The nested expression of this expression statement.
	 * @return A statement which represents this expression statement.
	 */
	public Statement createExpressionStatement(Locatable loc, Expression expression);

	/**
	 * Creates an if statement with the given location.
	 *
	 * Example:
	 *     if (condition)
	 *         consequence
	 *     else
	 *         alternative
	 *
	 * @param loc         The given location of this statement.
	 * @param condition   The if condition.
	 * @param consequence The consequence statement.
	 * @param alternative The alternative statement (if available, null otherwise)
	 * @return A statement which represents this if statement.
	 */
	public Statement createIfStatement(Locatable loc, Expression condition, Statement consequence,
			Statement alternative);

	/**
	 * Creates a return statement with the given location.
	 *
	 * Example:
	 *     return;
	 *     return 42;
	 *     return a + b;
	 *
	 * @param loc        The given location of this statement.
	 * @param expression The return expression (if available, null otherwise)
	 * @return A statement which represents this return statement.
	 */
	public Statement createReturnStatement(Locatable loc, Expression expression);

	/**
	 * Creates a while statement with the given location.
	 *
	 * Example:
	 *     while (condition)
	 *         body
	 *
	 * @param loc       The given location of this statement.
	 * @param condition The condition of the while loop.
	 * @param body      The body of the while loop (which is a statement).
	 *
	 * @return A statement which represents this while statement.
	 */
	public Statement createWhileStatement(Locatable loc, Expression condition, Statement body);

	/**
	 * Creates a while statement with the given location and verification annotations.
	 *
	 * Example 1:
	 *     while (condition)
	 *         _Invariant(e)
	 *         _Term(e; k)
	 *         body
	 *
	 * Example 2:
	 *     while (condition)
	 *         _Invariant(e)
	 *         body
	 *
	 * @param loc       The given location of this statement.
	 * @param condition The condition of the while loop.
	 * @param body      The body of the while loop (which is a statement).
	 * @param invariant The loop invariant.
	 * @param term      The termination function (if available, null otherwise).
	 * @param loopBound The loop bound variable used is the termination function (if available, null otherwise).
	 *
	 * @return A statement which represents this annotated while statement.
	 *
	 * @remarks Only required for the WP exercise.
	 */
	public default Statement createAnnotatedWhileStatement(
		Locatable loc, Expression condition, Statement body, 
		Expression invariant, Expression term, Token loopBound)
	{
		throw new UnsupportedOperationException("Annotated while statement not implemented!");
	}

	/**
	 * Creates an assume statement with the given location.
	 *
	 * Example:
	 *     _Assume(x > 0);
	 *
	 * @param loc       The given location of this statement.
	 * @param condition The condition of the assume statement.
	 * @return A statement which represents this assume statement.
	 *
	 * @remarks Only required for the WP exercise.
	 */
	public default Statement createAssumeStatement(Locatable loc, Expression condition) {
		throw new UnsupportedOperationException("Assume statement not implemented!");
	}

	/**
	 * Creates an assert statement with the given location.
	 *
	 * Example:
	 *     _Assert(x > 0);
	 *
	 * @param loc       The given location of this statement.
	 * @param condition The condition of the assume statement.
	 * @return A statement which represents this assume statement.
	 *
	 * @remarks Only required for the WP exercise.
	 */
	public default Statement createAssertStatement(Locatable loc, Expression condition) {
		throw new UnsupportedOperationException("Assert statement not implemented!");
	}

	// ------------ Types ------------

	/**
	 * Creates a function type.
	 *
	 * Example:
	 *     void(int, int)
	 *     is a function type which returns void and takes two parameters of type int.
	 *
	 * @param returnType The return type.
	 * @param parameters A list of parameter types.
	 * @return A type which represents this function type.
	 */
	public Type createFunctionType(Type returnType, List<Type> parameters);

	/**
	 * Creates a pointer type.
	 *
	 * Example:
	 *     void*
	 *
	 * @param pointsTo The type to which this pointer type points to.
	 * @return A type which represents this pointer type.
	 */
	public Type createPointerType(Type pointsTo);

	/**
	 * Creates a base type (int/char/void).
	 *
	 * @param kind The token type which represents this base type.
	 * @return A type which represents this base type.
	 */
	public Type createBaseType(TokenKind kind);

	// ------------ Expressions ------------

	/**
	 * Creates a binary expression with the given operator.
	 *
	 * Example:
	 *     a + b
	 *     a - b
	 *
	 * @param operator The operator of this binary expression.
	 * @param left     The expression of the left operand.
	 * @param right    The expression of the right operand.
	 * @return An expression which represents this binary expression.
	 */
	public Expression createBinaryExpression(Token operator, Expression left, Expression right);

	/**
	 * Creates a function call expression with the given function reference.
	 *
	 * @param token     The opening parenthesis token of this call expression (used
	 *                  for the location).
	 * @param callee    The reference to the called function.
	 * @param arguments The arguments of this call.
	 * @return An expression which represents this call expression.
	 */
	public Expression createCallExpression(Token token, Expression callee, List<Expression> arguments);

	/**
	 * BONUS Creates a conditional expression.
	 *
	 * Example:
	 *     a > 0 ? b : c
	 *     condition ? consequence : alternative
	 *
	 * @param token       The question mark token of this conditional expression
	 *                    (used for the location).
	 * @param condition   The condition of this expression.
	 * @param consequence The consequence of this expression.
	 * @param alternative The alternative of this expression.
	 * @return An expression which represents this conditional expression.
	 */
	public default Expression createConditionalExpression(Token token, Expression condition, Expression consequence,
			Expression alternative) {
		throw new UnsupportedOperationException("Conditional expression not implemented!");
	}

	/**
	 * Creates an unary expression.
	 *
	 * @param operator The operator of the expression.
	 * @param postfix  True if this is a postfix operator, false in the case of a
	 *                 prefix operator. Postfix must be handled for a BONUS
	 *                 exercise.
	 * @param operand  The operand for this operator.
	 * @return An expression which represents this unary expression.
	 */
	public Expression createUnaryExpression(Token operator, boolean postfix, Expression operand);

	/**
	 * Creates a primary expression.
	 *
	 * Example:
	 *     i
	 *     42
	 *     "hi"
	 *     'a'
	 *
	 * @param token The token of this primary expression.
	 * @return An expression which represents this primary expression.
	 */
	public Expression createPrimaryExpression(Token token);

	// ------------ External Declarations ------------

	/**
	 * Creates a declaration for a global variable or function with the given type
	 * and name.
	 *
	 * Example 1:
	 *     void foo(int a);
	 *     Type: void(int)
	 *     Name: foo
	 * Example 2:
	 *     int bar(int a,char b);
	 *     Type: int(int,char)
	 *     Name: bar
	 *
	 * @param type The type of the declaration.
	 * @param name The name of the declaration.
	 */
	public void createExternalDeclaration(Type type, Token name);

	/**
	 * Creates a function definition with the given type, function name, parameter
	 * names and its body.
	 *
	 * Example:
	 *     void foo(int a, char b) {}
	 *
	 *     Function type:      void(int, char)
	 *     Name:               foo
	 *     Parameter names:    [a, b]
	 *     Body:               {}
	 *
	 * @param type           The type of the function.
	 * @param name           The function name.
	 * @param parameterNames List of the parameter names.
	 * @param body           The body of the function.
	 */
	public void createFunctionDefinition(Type type, Token name, List<Token> parameterNames, Statement body);
}
