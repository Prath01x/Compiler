package tinycc.tests;

import org.junit.Test;
import prog2.tests.CompilerTests;

/**
 * Within this package you can implement your own tests that will
 * be run with the reference implementation.
 *
 * Note that no classes or interfaces will be available, except those initially
 * provided.
 * 
 * Do not write your own tests in this class. Use another class in this package.
 */
public class MyTests extends CompilerTests {
	@Test
	public void testCharSimple() {
		ASTMaker m = new ASTMaker("a");
		assertEqualsNormalized("Const_'a'", (m.createCharacter("a")).toString());
	}

}
