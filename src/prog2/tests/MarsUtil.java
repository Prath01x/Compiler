package prog2.tests;

import java.util.ArrayList;

import mars.Globals;
import mars.MIPSprogram;
import mars.ProcessingException;
import mars.assembler.SourceLine;
import mars.mips.hardware.Memory;
import mars.mips.hardware.RegisterFile;

/**
 * Utility class used to run MIPS assembly code.
 */
public class MarsUtil {
	private static final int STEPS = 10_000_000;
	private final static String preload =
			/* data segment */
			"	.data\n" +

			/* scratch space */
			"_scratch:\n" +
			"	.space 64\n" +

			/* code segment */
			"	.text\n" +

			/* wrapper that calls main and the exit syscall */
			"__crt0:\n" +
			"	jal main\n" +
			"	move $a0, $v0\n" +
			"	li $v0, 17\n" +
			"	syscall\n" +

			/* wrapper for print_integer */
			"print_integer:\n" +
			"	li $v0, 1\n" +
			"	syscall\n" +
			"	jr $ra\n" +

			/* wrapper for print_string */
			"print_string:\n" +
			"	li $v0, 4\n" +
			"	syscall\n" +
			"	jr $ra\n" +

			/* wrapper for print_char */
			"print_char:\n" +
			"	li $v0, 11\n" +
			"	syscall\n" +
			"	jr $ra\n" +

			/* wrapper for sbrk */
			"malloc:\n" +
			"	li $v0, 9\n" +
			"	syscall\n" +
			"	jr $ra\n" +

			"get_scratch:\n" +
			"	la $v0, _scratch\n" +
			"	jr $ra\n";

	private final String code;

	/**
	 * Constructs the simulator from a given string of MIPS assembly code.
	 */
	public MarsUtil(final String code) {
		this.code = code;
	}

	/**
	 * Runs the Mars simulator and returns the result returned by the main function.
	 * 
	 * @throws MarsException
	 */
	public synchronized int run() throws MarsException {
		Globals.initialize(false);
		RegisterFile.resetRegisters();
		Memory.getInstance().clear();

		final MIPSprogram mips = new MIPSprogram();

		/* tokenize */
		final ArrayList<SourceLine> sourceLineList = generateSourceLines(mips);
		mips.setSourceLineList(sourceLineList);
		try {
			mips.tokenize();
		} catch (final ProcessingException e) {
			throw new MarsException("tokenize", e);
		}

		/* assemble */
		final ArrayList<MIPSprogram> programs = new ArrayList<MIPSprogram>(1);
		programs.add(mips);
		try {
			mips.assemble(programs, true);
		} catch (final ProcessingException e) {
			throw new MarsException("assemble", e);
		}

		/* set PC to __crt0 */
		final int crt0 = mips.getLocalSymbolTable().getAddress("__crt0");
		RegisterFile.setProgramCounter(crt0);

		/* simulate */
		try {
			mips.simulate(STEPS);
		} catch (final ProcessingException e) {
			throw new MarsException("simulate", e);
		}
		return getRegister(4); /* $a0 */
	}

	private static int getRegister(final int num) {
		/* NOTE: if needed as public, we must copy registers before leaving run */
		return RegisterFile.getValue(num);
	}

	private ArrayList<SourceLine> generateSourceLines(final MIPSprogram mips) {
		final ArrayList<SourceLine> lines = new ArrayList<SourceLine>();

		if (CompilerTests.PRINT_ASM_CODE)
			System.out.println("ASM with preload:");
		int i = 0;
		for (final String l : preload.split("\n")) {
			if (CompilerTests.PRINT_ASM_CODE)
				System.out.println(l);
			lines.add(new SourceLine(l, mips, ++i));
		}
		for (final String l : code.split("\n")) {
			if (CompilerTests.PRINT_ASM_CODE)
				System.out.println(l);
			lines.add(new SourceLine(l, mips, ++i));
		}

		return lines;
	}
}
