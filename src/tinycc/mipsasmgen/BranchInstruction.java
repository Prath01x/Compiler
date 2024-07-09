package tinycc.mipsasmgen;

/**
 * Represents a branch instruction of the MIPS processor.
 *
 * It is used by the MipsAsmGenerator.
 *
 * @see MipsAsmGenerator
 */
public enum BranchInstruction {
	BEQ("beq"),
	BNE("bne"),
	BLEZ("blez"),
	BGTZ("bgtz"),
	BLTZ("bltz"),
	BGEZ("bgez");

	private final String name;

	private BranchInstruction(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
