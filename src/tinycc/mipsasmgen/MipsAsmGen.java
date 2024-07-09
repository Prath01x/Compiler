package tinycc.mipsasmgen;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import tinycc.util.Util;

/**
 * Helper class for the creation of MIPS code.
 */
public class MipsAsmGen {

    /**
     * A segment of the MIPS processor
     */
    private enum Segment {
        TEXT(".text"),
        DATA(".data");

        private final String name;

        private Segment(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final PrintStream out;
    private Segment currentSegment = Segment.TEXT;
    private int labelCounter = 0;

    /**
     * Initializes a new MIPS assembly generator
     *
     * @param out The output stream to use
     * @remarks The generator will write any output to the given output stream
     */
    public MipsAsmGen(final PrintStream out) {
        if (out == null) {
            throw new IllegalArgumentException();
        }
        this.out = out;
    }

    public MipsAsmGen(MipsAsmGen printStreamProvider) {
        this(printStreamProvider.out);
        if (!printStreamProvider.usedNames.isEmpty() || printStreamProvider.labelCounter != 0) {
            throw new IllegalStateException("Attempted to clone used MipsASMGen!");
        }
    }

    /**
     * Switches the segment to the given one.
     *
     * @param newSegment The new segment to switch to.
     */
    private void switchSegment(final Segment newSegment) {
        if (newSegment == null) {
            throw new IllegalArgumentException();
        }
        if (currentSegment != newSegment) {
            currentSegment = newSegment;
            out.printf("\t%s\n", newSegment);
        }
    }

    static final String privatePrefix = ".L";

    /**
     * Generates a new unique label name
     *
     * @return A unique label name
     */
    private String getNextLabelName() {
        return privatePrefix + labelCounter++;
    }

    /**
     * Generates a new unique label name with the given prefix.
     *
     * @param prefix The prefix to use for the label name.
     * @return A unique label name
     */
    private String getNextLabelName(final String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        return privatePrefix + prefix + '.' + labelCounter++;
    }

    private final Set<String> usedNames = new HashSet<String>();

    private void ensureUniqueLabel(final String name) {
        if (!usedNames.add(name)) {
            throw new IllegalArgumentException("duplicate label name '" + name + "'");
        }
    }

    private TextLabel makeTextLabel(final String name, final boolean externalVisible) {
        ensureUniqueLabel(name);
        return new TextLabel(name, externalVisible);
    }

    private DataLabel makeDataLabel(final String name, final boolean externalVisible) {
        ensureUniqueLabel(name);
        return new DataLabel(name, externalVisible);
    }

    /**
     * Creates a new label for the text segment with the given name.
     *
     * @param name The name of the label
     * @return A new TextLabel with the given name
     */
    public TextLabel makeTextLabel(final String name) {
        return makeTextLabel(name, true);
    }

    /**
     * Creates a new label for the text segment with a unique name.
     *
     * @return A new TextLabel with a unique name.
     */
    public TextLabel makeUniqueTextLabel() {
        return makeTextLabel(getNextLabelName(), false);
    }

    /**
     * Creates a new label for the text segment with a unique name using the given prefix.
     *
     * @param prefix The prefix to use for the label name.
     * @return A new DataLabel with a unique name.
     */
    public TextLabel makeUniqueTextLabel(final String prefix) {
        return makeTextLabel(getNextLabelName(prefix), false);
    }

    /**
     * Creates a new label for the data segment with the given name.
     *
     * @param name The name of the label
     * @return A new DataLabel with the given name
     */
    public DataLabel makeDataLabel(final String name) {
        return makeDataLabel(name, true);
    }

    /**
     * Creates a new label for the data segment with a unique name.
     *
     * @return A new DataLabel with a unique name.
     */
    public DataLabel makeUniqueDataLabel() {
        return makeDataLabel(getNextLabelName(), false);
    }

    /**
     * Creates a new label for the data segment with a unique name using the given prefix.
     *
     * @param prefix The prefix to use for the label name.
     * @return A new DataLabel with a unique name.
     */
    public DataLabel makeUniqueDataLabel(final String prefix) {
        return makeDataLabel(getNextLabelName(prefix), false);
    }

    private void emitLabel(final Segment segment, final Label l) {
        switchSegment(segment);
        if (l.isExternalVisible()) {
            out.printf("\t.globl %s\n", l);
        }
        out.printf("%s:\n", l);
    }

    /**
     * Emits the given DataLabel into the data segment.
     *
     * @param l The label to emit.
     */
    public void emitLabel(final DataLabel l) {
        emitLabel(Segment.DATA, l);
    }

    /**
     * Emits the given TextLabel into the text segment.
     *
     * @param l The label to emit.
     */
    public void emitLabel(final TextLabel l) {
        emitLabel(Segment.TEXT, l);
    }

    private final String insnFmt = "\t%-7s ";

    private void emit(final String fmt, final Object insn, final Object... args) {
        if (insn == null) {
            throw new IllegalArgumentException();
        }
        switchSegment(Segment.TEXT);
        out.printf(insnFmt, insn).printf(fmt, args).println();
    }

    /**
     * Emits a memory instruction. The effective address is the sum of the address of the label, the offset and value of the register.
     *
     * @param insn     The memory-instruction type to emit.
     * @param dest     The destination register to use.
     * @param label    The label to which the memory access is relative to (may be null).
     * @param offset   The offset of memory access.
     * @param register The address register for this memory access (may be null).
     * @remarks Emits into the text segment.
     */
    public void emitInstruction(final MemoryInstruction insn, final GPRegister dest, final Label label,
            final int offset, final GPRegister register) {
        if (insn == null || dest == null) {
            throw new IllegalArgumentException();
        }
        switchSegment(Segment.TEXT);
        out.printf(insnFmt + "%s, ", insn, dest);
        if (label != null) {
            out.print(label);
        }
        if (offset != 0 || (label == null && register == null)) {
            if (label != null) {
                out.print('+');
            }
            out.print(offset);
        }
        if (register != null) {
            out.printf("(%s)", register);
        }
        out.println();
    }

    /**
     * Emits a register instruction which operates on two registers and stores the result in a register.
     *
     * @param insn  The register-instruction type to emit.
     * @param dest  The destination register.
     * @param left  The register of the left operand.
     * @param right The register of the right operand.
     * @remarks Emits into the text segment.
     */
    public void emitInstruction(final RegisterInstruction insn, final GPRegister dest, final GPRegister left,
            final GPRegister right) {
        if (dest == null || left == null || right == null) {
            throw new IllegalArgumentException();
        }
        emit("%s, %s, %s", insn, dest, left, right);
    }

    /**
     * Emits a register instruction which operates on two registers and stores the result in a register. In this variant the left operand is the same
     * as the destination.
     *
     * @param insn  The register-instruction type to emit.
     * @param dest  The destination register.
     * @param right The register of the right operand.
     * @remarks Emits into the text segment.
     */
    public void emitInstruction(final RegisterInstruction insn, final GPRegister dest, final GPRegister right) {
        emitInstruction(insn, dest, dest, right);
    }

    /**
     * Emits an instruction which takes an additional immediate constant.
     *
     * @param insn The instruction type to emit.
     * @param dest The destination register.
     * @param left The register of the left operand (must be ZERO for LUI).
     * @param imm  The immediate constant to use. This must be in range for the used instruction.
     * @remarks Emits into the text segment.
     */
    public void emitInstruction(final ImmediateInstruction insn, final GPRegister dest, final GPRegister left,
            final int imm) {
        if (dest == null || left == null || !insn.getRange().inRange(imm)) {
            throw new IllegalArgumentException();
        }
        if (insn == ImmediateInstruction.LUI) {
            if (left != GPRegister.ZERO) {
                throw new IllegalArgumentException();
            }
            emit("%s, %d", insn, dest, imm);
        } else {
            emit("%s, %s, %d", insn, dest, left, imm);
        }
    }

    /**
     * Emits an instruction which takes an additional immediate constant. In this variant the left operand is the same as the destination, except for
     * LUI, which uses ZERO.
     *
     * @param insn The instruction type to emit.
     * @param dest The destination register.
     * @param imm  The immediate constant to use. This must be in range for the used instruction.
     * @remarks Emits into the text segment.
     */
    public void emitInstruction(final ImmediateInstruction insn, final GPRegister dest, final int imm) {
        final GPRegister left = insn == ImmediateInstruction.LUI ? GPRegister.ZERO : dest;
        emitInstruction(insn, dest, left, imm);
    }

    /**
     * Emits a branch instruction.
     *
     * @param insn   The branch-instruction type to emit.
     * @param left   The left operand for a conditional branch.
     * @param right  The right operand for a conditional branch (BEQ and BNE). Must be ZERO for all other branch-instruction types.
     * @param target The target text label to jump to.
     * @remarks Emits into the text segment.
     */
    public void emitInstruction(final BranchInstruction insn, final GPRegister left, final GPRegister right,
            final TextLabel target) {
        if (left == null || right == null || target == null) {
            throw new IllegalArgumentException();
        }
        switch (insn) {
            case BEQ:
            case BNE:
                emit("%s, %s, %s", insn, left, right, target);
                break;

            default:
                if (right != GPRegister.ZERO) {
                    throw new IllegalArgumentException();
                }
                emit("%s, %s", insn, left, target);
                break;
        }
    }

    /**
     * Emits a branch instruction where the right comparison operand is ZERO.
     *
     * @param insn   The branch-instruction type to emit.
     * @param left   The left operand for a conditional branch.
     * @param target The target text label to jump to.
     * @remarks Emits into the text segment.
     */
    public void emitInstruction(final BranchInstruction insn, final GPRegister left, final TextLabel target) {
        emitInstruction(insn, left, GPRegister.ZERO, target);
    }

    /**
     * Emits a jump instruction.
     *
     * @param insn   The jump-instruction type to emit.
     * @param target The target text label to jump to.
     * @remarks Emits into the text segment.
     */
    public void emitInstruction(final JumpInstruction insn, final TextLabel target) {
        if (target == null) {
            throw new IllegalArgumentException();
        }
        emit("%s", insn, target);
    }

    /**
     * Emits a jump-to-register instruction.
     *
     * @param insn   The jump-to-register-instruction type to use.
     * @param target The register which contains the target address.
     * @remarks Emits into the text segment.
     */
    public void emitInstruction(final JumpRegisterInstruction insn, final GPRegister target) {
        if (target == null) {
            throw new IllegalArgumentException();
        }
        emit("%s", insn, target);
    }

    /**
     * Emits a special instruction which takes two operands.
     *
     * @param insn  The special-instruction type to emit.
     * @param left  The register of the left operand.
     * @param right The register of the right operand.
     * @remarks Emits into the text segment.
     */
    public void emitInstruction(final SpecialInstruction insn, final GPRegister left, final GPRegister right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException();
        }
        emit("%s, %s", insn, left, right);
    }

    /**
     * Emits a special instruction which takes one operand.
     *
     * @param insn     The special-instruction type to emit.
     * @param register The register of operand.
     * @remarks Emits into the text segment.
     */
    public void emitInstruction(final SpecialRegisterInstruction insn, final GPRegister register) {
        if (register == null) {
            throw new IllegalArgumentException();
        }
        emit("%s", insn, register);
    }

    /**
     * Emits a string constant (null terminated) into the data segment.
     *
     * @param string The string constant to emit.
     * @remarks Emits into the data segment.
     */
    public void emitASCIIZ(final String string) {
        switchSegment(Segment.DATA);
        out.print("\t.asciiz ");
        out.println(Util.escapeString(string, '"'));
    }

    /**
     * Emits the given label and a string constant (null terminated): LABEL: .asciiz "hi"
     *
     * @param label  The label to use.
     * @param string The string constant to emit.
     * @remarks Emits into the data segment.
     */
    public void emitASCIIZ(final DataLabel label, final String string) {
        emitLabel(label);
        emitASCIIZ(string);
    }

    /**
     * Emits a byte constant into the data segment.
     *
     * @param value The byte constant to emit.
     * @remarks Emits into the data segment.
     */
    public void emitByte(final byte value) {
        switchSegment(Segment.DATA);
        out.printf("\t.byte %d\n", value);
    }

    /**
     * Emits the given label and a byte constant: LABEL: .byte 42
     *
     * @param label  The label to use.
     * @param string The byte constant to emit.
     * @remarks Emits into the data segment.
     */
    public void emitByte(final DataLabel label, final byte value) {
        emitLabel(label);
        emitByte(value);
    }

    /**
     * Emits a half constant into the data segment.
     *
     * @param value The half constant to emit.
     * @remarks Emits into the data segment.
     */
    public void emitHalf(final short value) {
        switchSegment(Segment.DATA);
        out.printf("\t.half %d\n", value);
    }

    /**
     * Emits the given label and a half constant: LABEL: .half 42
     *
     * @param label  The label to use.
     * @param string The half constant to emit.
     * @remarks Emits into the data segment.
     */
    public void emitHalf(final DataLabel label, final short value) {
        emitLabel(label);
        emitHalf(value);
    }

    /**
     * Emits a word constant into the data segment.
     *
     * @param value The word constant to emit.
     * @remarks Emits into the data segment.
     */
    public void emitWord(final int value) {
        switchSegment(Segment.DATA);
        out.printf("\t.word %d\n", value);
    }

    /**
     * Emits the given label and a word constant: LABEL: .word 42
     *
     * @param label  The label to use.
     * @param string The word constant to emit.
     * @remarks Emits into the data segment.
     */
    public void emitWord(final DataLabel label, final int value) {
        emitLabel(label);
        emitWord(value);
    }
}
