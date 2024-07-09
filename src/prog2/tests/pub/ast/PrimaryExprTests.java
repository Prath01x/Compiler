package prog2.tests.pub.ast;

import org.junit.Test;

import prog2.tests.ASTExercise;
import prog2.tests.CompilerTests;
import prog2.tests.PublicTest;
import tinycc.implementation.expression.Expression;

public class PrimaryExprTests extends CompilerTests implements PublicTest, ASTExercise {
	@Test
	public void testNumber() {
		final ASTMaker m = new ASTMaker("testNumber");
		final Expression e = m.createNumber("1337");
		assertEqualsNormalized("Const_1337", e.toString());
	}

	@Test
	public void testCharacter() {
		final ASTMaker m = new ASTMaker("testCharacter");
		final Expression e = m.createCharacter("y");
		assertEqualsNormalized("Const_'y'", e.toString());
	}

	@Test
	public void testString() {
		final ASTMaker m = new ASTMaker("testString");
		final Expression e = m.createString("Hallo Welt!");
		assertEqualsNormalized("Const_\"Hallo Welt!\"", e.toString());
	}

	@Test
	public void testIdentifier() {
		final ASTMaker m = new ASTMaker("testIdentifier");
		m.createExternalDeclaration(m.createInt(), "a");
		final Expression e = m.createIdentifier("a");
		assertEqualsNormalized("Var_a", e.toString());
	}
}
