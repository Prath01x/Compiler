package tinycc.mipsasmgen;

/**
 * Represents a jump instruction of the MIPS processor.
 *
 * It is used by the MipsAsmGenerator.
 *
 * @see MipsAsmGenerator
 */
public enum JumpInstruction {
	J("j"),
	JAL("jal");

	private final String name;

	private JumpInstruction(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
