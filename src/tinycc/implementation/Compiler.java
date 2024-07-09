package tinycc.implementation;

import tinycc.diagnostic.Diagnostic;
import tinycc.parser.ASTFactory;
import tinycc.parser.Lexer;
import tinycc.parser.Parser;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.MipsAsmGen;

/**
 * The main compiler class.
 *
 * An instance of this class will handle a single translation unit (e.g. input
 * file). There will be multiple instances of your class during runtime of your
 * compiler. You can change this class but the given name and signature of
 * methods and the name of the class must not be modified.
 */
public class Compiler {

	Diagnostic diagnostic;

	/**
	 * Initializes the compiler class with the given diagnostic module
	 *
	 * @param diagnostic The diagnostic module to use
	 * @see Diagnostic
	 */
	public Compiler(final Diagnostic diagnostic) {
		this.diagnostic = diagnostic;
	}

	/**
	 * Returns the current ASTFactory which is used internally.
	 *
	 * @return The current ASTFactory which is used internally.
	 * @see ASTFactory
	 */
	public ASTFactory getASTFactory() {
		throw new UnsupportedOperationException("TODO: implement this");
	}

	/**
	 * Parses a single translation unit which is given by an instance of the Lexer
	 * class.
	 *
	 * @param lexer The lexer to use
	 * @see Lexer
	 * @remarks This function is invoked only once in each instance of the compiler
	 *          class.
	 */
	public void parseTranslationUnit(final Lexer lexer) {
		Parser parser = new Parser(diagnostic, lexer, this.getASTFactory());
		parser.parseTranslationUnit();
	}

	/**
	 * Checks the semantics of the input program.
	 *
	 * @see ASTFactory
	 * @remarks Use the diagnostics module to report errors. This function is
	 *          invoked only once in each instance of the compiler class.
	 */
	public void checkSemantics() {
		throw new UnsupportedOperationException("TODO: implement this");
	}

	/**
	 * Performs optimizations on the input program.
	 *
	 * @remarks Bonus exercise.
	 */
	public void performOptimizations() {
		throw new UnsupportedOperationException("TODO: implement this");
	}

	/**
	 * Generates code for the input program.
	 *
	 * @param out The target output stream.
	 * @remarks This function is invoked only once in each instance of the compiler
	 *          class. Only necessary if mentioned in the project description.
	 */
	public void generateCode(final MipsAsmGen out) {
		throw new UnsupportedOperationException("TODO: implement this");
	}

	/**
	 * Generates verification conditions for the input program.
	 *
	 * @remarks This function is invoked only once in each instance of the compiler
	 *          class. Only necessary if mentioned in the project description.
	 */
	public Formula genVerificationConditions() {
		throw new UnsupportedOperationException("TODO: implement this");
	}
}
