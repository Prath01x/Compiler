package prog2.tests.pub.ast;

import org.junit.Test;

import prog2.tests.ASTExercise;
import prog2.tests.CompilerTests;
import prog2.tests.PublicTest;
import tinycc.implementation.expression.Expression;
import tinycc.parser.TokenKind;

public class UnaryExprTests extends CompilerTests implements PublicTest, ASTExercise {
	@Test
	public void testSizeof() {
		final ASTMaker m = new ASTMaker("testSizeof");
		final Expression a = m.createNumber("42");
		final Expression e = m.createUnary(TokenKind.SIZEOF, a);
		assertEqualsNormalized("Unary_sizeof[Const_42]", e.toString());
	}

	@Test
	public void testAddress() {
		final ASTMaker m = new ASTMaker("testAddress");
		m.createExternalDeclaration(m.createInt(), "a");
		final Expression a = m.createIdentifier("a");
		final Expression e = m.createUnary(TokenKind.AND, a);
		assertEqualsNormalized("Unary_&[Var_a]", e.toString());
	}

	@Test
	public void testIndirection() {
		final ASTMaker m = new ASTMaker("testIndirection");
		m.createExternalDeclaration(m.createInt(), "a");
		final Expression a = m.createIdentifier("a");
		final Expression e = m.createUnary(TokenKind.ASTERISK, a);
		assertEqualsNormalized("Unary_*[Var_a]", e.toString());
	}
}
