package tinycc.mipsasmgen;

/**
 * Represents a memory-access instruction of the MIPS processor.
 *
 * It is used by the MipsAsmGenerator.
 *
 * @see MipsAsmGenerator
 */
public enum MemoryInstruction {
	LA("la"),
	LB("lb"),
	LBU("lbu"),
	LH("lh"),
	LHU("lhu"),
	LW("lw"),
	SB("sb"),
	SH("sh"),
	SW("sw");

	private final String name;

	private MemoryInstruction(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
