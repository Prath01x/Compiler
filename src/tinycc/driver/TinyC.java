package tinycc.driver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import tinycc.diagnostic.Location;
import tinycc.diagnostic.PrintDiagnostic;
import tinycc.implementation.Compiler;
import tinycc.logic.Formula;
import tinycc.logic.UnaryOperator;
import tinycc.logic.UnaryOpFormula;
import tinycc.logic.solver.Logic;
import tinycc.logic.solver.SolverInterface;
import tinycc.logic.solver.SolverResult;
import tinycc.logic.solver.z3.Z3Translator;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Lexer;

class ArgumentParser {
	private final String[] args;

	private int idx;
	private int pos;

	public ArgumentParser(final String[] args) {
		this.args = args;
		this.idx = 0;
		this.pos = 1;
	}

	public int getOption() {
		if (idx == args.length)
			return -1;
		String arg = args[idx];
		if (pos == arg.length()) {
			if (++idx == args.length)
				return -1;
			arg = args[idx];
			pos = 1;
		}
		if (arg.length() >= 2 && arg.charAt(0) == '-')
			return arg.charAt(pos++);
		return 0;
	}

	public String getValue() {
		String arg = args[idx++];
		if (arg.length() >= 2 && arg.charAt(0) == '-') {
			if (pos < arg.length()) {
				arg = arg.substring(pos);
			} else if (idx != args.length) {
				arg = args[idx++];
			} else {
				arg = null;
			}
			pos = 1;
		}
		return arg;
	}
}

/**
 * Main class of the compiler
 *
 */
public class TinyC {
	/**
	 * Runs the compiler
	 *
	 * @param diagnostic The diagnostic instance to use
	 * @param args       The command line arguments
	 */
	private static void run(final PrintDiagnostic diagnostic, final String[] args) {
		final List<String> inputNames = new ArrayList<String>();
		boolean optimize = false;
		boolean compile = false;
		boolean verify = false;
		String outName = null;
		final ArgumentParser argParser = new ArgumentParser(args);
		argParsing: for (;;) {
			final int option = argParser.getOption();
			switch (option) {
			case -1:
				break argParsing;

			case 0:
				inputNames.add(argParser.getValue());
				continue;

			case 'O': {
				optimize = true;
				continue;
			}

			case 'o': {
				final String arg = argParser.getValue();
				if (arg == null)
					break;
				outName = arg;
				continue;
			}
			case 'c':
				compile = true;
				continue;

			case 'v':
				verify = true;
				continue;

			default:
				diagnostic.printError(null, "unknown option '-%c'", option);
				continue;
			}

			diagnostic.printError(null, "'-%c' requires an argument", option);
		}

		final int nNames = inputNames.size();
		if (nNames == 0) {
			diagnostic.printError(null, "no input files specified");
		} else if (nNames != 1 && outName != null) {
			diagnostic.printError(null, "'-o' can only be used with a single input name");
		} else if (!compile && outName != null) {
			diagnostic.printError(null, "'-o' can only be used when compiling");
		}

		if (diagnostic.hasNewErrors())
			return;

		// For every file do...
		for (final String inName : inputNames) {
			try {
				Reader r = null;
				try {
					if (inName.equals("-")) {
						r = new InputStreamReader(System.in);
					} else {
						try {
							r = new FileReader(inName);
						} catch (final FileNotFoundException e) {
							diagnostic.printError(new Location(inName), "no such file or directory");
							continue;
						}
						r = new BufferedReader(r);
					}

					// Init a new compiler with the given diagnostic module
					final Compiler compiler = new Compiler(diagnostic);
					// Create a new lexer with the input file
					final Lexer lexer = new Lexer(diagnostic, r, inName);
					// Parse the given translation unit
					compiler.parseTranslationUnit(lexer);
					if (diagnostic.hasNewErrors())
						continue;

					// 1) Try to check semantics
					compiler.checkSemantics();
					if (diagnostic.hasNewErrors())
						continue;

					// 2) Generate verification conditions
					if (verify) {
						Formula f = compiler.genVerificationConditions();
						System.out.println("Generated formula: " + f.toString());
						
						SolverInterface translator = new Z3Translator();

						// Solvers can only return "satisfiable" or "unsatisfiable".
						// To prove a universally true formula, we therefore negate it and expect the
						// result to be unsatisfiable.
						Formula negated = new UnaryOpFormula(UnaryOperator.NOT, f);
						SolverResult result = translator.querySatisfiability(negated, Logic.QF_NIA);

						if (result.isUnSatifiable()) {
							System.out.println("Formula is valid. Verification successful.");
						} else {
							System.out.println("Formula is invalid. Program could not be verified.");
						}
					}

					if (optimize)
						compiler.performOptimizations();

					// 3) Generate code

					if (compile) {
						final PrintStream out = makeOutputStream(outName, inName);
						try {
							compiler.generateCode(new MipsAsmGen(out));
						} finally {
							if (out != System.out)
								out.close();
						}
					}
				} finally {
					if (r != null)
						r.close();
				}
			} catch (final Exception e) {
				diagnostic.printError(new Location(inName), "%s", e);
			}
		}
	}

	private static PrintStream makeOutputStream(String outName, final String inName) throws FileNotFoundException {
		if (outName == null) {
			outName = inName.substring(inName.lastIndexOf('/') + 1);
			if (outName.endsWith(".c"))
				outName = outName.substring(0, outName.length() - 2);
			outName += ".s";
		} else if (outName.equals("-")) {
			return System.out;
		}
		return new PrintStream(new FileOutputStream(outName));
	}

	/**
	 * Entry point of the program
	 *
	 * @param args
	 */
	public static void main(final String[] args) {
		// Instantiate default diagnostic module and run the compiler
		final PrintDiagnostic diagnostic = new PrintDiagnostic(System.err);
		run(diagnostic, args);
		// Print a summary and use the return code as exit code
		System.exit(diagnostic.printSummary());
	}
}
