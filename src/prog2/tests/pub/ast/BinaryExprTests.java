package prog2.tests.pub.ast;

import org.junit.Test;

import prog2.tests.ASTExercise;
import prog2.tests.CompilerTests;
import prog2.tests.PublicTest;
import tinycc.implementation.expression.Expression;
import tinycc.parser.TokenKind;

public class BinaryExprTests extends CompilerTests implements PublicTest, ASTExercise {

	private void testBinarySimple(final TokenKind kind) {
		final ASTMaker m = new ASTMaker("testBinary" + kind.name());
		final Expression a = m.createNumber("42");
		final Expression b = m.createNumber("1337");
		final Expression e = m.createBinary(kind, a, b);
		assertEqualsNormalized("Binary_"+kind+"[Const_42, Const_1337]", e.toString());
	}

	@Test
	public void testPlus() {
		testBinarySimple(TokenKind.PLUS);
	}

	@Test
	public void testMinus() {
		testBinarySimple(TokenKind.MINUS);
	}

	@Test
	public void testMultiply() {
		testBinarySimple(TokenKind.ASTERISK);
	}

	@Test
	public void testDivide() {
		testBinarySimple(TokenKind.SLASH);
	}

	@Test
	public void testEqual() {
		testBinarySimple(TokenKind.EQUAL_EQUAL);
	}

	@Test
	public void testNotEqual() {
		testBinarySimple(TokenKind.BANG_EQUAL);
	}

	@Test
	public void testLessThan() {
		testBinarySimple(TokenKind.LESS);
	}

	@Test
	public void testGreaterThan() {
		testBinarySimple(TokenKind.GREATER);
	}

	@Test
	public void testLessOrEqual() {
		testBinarySimple(TokenKind.LESS_EQUAL);
	}

	@Test
	public void testGreaterOrEqual() {
		testBinarySimple(TokenKind.GREATER_EQUAL);
	}

	@Test
	public void testAssign() {
		final ASTMaker m = new ASTMaker("testAssignment");
		m.createExternalDeclaration(m.createInt(), "a");
		final Expression lhs = m.createIdentifier("a");
		final Expression rhs = m.createNumber("42");
		final Expression e = m.createBinary(TokenKind.EQUAL, lhs, rhs);
		assertEqualsNormalized("Binary_=[Var_a, Const_42]", e.toString());
	}
}
