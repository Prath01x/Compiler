package prog2.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import junit.runner.Version;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

import tinycc.diagnostic.Locatable;
import tinycc.diagnostic.Location;
import tinycc.implementation.Compiler;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.statement.Statement;
import tinycc.implementation.type.Type;
import tinycc.logic.Formula;
import tinycc.logic.UnaryOpFormula;
import tinycc.logic.UnaryOperator;
import tinycc.logic.solver.Logic;
import tinycc.logic.solver.SolverInterface;
import tinycc.logic.solver.SolverResult;
import tinycc.logic.solver.z3.Z3Translator;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.ASTFactory;
import tinycc.parser.Lexer;
import tinycc.parser.Token;
import tinycc.parser.TokenKind;

public class CompilerTests {
	public static final String INPUT_NAME = "test.c";

	/* set these to true, if you need some help while debugging */
	public static final boolean PRINT_C_CODE = false;
	public static final boolean PRINT_ASM_CODE = false;
	public static final boolean PRINT_VC = false;

	protected FatalDiagnostic diagnostic;
	protected Compiler compiler;
	protected ASTFactory astFactory;
	private final String NORMALIZE_WHITESPACE_TO = "";

	@Rule
	public TestRule globalTimeout = new DisableOnDebug(new Timeout(1, TimeUnit.SECONDS));

	@Before
	public void setUp() {
		diagnostic = new FatalDiagnostic();
		compiler = new Compiler(diagnostic);
		astFactory = compiler.getASTFactory();
	}

	private String normalizeString(final String s) {
		return s.replaceAll("\\s+", NORMALIZE_WHITESPACE_TO);
	}

	protected void assertEqualsNormalized(final String expected, final String got) {
		final String expected_normalized = normalizeString(expected);
		final String got_normalized = normalizeString(got);

		// FIXME: In JUnit < 5, assertEquals uses square brackets to indicate 
		// the spot where the two strings differ. However, the abstract syntax
		// also uses square brackets, which leads to confusing output. We 
		// therefore check for the version and manually fail the test if
		// JUnit < 5 is used.
		if (Version.id().startsWith("5")) {
			assertEquals(expected_normalized, got_normalized);
		} else if (!expected_normalized.equals(got_normalized)) {
			fail("expected: <" + expected_normalized + "> but was <" + got_normalized + ">");
		}
	}

	protected void checkLocation(final FatalCompilerError e, final Locatable expected) {
		final Locatable l = e.getLocatable();
		if (!equals(expected, l))
			fail("expected <" + toString(expected) + ">, got <" + e.getMessage() + "> (the actual message is ignored)");
	}

	protected void checkLocation(final FatalCompilerError e, final String inputName, final int line, final int column) {
		checkLocation(e, new Location(inputName, line, column));
	}

	public static boolean equals(final Locatable a, final Locatable b) {
		return a.getInputName().equals(b.getInputName()) && a.getLine() == b.getLine()
				&& a.getColumn() == b.getColumn();
	}

	public static String toString(final Locatable l) {
		return l.getInputName() + ':' + l.getLine() + ':' + l.getColumn();
	}

	protected class ASTMaker {
		private final Locatable l;

		public ASTMaker(final String name) {
			l = new Location(name);
		}

		public void createExternalDeclaration(final Type type, final String name) {
			astFactory.createExternalDeclaration(type, createIdentifierToken(name));
		}

		public Expression createBinary(final TokenKind operator, final Expression left, final Expression right) {
			return astFactory.createBinaryExpression(createToken(operator), left, right);
		}

		public Expression createIdentifier(final String name) {
			return astFactory.createPrimaryExpression(createIdentifierToken(name));
		}

		Token createToken(final TokenKind kind) {
			return new Token(l, kind);
		}

		public Expression createCall(final Expression callee, final Expression... arguments) {
			return astFactory.createCallExpression(createToken(TokenKind.LPAREN), callee, Arrays.asList(arguments));
		}

		public Expression createCharacter(final String c) {
			return astFactory.createPrimaryExpression(new Token(l, TokenKind.CHARACTER, c));
		}

		public Expression createConditional(final Expression cond, final Expression con, final Expression alt) {
			return astFactory.createConditionalExpression(createToken(TokenKind.QUESTION_MARK), cond, con, alt);
		}

		public Expression createNumber(final String value) {
			return astFactory.createPrimaryExpression(new Token(l, TokenKind.NUMBER, value));
		}

		public Expression createString(final String c) {
			return astFactory.createPrimaryExpression(new Token(l, TokenKind.STRING, c));
		}

		public Expression createUnary(final TokenKind operator, final Expression operand) {
			return astFactory.createUnaryExpression(createToken(operator), false, operand);
		}

		public Expression createPostfix(final TokenKind operator, final Expression operand) {
			return astFactory.createUnaryExpression(createToken(operator), true, operand);
		}

		public Statement createBlock(final Statement... body) {
			return astFactory.createBlockStatement(l, Arrays.asList(body));
		}

		public Statement createBreak() {
			return astFactory.createBreakStatement(l);
		}

		public Statement createContinue() {
			return astFactory.createContinueStatement(l);
		}

		public Statement createDeclaration(final Type type, final String name) {
			return astFactory.createDeclarationStatement(type, createIdentifierToken(name), null);
		}

		public Statement createDeclaration(final Type type, final String name, final Expression initializer) {
			return astFactory.createDeclarationStatement(type, createIdentifierToken(name), initializer);
		}

		public Statement createExpressionStatement(final Expression e) {
			return astFactory.createExpressionStatement(l, e);
		}

		public Statement createIf(final Expression cond, final Statement con, final Statement alt) {
			return astFactory.createIfStatement(l, cond, con, alt);
		}

		public Statement createReturn(final Expression expression) {
			return astFactory.createReturnStatement(l, expression);
		}

		public Statement createWhile(final Expression cond, final Statement body) {
			return astFactory.createWhileStatement(l, cond, body);
		}

		public Statement createWhile(final Expression cond, final Statement body, final Expression invariant) {
			return astFactory.createAnnotatedWhileStatement(l, cond, body, invariant, null, null);
		}

		public Statement createWhile(final Expression cond, final Statement body, 
				final Expression invariant, final Expression term, Token bound) {
			return astFactory.createAnnotatedWhileStatement(l, cond, body, invariant, term, bound);
		}

		public Statement createAssert(final Expression cond) {
			return astFactory.createAssertStatement(l, cond);
		}

		public Statement createAssume(final Expression cond) {
			return astFactory.createAssumeStatement(l, cond);
		}

		public Type createChar() {
			return astFactory.createBaseType(TokenKind.CHAR);
		}

		public Type createFunctionType(final Type returnType, final Type... paramTypes) {
			return astFactory.createFunctionType(returnType, Arrays.asList(paramTypes));
		}

		public Type createInt() {
			return astFactory.createBaseType(TokenKind.INT);
		}

		public Type createPointer(final Type type) {
			return astFactory.createPointerType(type);
		}

		public Type createVoid() {
			return astFactory.createBaseType(TokenKind.VOID);
		}

		private Token createIdentifierToken(final String name) {
			return new Token(l, TokenKind.IDENTIFIER, name);
		}
	}

	/**
	 * Parses and checks the semantics for the input program.
	 * 
	 * @param code The input program
	 */
	public void checkCode(final String code) {
		checkCode(INPUT_NAME, code);
	}

	protected void checkCode(final String name, final String code) {
		final StringReader r = new StringReader(code);
		final Lexer l = new Lexer(diagnostic, r, name);
		compiler.parseTranslationUnit(l);
		compiler.checkSemantics();
	}

	private static String excToString(final Exception e) {
		final String stack = Arrays.toString(e.getStackTrace()).replace(",", "\n\t\t  at ");
		return "\t " + e + ":\n\t\t  at " + stack.substring(1, stack.length() - 1);
	}

	/**
	 * Parses and checks the semantics for the input program.
	 * Expects a FatalCompilerError to be output, indicating an error at a certain
	 * location, otherwise calls fail.
	 * 
	 * @param code The input program
	 * @param line The expected line of the reported error
	 * @param column The expected column of the reported error
	 */
	public void checkCodeNegative(final String code, final int line, final int column) {
		checkCodeNegative(INPUT_NAME, code, line, column);
	}

	public void checkCodeNegative(final String program_name, final String code, final int line, final int column) {
		try {
			checkCode(code);
			fail("Semantics check didn't produce any errors");
		} catch (final FatalCompilerError e) {
			checkLocation(e, program_name, line, column);
		}
	}

	/**
	 * Parses, checks the semantics and compiles C code to MIPS assembly.
	 * If an error occured in any of the phases, calls fail.
	 * 
	 * @param cCode The input program to compile and execute.
	 * @return A simulator for the generated assembly code.
	 */
	public MarsUtil prepareCode(final String cCode) {
		final StringReader r = new StringReader(cCode);

		if (PRINT_C_CODE) {
			System.out.println("C code:");
			final String[] lines = cCode.split("\n");
			for (int i = 0; i < lines.length; i++) {
				System.out.printf("%03d: %s\n", i + 1, lines[i]);
				// System.out.println(lines[i]);
			}
		}

		final Lexer lexer = new Lexer(diagnostic, r, INPUT_NAME);
		try {
			compiler.parseTranslationUnit(lexer);
		} catch (Exception e) {
			fail("Failed to parseTranslationUnit:\n" + excToString(e));
		}
		try {
			compiler.checkSemantics();
		} catch (Exception e) {
			fail("Failed to checkSemantics:\n" + excToString(e));
		}

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final PrintStream ps = new PrintStream(baos);
		try {
			compiler.generateCode(new MipsAsmGen(ps));
		} catch (Exception e) {
			fail("Failed to generateCode:\n" + excToString(e));
		}

		if (PRINT_ASM_CODE) {
			System.out.println("ASM code:");
			System.out.println(baos.toString());
		}

		return new MarsUtil(baos.toString());
	}
	
	public SolverResult solver(Formula f) {

		SolverInterface translator = new Z3Translator();

		// Solvers can only return "satisfiable" or "unsatisfiable".
		// To prove a universally true formula, we therefore negate it and expect the
		// result to be unsatisfiable.
		Formula negated = new UnaryOpFormula(UnaryOperator.NOT, f);
		SolverResult result = translator.querySatisfiability(negated, Logic.QF_NIA);
		if (PRINT_VC) {
			System.out.println("Solver result:\n" + result);
		}
		return result;
	}

	/**
	 * Parses, checks semantics, generates verification conditions and verifies the
	 * program.
	 * @param cCode The input program to verify
	 * @return The result of the Z3 solver, given the negation of the generated
	 * verfication formula as input
	 */
	public SolverResult computeVerificationResult(String cCode) {

		checkCode(cCode);

		Formula f = compiler.genVerificationConditions();
		if (f == null)
			return null;

		SolverInterface translator = new Z3Translator();
		if (PRINT_VC) {
			System.out.println("Generated formula:\n" + f);
		}

		// Solvers can only return "satisfiable" or "unsatisfiable".
		// To prove a universally true formula, we therefore negate it and expect the
		// result to be unsatisfiable.
		Formula negated = new UnaryOpFormula(UnaryOperator.NOT, f);
		SolverResult result = translator.querySatisfiability(negated, Logic.QF_NIA);
		if (PRINT_VC) {
			System.out.println("Solver result:\n" + result);
		}
		return result;
	}
}
