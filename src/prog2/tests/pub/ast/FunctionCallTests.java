package prog2.tests.pub.ast;

import org.junit.Test;

import prog2.tests.ASTExercise;
import prog2.tests.CompilerTests;
import prog2.tests.PublicTest;
import tinycc.implementation.expression.Expression;

public class FunctionCallTests extends CompilerTests implements PublicTest, ASTExercise {
	@Test
	public void testCallNoArgs() {
		final ASTMaker m = new ASTMaker("testCallNoArgs");
		final Expression e = m.createIdentifier("foo");
		final Expression c = m.createCall(e);
		assertEqualsNormalized("Call[Var_foo]", c.toString());
	}

	@Test
	public void testCallOneArg() {
		final ASTMaker m = new ASTMaker("testCallOneArg");
		final Expression e = m.createIdentifier("foo");
		final Expression e2 = m.createNumber("42");
		final Expression c = m.createCall(e, e2);
		assertEqualsNormalized("Call[Var_foo, Const_42]", c.toString());
	}
}
