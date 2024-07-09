package prog2.tests.pub.sema;

import org.junit.Test;

import prog2.tests.CompilerTests;
import prog2.tests.PublicTest;
import prog2.tests.SemanticsExercise;

public class SemanticsTests extends CompilerTests implements PublicTest, SemanticsExercise {

	@Test
	public void testTypeCheckPositive() {
		checkCode("void f() { 42 + 1337; }");
	}

	@Test
	public void testTypeCheckNegative() {
		checkCodeNegative("int foo() { return 1 + x; }", 1, 24);
	}

	@Test
	public void testCompleteProgram() {
		final String code = ""
				+ "int z;\n"
				+ "char w;\n"
				+ "char *v;\n"
				+ "void swap(int *a, int *b);\n"
				+ "int foo(int x, int y) {\n"
				+ "	x = x + (y * z) / sizeof(z) - (0 - 5);\n"
				+ "	y = 1337;\n"
				+ "	if ((z = 1000) < x) {\n" /* yes, this is a valid expression */
				+ "		return x;\n"
				+ "	} else {\n"
				+ "		swap(&x, &y);\n"
				+ "		return foo(x, y);\n"
				+ "	}\n"
				+ "}\n"
				+ "void swap(int *a, int *b) {\n"
				+ "	int t = *a;\n"
				+ "	*b = *a;\n"
				+ "	*a = t;\n"
				+ "}\n"
				+ "int main() {\n"
				+ "	v = \"foobar\";\n"
				+ "	w = *(v + 3);\n"
				+ "	return foo(42, 42);\n"
				+ "}\n";
		
		checkCode(code);
	}
}
