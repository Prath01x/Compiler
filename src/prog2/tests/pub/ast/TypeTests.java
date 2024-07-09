package prog2.tests.pub.ast;

import java.util.ArrayList;

import org.junit.Test;

import tinycc.implementation.type.Type;
import tinycc.parser.TokenKind;
import prog2.tests.ASTExercise;
import prog2.tests.CompilerTests;
import prog2.tests.PublicTest;

public class TypeTests extends CompilerTests implements PublicTest, ASTExercise {
	@Test
	public void testChar() {
		final Type t = astFactory.createBaseType(TokenKind.CHAR);
		assertEqualsNormalized("Type_char", t.toString());
	}

	@Test
	public void testInt() {
		final Type t = astFactory.createBaseType(TokenKind.INT);
		assertEqualsNormalized("Type_int", t.toString());
	}

	@Test
	public void testVoid() {
		final Type t = astFactory.createBaseType(TokenKind.VOID);
		assertEqualsNormalized("Type_void", t.toString());
	}

	@Test
	public void testCharPointer() {
		final Type pointsTo = astFactory.createBaseType(TokenKind.CHAR);
		final Type t = astFactory.createPointerType(pointsTo);
		assertEqualsNormalized("Pointer[Type_char]", t.toString());
	}

	@Test
	public void testIntPointer() {
		final Type pointsTo = astFactory.createBaseType(TokenKind.INT);
		final Type t = astFactory.createPointerType(pointsTo);
		assertEqualsNormalized("Pointer[Type_int]", t.toString());
	}

	@Test
	public void testVoidPointer() {
		final Type pointsTo = astFactory.createBaseType(TokenKind.VOID);
		final Type t = astFactory.createPointerType(pointsTo);
		assertEqualsNormalized("Pointer[Type_void]", t.toString());
	}

	@Test
	public void testVoidPP() {
		final Type pointsTo = astFactory.createBaseType(TokenKind.VOID);
		final Type t1 = astFactory.createPointerType(pointsTo);
		final Type t2 = astFactory.createPointerType(t1);
		assertEqualsNormalized("Pointer[Pointer[Type_void]]", t2.toString());
	}

	@Test
	public void testFunctionTypeIntIntInt() {
		final Type intType = astFactory.createBaseType(TokenKind.INT);
		final ArrayList<Type> params = new ArrayList<Type>(2);
		params.add(intType);
		params.add(intType);
		final Type f = astFactory.createFunctionType(intType, params);
		assertEqualsNormalized("FunctionType[Type_int,Type_int,Type_int]", f.toString());
	}

	@Test
	public void testFunctionTypeVoidInt() {
		final Type intType = astFactory.createBaseType(TokenKind.INT);
		final ArrayList<Type> params = new ArrayList<Type>(2);
		params.add(intType);
		final Type f = astFactory.createFunctionType(astFactory.createBaseType(TokenKind.VOID), params);
		assertEqualsNormalized("FunctionType[Type_void,Type_int]", f.toString());
	}

	@Test
	public void testFunctionTypeVoidVoid() {
		final Type f = astFactory.createFunctionType(astFactory.createBaseType(TokenKind.VOID), new ArrayList<Type>(0));
		assertEqualsNormalized("FunctionType[Type_void]", f.toString());
	}
}
