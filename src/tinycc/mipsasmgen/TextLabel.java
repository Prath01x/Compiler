package tinycc.mipsasmgen;

/**
 * A text label for the code segment of an emitted MIPS program.
 *
 * @see MipsAsmGenerator
 */
public class TextLabel extends Label {
	/**
	 * Initializes a new text label
	 *
	 * @param name            The name of the label Can be an automatically
	 *                        generated name or a specified one.
	 * @param externalVisible The label is visible to other translation units.
	 * @see MipsAsmGenerator
	 */
	TextLabel(final String name, final boolean externalVisible) {
		super(name, externalVisible);
	}
}
