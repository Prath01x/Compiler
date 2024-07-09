package prog2.tests.pub.verify;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import prog2.tests.CompilerTests;
import prog2.tests.PublicTest;
import prog2.tests.WPExercise;
import tinycc.logic.solver.SolverResult;

public class VerificationTests extends CompilerTests implements WPExercise, PublicTest {

	@Test
	public void testSemaAssume() {
		final String code = "\n" +
				"int f(int x) {" +
				"	_Assume(x > 0);" +
				"	return x;" +
				"}\n";
		checkCode(code);
	}

	@Test
	public void testExampleReject() {
		final String code = "\n" +
				"void f(int y, int z) {" +
				"	y = (z = 5);" +
				"}\n";
		assertThrows(Exception.class, () -> computeVerificationResult(code));
	}

	@Test
	public void testSimpleAssign() {
		final String code = "\n" +
				"int f(int x) {" +
				"	x = 5;" +
				"	_Assert(x >= 5);" +
				"	return x;" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testSimpleAssignWrong() {
		final String code = "\n" +
				"int f(int x) {" +
				"	x = 5;" +
				"	_Assert(x < 5);" +
				"	return x;" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isSatifiable());
	}

	@Test
	public void testSimpleAssume() {
		final String code = "\n" +
				"int f(int x) {" +
				"	_Assume(x == 3);" +
				"	_Assert(x < 5);" +
				"	return x;" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

	@Test
	public void testPDFExample() {
		final String code = "\n" +
				"int f(int x, int y) {" +
				"	_Assume(y == 5);" +
				"	x = 5;" +
				"	_Assert(x == y);" +
				"	return x;" +
				"}\n";
		SolverResult res = computeVerificationResult(code);
		assertTrue(res.isUnSatifiable());
	}

}
