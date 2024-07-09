package tinycc.mipsasmgen;

/**
 * A data label for the data segment of an emitted MIPS program.
 *
 * @see MipsAsmGenerator
 */
public class DataLabel extends Label {
	/**
	 * Initializes a new data label
	 *
	 * @param name            The name of the label Can be an automatically
	 *                        generated name or a specified one.
	 * @param externalVisible The label is visible to other translation units.
	 * @see MipsAsmGenerator
	 */
	DataLabel(final String name, final boolean externalVisible) {
		super(name, externalVisible);
	}
}
