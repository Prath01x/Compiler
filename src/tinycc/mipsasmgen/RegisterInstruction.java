package tinycc.mipsasmgen;

/**
 * Represents an instruction of the MIPS processor which works on registers.
 *
 * It is used by the MipsAsmGenerator.
 *
 * @see MipsAsmGenerator
 */
public enum RegisterInstruction {
	ADD("add"),
	ADDU("addu"),
	AND("and"),
	MUL("mul"),
	OR("or"),
	NOR("nor"),
	SUB("sub"),
	SUBU("subu"),
	SLLV("sllv"),
	SLT("slt"),
	SLTU("sltu"),
	SRLV("srlv"),
	SRAV("srav"),
	XOR("xor");

	private final String name;

	private RegisterInstruction(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
