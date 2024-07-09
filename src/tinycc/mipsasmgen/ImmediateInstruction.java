package tinycc.mipsasmgen;

/**
 * Represents an instruction of the MIPS processor which takes an additional
 * immediate constant.
 *
 * It is used by the MipsAsmGenerator.
 *
 * @see MipsAsmGenerator
 */
public enum ImmediateInstruction {
	ADDI ("addi",  ImmediateRange.SIGNED16),
	ADDIU("addiu", ImmediateRange.SIGNED16),
	ANDI ("andi",  ImmediateRange.UNSIGNED16),
	LUI  ("lui",   ImmediateRange.UNSIGNED16),
	ORI  ("ori",   ImmediateRange.UNSIGNED16),
	SLL  ("sll",   ImmediateRange.UNSIGNED5),
	SLTI ("slti",  ImmediateRange.SIGNED16),
	SLTIU("sltiu", ImmediateRange.SIGNED16),
	SRA  ("sra",   ImmediateRange.UNSIGNED5),
	SRL  ("srl",   ImmediateRange.UNSIGNED5),
	XORI ("xori",  ImmediateRange.UNSIGNED16);

	private final String name;
	private final ImmediateRange range;

	private ImmediateInstruction(final String name, final ImmediateRange range) {
		this.name = name;
		this.range = range;
	}

	@Override
	public String toString() {
		return name;
	}

	public ImmediateRange getRange() {
		return range;
	}
}
