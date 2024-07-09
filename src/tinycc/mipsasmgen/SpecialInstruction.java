package tinycc.mipsasmgen;

/**
 * Represents a special instruction of the MIPS processor.
 *
 * It is used by the MipsAsmGenerator.
 *
 * @see MipsAsmGenerator
 * @see SpecialRegisterInstruction
 */
public enum SpecialInstruction {
	DIV("div"),
	DIVU("divu"),
	MULT("mult"),
	MULTU("multu");

	private final String name;

	private SpecialInstruction(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
