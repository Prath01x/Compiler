package tinycc.mipsasmgen;

/**
 * Represents an special instruction of the MIPS processor with works on
 * registers.
 *
 * It is used by the MipsAsmGenerator.
 *
 * @see MipsAsmGenerator
 * @see SpecialInstruction
 */
public enum SpecialRegisterInstruction {
	MFHI("mfhi"),
	MFLO("mflo"),
	MTHI("mthi"),
	MTLO("mtlo");

	private final String name;

	private SpecialRegisterInstruction(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
