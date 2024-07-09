package tinycc.mipsasmgen;

/**
 * Represents a jump instruction of the MIPS processor which jumps to a target
 * address (given by a register).
 *
 * It is used by the MipsAsmGenerator.
 *
 * @see MipsAsmGenerator
 */
public enum JumpRegisterInstruction {
	JR("jr"),
	JALR("jalr");

	private String name;

	private JumpRegisterInstruction(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
